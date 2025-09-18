import { createRouter, createWebHistory } from 'vue-router'
import { getUserInfo } from '@/js/auth'

import HomeView from '../views/HomeView.vue';
import Login from "../views/LoginPage.vue";
import Registration from "../views/RegistrationPage.vue";
import Profile from "../views/ProfilePage.vue";

import ContestsPage from '@/views/ContestsPage.vue';
import ContestVersions from '@/views/ContestVersions.vue';
import ContestTasks from '@/views/ContestTasks.vue';
import ContestSolving from '@/views/ContestSolving.vue';
import ContestForm from '../views/ContestForm.vue';
import TaskForm from '@/views/TaskForm.vue';

import ManageTasksContests from '../views/ManageTasksContests.vue';
import ManageStudents from '@/views/ManageStudents.vue';
import ManageGroups from '@/views/ManageGroups.vue';
import ManageSolutions from '@/views/ManageSolutions.vue';

import AccessDeniedTime from '@/views/AccessDeniedTimePage.vue';
import AccessDeniedRole from '@/views/AccessDeniedRolePage.vue';

const routes = [
  { path: "/", component: HomeView },
  { path: "/login", component: Login, meta: { requiresUnauth: true } },
  { path: "/register", component: Registration, meta: { requiresUnauth: true } },
  { path: "/profile", component: Profile, meta: { requiresAuth: true } },

  { path: "/contests", component: ContestsPage },
  { path: "/contests/:contestId", component: ContestVersions, meta: { requiresAuth: true } },
  { path: "/contests/:contestId/contest-version/:versionId", component: ContestTasks },
  { path: "/contests/:contestId/contest-version/:versionId/task/:taskId", component: ContestSolving, name: "ContestSolving" },

  { path: "/manage-contests", component: ManageTasksContests, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: "/manage-contests/create-contest", component: ContestForm, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: "/manage-contests/create-task", component: TaskForm, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: "/manage-contests/edit-contest/:id", component: ContestForm, props: true, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: "/manage-contests/edit-task/:id", component: TaskForm, props: true, meta: { requiresAuth: true, requiresTeacher: true } },

  { path: "/manage-students", component: ManageStudents, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: "/manage-groups", component: ManageGroups, meta: { requiresAuth: true, requiresTeacher: true } },
  { path: "/manage-solutions", component: ManageSolutions, meta: { requiresAuth: true, requiresTeacher: true } },

  { path: "/access-denied-role", component: AccessDeniedRole },
  { path: "/access-denied-time", name: "AccessDenied", component: AccessDeniedTime },
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
      return next('/access-denied-role');
    }
  }

  next();
});

export default router;