<template>
    <div class="auth-container">
      <h2>Регистрация</h2>
      <form @submit.prevent="register">
        <div>
          <label>Email:</label>
          <input type="email" v-model="email" required />
        </div>
        <div>
          <label>Пароль:</label>
          <input type="password" v-model="password" required />
        </div>
        <button type="submit">Зарегистрироваться</button>
      </form>
      <p>Уже есть аккаунт? <router-link to="/login">Войти</router-link></p>
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
      register() {
        console.log("Регистрация:", this.email, this.password);
        // Здесь будет логика отправки запроса на регистрацию
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
  