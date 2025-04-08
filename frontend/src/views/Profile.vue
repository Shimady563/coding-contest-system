<template>
    <div class="profile-container">
      <h1>Личный кабинет</h1>

      <div v-if="user">
        <p><strong>Имя:</strong> {{ user.name }}</p>
        <p><strong>Email:</strong> {{ user.email }}</p>
        <p><strong>Роль:</strong> {{ user.role }}</p>

        <!-- Блоки в зависимости от роли -->
        <StudentPanel v-if="user.role === 'student'" />
        <TeacherPanel v-if="user.role === 'teacher'" />

        <button @click="logout" class="btn btn-danger">Выйти</button>
      </div>
      <div v-else>
        <p>Загрузка профиля...</p>
      </div>
    </div>
  </template>

  <script>
  import StudentPanel from "@/components/StudentPanel.vue";
  import TeacherPanel from "@/components/TeacherPanel.vue";
  import { getUserInfo } from "../js/auth";

  export default {
    components: { StudentPanel, TeacherPanel },
    data() {
      return {
        user: null, // Данные пользователя
      };
    },
    async created() {
    try {
      const userInfo = await getUserInfo();
      this.user = userInfo;
    } catch (err) {
      if (err.message === "UNAUTHORIZED") {
        this.$router.push("/login");
      } else {
        console.error("Ошибка загрузки профиля:", err);
      }
    }
  },
  methods: {
    logout() {
      // Очищаем accessToken (если бы не был httpOnly), но можно сделать запрос на logout
      this.$router.push("/login");
    },
  },
};
  </script>

  <style scoped>
  .profile-container {
    max-width: 600px;
    margin: auto;
    text-align: center;
  }

  .btn-danger {
    background: red;
    color: white;
    border: none;
    padding: 10px;
    cursor: pointer;
    margin-top: 20px;
    border-radius: 5px;
    margin: 5px;
  }
  </style>
