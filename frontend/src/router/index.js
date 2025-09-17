import { createRouter, createWebHistory } from 'vue-router'
import { getUserInfo } from '@/js/auth'

import HomeView from '../views/HomeView.vue';
import Login from "../views/Login.vue";
import Register from "../views/Register.vue";
import Profile from "../views/Profile.vue";
import Tests from "../views/StudentContest.vue";
import TeacherManage from '../views/TeacherManage.vue';
import ContestForm from '../views/ContestForm.vue';
import TaskForm from '@/views/TaskForm.vue';
import StudentContestsPage from '@/views/StudentContestsPage.vue';
import ContestVersionsPage from '@/views/ContestVersionsPage.vue';
import TasksPage from '@/views/TasksPage.vue';
import StudentContest from '../views/StudentContest.vue';
import ManageStudents from '@/views/ManageStudents.vue';
import ManageGroups from '@/views/ManageGroups.vue';

const routes = [
  { path: "/", component: HomeView },
  { path: "/login", component: Login, meta: { requiresUnauth: true } }, 
  { path: "/register", component: Register, meta: { requiresUnauth: true } }, 
  { path: "/profile", component: Profile, meta: { requiresAuth: true } },
  { path: "/contest", component: Tests },
  { path: '/manage-contests', component: TeacherManage, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: '/create-contest', component: ContestForm, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: '/create-task', component: TaskForm, meta: { requiresAuth: true, requiresTeacher: true }},
  { path: '/solve-contest', component: StudentContestsPage},
  { path: '/edit-contest/:id', component: ContestForm, props: true, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: '/edit-task/:id', component: TaskForm, props: true, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: '/contest/:contestId', component: ContestVersionsPage, meta: { requiresAuth: true }  },
  { path: '/contest/:contestId/contest-version/:versionId', component: TasksPage },
  { path: '/contest/:contestId/contest-version/:versionId/task/:taskId', component: StudentContest, name: 'StudentContest' },
  { path: '/manage-students', component: ManageStudents, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: '/manage-groups', component: ManageGroups, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: '/access-denied', component: () => import('@/views/AccessDenied.vue') },
  { path: '/access-denied-contest', name: 'AccessDenied', component: () => import('@/views/AccessDeniedPage.vue') },
  { path: '/student-results', component: () => import('@/views/StudentSolutionsPage.vue'),
  }  
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

router.beforeEach(async (to, from, next) => {
  const user = await getUserInfo();

  if (to.meta.requiresUnauth) {
    if (user) return next('/');
    return next();
  }

  if (to.meta.requiresAuth) {
    if (!user) return next('/login');

    if (to.meta.requiresTeacher && user?.role !== 'teacher') {
      return next('/access-denied');
    }
  }

  next();
});

export default router;
