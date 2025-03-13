<template>
    <div class="auth-container">
      <h2>Вход в систему</h2>
      <form @submit.prevent="login">
        <div>
          <label>Email:</label>
          <input type="email" v-model="email" required />
        </div>
        <div>
          <label>Пароль:</label>
          <input type="password" v-model="password" required />
        </div>
        <button type="submit">Войти</button>
      </form>
      <p>Нет аккаунта? <router-link to="/register">Зарегистрироваться</router-link></p>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        email: "",
        password: "",
      };
    },
    methods: {
      login() {
        console.log("Вход:", this.email, this.password);
        // Здесь будет логика запроса на бэкенд
      },
    },
  };
  </script>
  
  <style scoped>
  .auth-container {
    max-width: 400px;
    margin: auto;
    text-align: center;
  }
  
  input {
    width: 100%;
    padding: 8px;
    margin: 5px 0;
  }
  </style>
  