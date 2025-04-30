import { createRouter, createWebHistory } from 'vue-router'
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

const routes = [
  { path: "/", component: HomeView },
  { path: "/login", component: Login },
  { path: "/register", component: Register },
  { path: "/profile", component: Profile },
  { path: "/contest", component: Tests },
  { path: '/manage-contests', component: TeacherManage },
  { path: '/create-contest', component: CreateTest },
  { path: '/create-task', component: CreateTask},
  { path: '/solve-contest', component: StudentContestsPage},
  { path: '/edit-contest/:id', component: EditContest, props: true },
  { path: '/contest/:id', component: ContestVersionsPage},
  { path: '/contest-version/:id/tasks', component: TasksPage},
  { path: '/task/:taskId', component: StudentContest, name: 'StudentContest' }
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
});

export default router;
