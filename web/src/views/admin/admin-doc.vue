<template>
  <a-layout>
    <a-layout-content
      :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <a-row :gutter="24">
        <a-col :span="8">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleQuery()">
                  Search
                </a-button>
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="add()">
                  Add
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-table
            v-if="level1.length > 0"
            :columns="columns"
            :row-key="record => record.id"
            :data-source="level1"
            :loading="loading"
            :pagination="false"
            size="small"
            :defaultExpandAllRows="true"
          >
            <template #name="{ text, record }">
              {{record.sort}} {{text}}
            </template>
            <template v-slot:action="{ text, record }">
              <a-space size="small">
                <a-button type="primary" @click="edit(record)" size="small">
                  Edit
                </a-button>
                <a-popconfirm
                  title="Deletion is irreversible. Are you sure you want to delete?"
                  ok-text="Yes"
                  cancel-text="No"
                  @confirm="handleDelete(record.id)"
                >
                  <a-button type="danger" size="small">
                    Delete
                  </a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-form layout="inline" :model="param">
              <a-form-item>
                <a-button type="primary" @click="handleSave()">
                  Save
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-form :model="doc" layout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="名称"/>
            </a-form-item>
            <a-form-item>
              <a-tree-select
                v-model:value="doc.parent"
                style="width: 100%"
                :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                :tree-data="treeSelectData"
                placeholder="Please select a parent document"
                tree-default-expand-all
                :replaceFields="{title: 'name', key: 'id', value: 'id'}"
              >
              </a-tree-select>
            </a-form-item>
            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="Sort"/>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handlePreviewContent()">
                <EyeOutlined /> Content Preview
              </a-button>
            </a-form-item>
            <a-form-item>
              <div id="content"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>

      <a-drawer width="900" placement="right" :closable="false" :visible="drawerVisible" @close="onDrawerClose">
        <div class="wangeditor" :innerHTML="previewHtml"></div>
      </a-drawer>

    </a-layout-content>
  </a-layout>

  <!--<a-modal-->
  <!--  title="Document Form"-->
  <!--  v-model:visible="modalVisible"-->
  <!--  :confirm-loading="modalLoading"-->
  <!--  @ok="handleModalOk"-->
  <!--&gt;-->
  <!--  -->
  <!--</a-modal>-->
</template>

