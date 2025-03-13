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
  
  export default {
    components: { StudentPanel, TeacherPanel },
    data() {
      return {
        user: null, // Данные пользователя
      };
    },
    created() {
      this.fetchUser();
    },
    methods: {
      fetchUser() {
        // Здесь можно делать запрос к API, но пока используем моковые данные
        this.user = {
          name: "Иван Иванов",
          email: "ivan@example.com",
          role: "student", // Или "teacher"
        };
      },
      logout() {
        console.log("Выход...");
        // Очистка токена, редирект на логин
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
  