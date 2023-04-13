<template>
  <a-layout-footer style="text-align: center">
    Kaiyu Personal Wiki<span v-show="user.id">, welcome: {{user.name}}</span>
  </a-layout-footer>
</template>

<script lang="ts">
  import { defineComponent, computed, onMounted } from 'vue';
  import store from "@/store";
  import {Tool} from "@/util/tool";
  import { notification } from 'ant-design-vue';

  export default defineComponent({
    name: 'the-footer',
    setup() {
      const user = computed(() => store.state.user);

      let websocket: any;
      let token: any;
      const onOpen = () => {
        console.log('The WebSocket connection is successful, status code:', websocket.readyState)
      };
      const onMessage = (event: any) => {
        console.log('The WebSocket has received a message:', event.data);
        notification['info']({
          message: 'Message Received',
          description: event.data,
        });
      };
      const onError = () => {
        console.log('The WebSocket connection encountered an error, status code:', websocket.readyState)
      };
      const onClose = () => {
        console.log('The WebSocket connection has been closed, status code:', websocket.readyState)
      };
      const initWebSocket = () => {
        // Connection Success
        websocket.onopen = onOpen;
        // Callback for Received Message
        websocket.onmessage = onMessage;
        // Connection Error
        websocket.onerror = onError;
        // Callback for Connection Closed
        websocket.onclose = onClose;
      };

      onMounted(() => {
        // WebSocket
        if ('WebSocket' in window) {
          token = Tool.uuid(10);
          // Connection Address: ws://127.0.0.1:8880/ws/xxx
          websocket = new WebSocket(process.env.VUE_APP_WS_SERVER + '/ws/' + token);
          initWebSocket()

          // close
          // websocket.close();
        } else {
          alert('Current browser does not support it.')
        }
      });

      return {
        user
      }
    }
  });
</script>
