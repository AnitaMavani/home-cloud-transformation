import { createRouter, createWebHistory } from 'vue-router';
import Login from '../components/Login.vue';
import Main from '../components/Main.vue';
import Uploader from "../components/Uploader.vue";
const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/main',
    name: 'Main',
    component: Main,
  },
  {
    path: '/Uploader',
    name: 'Uploader',
    component: Uploader
  },

];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes
});



export default router;
