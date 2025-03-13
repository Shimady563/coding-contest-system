<template>
    <nav class="navbar">
      <div class="nav-content">
        <router-link to="/" class="logo">coding-contest-system</router-link>
  
        <ul class="nav-links">
          <li v-if="user.role === 'student'">
            <router-link to="/tests">Мои контрольные</router-link>
          </li>
          <li v-if="user.role === 'teacher'">
            <router-link to="/manage-tests">Управление контрольными</router-link>
          </li>
          <li>
            <router-link to="/profile">Личный кабинет</router-link>
          </li>
          <li>
            <button @click="logout" class="btn-logout">Выйти</button>
          </li>
        </ul>
      </div>
    </nav>
  </template>
  
  <script>
  export default {
    data() {
      return {
        user: { name: "Иван Иванов", role: "student" }, // Заглушка, позже заменится API-запросом
      };
    },
    methods: {
      logout() {
        console.log("Выход...");
        this.$router.push("/login");
      },
    },
  };
  </script>
  
  <style scoped>
  /* Убираем все отступы */
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }
  
  .navbar {
    width: 100%;
    background: #333;
    color: white;
    padding: 15px 20px;
    position: fixed; /* Фиксируем наверху */
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000; /* Поверх всего */
  }
  
  .nav-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }
  
  .logo {
    font-size: 20px;
    font-weight: bold;
    color: white;
    text-decoration: none;
  }
  
  .nav-links {
    list-style: none;
    display: flex;
    gap: 20px;
  }
  
  .nav-links a {
    color: white;
    text-decoration: none;
    font-size: 16px;
  }
  
  .btn-logout {
    background: red;
    border: none;
    color: white;
    padding: 5px 10px;
    cursor: pointer;
  }
  </style>
  