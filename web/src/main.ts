import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';
import * as Icons from '@ant-design/icons-vue';
import axios from 'axios';
import {Tool} from "@/util/tool";
import { message } from 'ant-design-vue';

axios.defaults.baseURL = process.env.VUE_APP_SERVER;

/**
 * Axios interceptor
 */
axios.interceptors.request.use(function (config) {
  console.log('Request parameters:', config);
  const token = store.state.user.token;
  if (Tool.isNotEmpty(token)) {
    config.headers.token = token;
    console.log("Add token to request headers:", token);
  }
  return config;
}, error => {
  return Promise.reject(error);
});
axios.interceptors.response.use(function (response) {
  console.log('Return results: ', response);
  return response;
}, error => {
  console.log('Return error:', error);
  const response = error.response;
  const status = response.status;
  if (status === 401) {
    // If the status code is 401, redirect to the homepage or the login page
    console.log("If not logged in, redirect to the homepage");
    store.commit("setUser", {});
    message.error("If not logged in or login session has expired");
    router.push('/');
  }
  return Promise.reject(error);
});

const app = createApp(App);
app.use(store).use(router).use(Antd).mount('#app');

// Use icons globally
const icons: any = Icons;
for (const i in icons) {
  app.component(i, icons[i]);
}

console.log('Environment: ', process.env.NODE_ENV);
console.log('Server: ', process.env.VUE_APP_SERVER);
