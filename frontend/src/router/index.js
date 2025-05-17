import { createRouter, createWebHistory } from 'vue-router'
import { getUserInfo, getAccessToken } from '@/js/auth'

import HomeView from '../views/HomeView.vue';
import Login from "../views/Login.vue";
import Register from "../views/Register.vue";
import Profile from "../views/Profile.vue";
import Tests from "../views/StudentContest.vue";
import TeacherManage from '../views/TeacherManage.vue';
import CreateTest from '../views/CreateTest.vue';
import CreateTask from '@/views/CreateTask.vue';
import StudentContestsPage from '@/views/StudentContestsPage.vue';
import EditContest from '@/views/EditContest.vue';
import ContestVersionsPage from '@/views/ContestVersionsPage.vue';
import TasksPage from '@/views/TasksPage.vue';
import StudentContest from '../views/StudentContest.vue';
import ManageStudents from '@/views/ManageStudents.vue';

const routes = [
  { path: "/", component: HomeView },
  { path: "/login", component: Login, meta: { requiresUnauth: true } }, // Добавляем мета-поле
  { path: "/register", component: Register, meta: { requiresUnauth: true } }, // Добавляем мета-поле
  { path: "/profile", component: Profile, meta: { requiresAuth: true } },
  { path: "/contest", component: Tests },
  { path: '/manage-contests', component: TeacherManage, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: '/create-contest', component: CreateTest, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: '/create-task', component: CreateTask, meta: { requiresAuth: true, requiresTeacher: true }},
  { path: '/solve-contest', component: StudentContestsPage},
  { path: '/edit-contest/:id', component: EditContest, props: true, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: '/contest/:id', component: ContestVersionsPage},
  { path: '/contest-version/:id/tasks', component: TasksPage},
  { path: '/task/:taskId', component: StudentContest, name: 'StudentContest' },
  { path: '/manage-students', component: ManageStudents, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: '/access-denied', component: () => import('@/views/AccessDenied.vue') }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach(async (to, from, next) => {
  // Получаем токен и информацию о пользователе
  const token = getAccessToken();
  const user = token ? await getUserInfo() : null; // Получаем информацию только если есть токен

  // Страницы, доступные только для неавторизованных
  if (to.meta.requiresUnauth) {
    if (token) {
      // Если пользователь авторизован, перенаправляем на главную
      return next('/');
    } else {
      // Разрешаем доступ к страницам входа/регистрации
      return next();
    }
  }

  // Страницы, требующие авторизации
  if (to.meta.requiresAuth) {
    if (!token) {
      // Перенаправляем на страницу входа, если нет токена
      return next('/login');
    }

    // Проверка роли для преподавательских страниц
    if (to.meta.requiresTeacher && user?.role !== 'teacher') {
      return next('/access-denied');
    }
  }

  // Разрешаем доступ ко всем остальным страницам
  next();
});

export default router;