<script lang="ts">
  import { defineComponent, onMounted, ref, createVNode } from 'vue';
  import axios from 'axios';
  import {message, Modal} from 'ant-design-vue';
  import {Tool} from "@/util/tool";
  import {useRoute} from "vue-router";
  import ExclamationCircleOutlined from "@ant-design/icons-vue/ExclamationCircleOutlined";
  import E from 'wangeditor'

  export default defineComponent({
    name: 'AdminDoc',
    setup() {
      const route = useRoute();
      console.log("路由：", route);
      console.log("route.path：", route.path);
      console.log("route.query：", route.query);
      console.log("route.param：", route.params);
      console.log("route.fullPath：", route.fullPath);
      console.log("route.name：", route.name);
      console.log("route.meta：", route.meta);
      const param = ref();
      param.value = {};
      const docs = ref();
      const loading = ref(false);
      // Because the status of the properties in the tree selection component changes with the currently edited node, a separate reactive variable should be declared.
      const treeSelectData = ref();
      treeSelectData.value = [];

      const columns = [
        {
          title: 'Name',
          dataIndex: 'name',
          slots: { customRender: 'name' }
        },
        {
          title: 'Action',
          key: 'action',
          slots: { customRender: 'action' }
        }
      ];

      /**
       * Level 1 document tree, where the 'children' attribute represents the level 2 documents.
       * [{
       *   id: "",
       *   name: "",
       *   children: [{
       *     id: "",
       *     name: "",
       *   }]
       * }]
       */
      const level1 = ref(); // Level 1 document tree, where the 'children' attribute represents the level 2 documents.
      level1.value = [];

      /**
       * Data Query
       **/
      const handleQuery = () => {
        loading.value = true;
        // If the existing data is not cleared, after editing and saving, then reloading the data and clicking on the edit button again, the list will still display the old data from before editing.
        level1.value = [];
        axios.get("/doc/all/" + route.query.ebookId).then((response) => {
          loading.value = false;
          const data = response.data;
          if (data.success) {
            docs.value = data.content;
            console.log("Original Array: ", docs.value);

            level1.value = [];
            level1.value = Tool.array2Tree(docs.value, 0);
            console.log("Tree Structure: ", level1);

            // Initialize the parent document drop-down list, equivalent to clicking 'Add New'
            treeSelectData.value = Tool.copy(level1.value) || [];
            // Add a 'None' option to the selection tree
            treeSelectData.value.unshift({id: 0, name: 'None'});
          } else {
            message.error(data.message);
          }
        });
      };

      // -------- Form ---------
      const doc = ref();
      doc.value = {
        ebookId: route.query.ebookId
      };
      const modalVisible = ref(false);
      const modalLoading = ref(false);
      const editor = new E('#content');
      editor.config.zIndex = 0;
      // Display the upload image button, convert it to Base64 for storage, and also support dragging and dropping images
      // Upload image: https://doc.wangeditor.com/pages/07-%E4%B8%8A%E4%BC%A0%E5%9B%BE%E7%89%87/01-%E9%85%8D%E7%BD%AE%E6%9C%8D%E5%8A%A1%E7%AB%AF%E6%8E%A5%E5%8F%A3.html
      // Upload video: https://doc.wangeditor.com/pages/07-%E4%B8%8A%E4%BC%A0%E8%A7%86%E9%A2%91/01-%E9%85%8D%E7%BD%AE%E6%9C%8D%E5%8A%A1%E7%AB%AF%E6%8E%A5%E5%8F%A3.html
      editor.config.uploadImgShowBase64 = true;

      const handleSave = () => {
        modalLoading.value = true;
        doc.value.content = editor.txt.html();
        axios.post("/doc/save", doc.value).then((response) => {
          modalLoading.value = false;
          const data = response.data; // data = commonResp
          if (data.success) {
            // modalVisible.value = false;
            message.success("Save success!");

            // Reload the List
            handleQuery();
          } else {
            message.error(data.message);
          }
        });
      };

      /**
       * Set a node and all its descendant nodes to 'disabled'
       */
      const setDisable = (treeSelectData: any, id: any) => {
        // console.log(treeSelectData, id);
        // Traverse the array, that is, traverse a certain level of nodes.
        for (let i = 0; i < treeSelectData.length; i++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // If the current node is the target node
            console.log("disabled", node);
            // Set the target node to 'disabled'
            node.disabled = true;

            // Traverse all child nodes and set them all to 'disabled'
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              for (let j = 0; j < children.length; j++) {
                setDisable(children, children[j].id)
              }
            }
          } else {
            // If the current node is not the target node, then look for it in its child nodes.
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              setDisable(children, id);
            }
          }
        }
      };

      const deleteIds: Array<string> = [];
      const deleteNames: Array<string> = [];
      /**
       * Search the entire branch of the tree
       */
      const getDeleteIds = (treeSelectData: any, id: any) => {
        // console.log(treeSelectData, id);
        // Traverse the array, that is, traverse a certain level of nodes.
        for (let i = 0; i < treeSelectData.length; i++) {
          const node = treeSelectData[i];
          if (node.id === id) {
            // If the current node is the target node
            console.log("delete", node);
            // Add the target ID to the result set 'ids'
            // node.disabled = true;
            deleteIds.push(id);
            deleteNames.push(node.name);

            // Traverse all child nodes
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              for (let j = 0; j < children.length; j++) {
                getDeleteIds(children, children[j].id)
              }
            }
          } else {
            // If the current node is not the target node, then look for it in its child nodes.
            const children = node.children;
            if (Tool.isNotEmpty(children)) {
              getDeleteIds(children, id);
            }
          }
        }
      };

      /**
       * Content Query
       **/
      const handleQueryContent = () => {
        axios.get("/doc/find-content/" + doc.value.id).then((response) => {
          const data = response.data;
          if (data.success) {
            editor.txt.html(data.content)
          } else {
            message.error(data.message);
          }
        });
      };

      /**
       * Edit
       */
      const edit = (record: any) => {
        // Clear the rich text box
        editor.txt.html("");
        modalVisible.value = true;
        doc.value = Tool.copy(record);
        handleQueryContent();

        // You cannot select the current node and all its descendant nodes as the parent node, as this would break the tree
        treeSelectData.value = Tool.copy(level1.value);
        setDisable(treeSelectData.value, record.id);

        // Add a 'None' option to the selection tree
        treeSelectData.value.unshift({id: 0, name: 'None'});
      };

      /**
       * Add
       */
      const add = () => {
        // Clear the rich text box
        editor.txt.html("");
        modalVisible.value = true;
        doc.value = {
          ebookId: route.query.ebookId
        };

        treeSelectData.value = Tool.copy(level1.value) || [];

        // Add a 'None' option to the selection tree
        treeSelectData.value.unshift({id: 0, name: 'None'});
      };

      const handleDelete = (id: number) => {
        // console.log(level1, level1.value, id)
        // Clear the array, otherwise it will keep growing when deleting multiple times
        deleteIds.length = 0;
        deleteNames.length = 0;
        getDeleteIds(level1.value, id);
        Modal.confirm({
          title: 'Important Reminder',
          icon: createVNode(ExclamationCircleOutlined),
          content: 'To be Deleted: [' + deleteNames.join(", ") + "] Deletion is irreversible. Are you sure you want to delete?",
          onOk() {
            // console.log(ids)
            axios.delete("/doc/delete/" + deleteIds.join(",")).then((response) => {
              const data = response.data; // data = commonResp
              if (data.success) {
                // Reload the List
                handleQuery();
              } else {
                message.error(data.message);
              }
            });
          },
        });
      };

      // ----------------Rich Text Preview--------------
      const drawerVisible = ref(false);
      const previewHtml = ref();
      const handlePreviewContent = () => {
        const html = editor.txt.html();
        previewHtml.value = html;
        drawerVisible.value = true;
      };
      const onDrawerClose = () => {
        drawerVisible.value = false;
      };

      onMounted(() => {
        handleQuery();

        editor.create();
      });

      return {
        param,
        // docs,
        level1,
        columns,
        loading,
        handleQuery,

        edit,
        add,

        doc,
        modalVisible,
        modalLoading,
        handleSave,

        handleDelete,

        treeSelectData,

        drawerVisible,
        previewHtml,
        handlePreviewContent,
        onDrawerClose,
      }
    }
  });
</script>

<style scoped>
  img {
    width: 50px;
    height: 50px;
  }
</style>
