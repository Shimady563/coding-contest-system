import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue';
import Login from "../views/Login.vue";
import Register from "../views/Register.vue";
import Profile from "../views/Profile.vue";
import Tests from "../views/StudentTests.vue"; 
import TeacherManage from '../views/TeacherManage.vue';
import TeacherCreateTest from '../views/TeacherCreateTest.vue';

const routes = [
  { path: "/", component: HomeView },
  { path: "/login", component: Login },
  { path: "/register", component: Register },
  { path: "/profile", component: Profile },
  { path: "/tests", component: Tests },
  { path: '/manage-tests', component: TeacherManage },
  { path: '/create-test', component: TeacherCreateTest }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router;