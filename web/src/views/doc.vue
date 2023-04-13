<template>
  <a-layout>
    <a-layout-content :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }">
      <h3 v-if="level1.length === 0">对不起，找不到相关文档！</h3>
      <a-row>
        <a-col :span="6">
          <a-tree
            v-if="level1.length > 0"
            :tree-data="level1"
            @select="onSelect"
            :replaceFields="{title: 'name', key: 'id', value: 'id'}"
            :defaultExpandAll="true"
            :defaultSelectedKeys="defaultSelectedKeys"
          >
          </a-tree>
        </a-col>
        <a-col :span="18">
          <div>
            <h2>{{doc.name}}</h2>
            <div>
              <span>Number of views: {{doc.viewCount}}</span> &nbsp; &nbsp;
              <span>Number of likes: {{doc.voteCount}}</span>
            </div>
            <a-divider style="height: 2px; background-color: #9999cc"/>
          </div>
          <div class="wangeditor" :innerHTML="html"></div>
          <div class="vote-div">
            <a-button type="primary" shape="round" :size="'large'" @click="vote">
              <template #icon><LikeOutlined /> &nbsp;Like: {{doc.voteCount}} </template>
            </a-button>
          </div>
        </a-col>
      </a-row>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts">
  import { defineComponent, onMounted, ref, createVNode } from 'vue';
  import axios from 'axios';
  import {message} from 'ant-design-vue';
  import {Tool} from "@/util/tool";
  import {useRoute} from "vue-router";

  export default defineComponent({
    name: 'Doc',
    setup() {
      const route = useRoute();
      const docs = ref();
      const html = ref();
      const defaultSelectedKeys = ref();
      defaultSelectedKeys.value = [];
      // The currently selected document
      const doc = ref();
      doc.value = {};

      /**
       * Top-level document tree, where the 'children' attribute represents the second-level documents
       * [{
       *   id: "",
       *   name: "",
       *   children: [{
       *     id: "",
       *     name: "",
       *   }]
       * }]
       */
      const level1 = ref(); // Top-level document tree, where the 'children' attribute represents the second-level documents
      level1.value = [];

      /**
       * Content search
       **/
      const handleQueryContent = (id: number) => {
        axios.get("/doc/find-content/" + id).then((response) => {
          const data = response.data;
          if (data.success) {
            html.value = data.content;
          } else {
            message.error(data.message);
          }
        });
      };

      /**
       * Data query
       **/
      const handleQuery = () => {
        axios.get("/doc/all/" + route.query.ebookId).then((response) => {
          const data = response.data;
          if (data.success) {
            docs.value = data.content;

            level1.value = [];
            level1.value = Tool.array2Tree(docs.value, 0);

            if (Tool.isNotEmpty(level1)) {
              defaultSelectedKeys.value = [level1.value[0].id];
              handleQueryContent(level1.value[0].id);
              // Display document information initially
              doc.value = level1.value[0];
            }
          } else {
            message.error(data.message);
          }
        });
      };

      const onSelect = (selectedKeys: any, info: any) => {
        console.log('selected', selectedKeys, info);
        if (Tool.isNotEmpty(selectedKeys)) {
          // When a certain node is selected, load the document information for that node
          doc.value = info.selectedNodes[0].props;
          // Load content
          handleQueryContent(selectedKeys[0]);
        }
      };

      // Like
      const vote = () => {
        axios.get('/doc/vote/' + doc.value.id).then((response) => {
          const data = response.data;
          if (data.success) {
            doc.value.voteCount++;
          } else {
            message.error(data.message);
          }
        });
      };

      onMounted(() => {
        handleQuery();
      });

      return {
        level1,
        html,
        onSelect,
        defaultSelectedKeys,
        doc,
        vote
      }
    }
  });
</script>

<style>
  /* table style */
  .wangeditor table {
    border-top: 1px solid #ccc;
    border-left: 1px solid #ccc;
  }
  .wangeditor table td,
  .wangeditor table th {
    border-bottom: 1px solid #ccc;
    border-right: 1px solid #ccc;
    padding: 3px 5px;
  }
  .wangeditor table th {
    border-bottom: 2px solid #ccc;
    text-align: center;
  }

  /* blockquote style */
  .wangeditor blockquote {
    display: block;
    border-left: 8px solid #d0e5f2;
    padding: 5px 10px;
    margin: 10px 0;
    line-height: 1.4;
    font-size: 100%;
    background-color: #f1f1f1;
  }

  /* code style */
  .wangeditor code {
    display: inline-block;
    *display: inline;
    *zoom: 1;
    background-color: #f1f1f1;
    border-radius: 3px;
    padding: 3px 5px;
    margin: 0 3px;
  }
  .wangeditor pre code {
    display: block;
  }

  /* ul ol style */
  .wangeditor ul, ol {
    margin: 10px 0 10px 20px;
  }

  .wangeditor blockquote p {
    font-family:"YouYuan";
    margin: 20px 10px !important;
    font-size: 16px !important;
    font-weight:600;
  }

  /* like */
  .vote-div {
    padding: 15px;
    text-align: center;
  }

  /* Image auto-adjustment */
  .wangeditor img {
    max-width: 100%;
    height: auto;
  }

  /* Video auto-adjustment */
  .wangeditor iframe {
    width: 100%;
    height: 400px;
  }
</style>
