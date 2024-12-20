import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import VueParticles from 'vue-particles';
import Request from '@/components/Request';
import '@/assets/base.scss';
import 'font-awesome/css/font-awesome.min.css';
import HljsVuePlugin from '@highlightjs/vue-plugin'
import "highlight.js/styles/atom-one-light.css";
import 'highlight.js/lib/common'
import Table from '@/components/Table.vue'
// 首先调用 loadStoredUserData
store.dispatch('loadStoredUserData').then(() => {
  // 然后创建 Vue 应用实例
  const app = createApp(App);

  // 配置全局属性
  app.config.globalProperties.Request = Request;
  app.config.globalProperties.globalInfo = {
    avatarUrl: "/api/getAvatar/",
    imageUrl: "/api/file/getFile/"
  }

  // 使用插件和中间件
  app.use(ElementPlus);
  app.use(VueParticles);
  app.use(HljsVuePlugin);
  app.use(store);
  app.use(router);

  app.component("Table", Table);

  // 最后挂载应用
  app.mount('#app');
});

// debounce function 和 ResizeObserver 类的代码可以保留在这里
const debounce = (fn, delay) => {
  let timer = null;
  return function () {
    let context = this;
    let args = arguments;
    clearTimeout(timer);
    timer = setTimeout(function () {
      fn.apply(context, args);
    }, delay);
  }
}

const _ResizeObserver = window.ResizeObserver;
window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
  constructor(callback) {
    callback = debounce(callback, 16);
    super(callback);
  }
}
