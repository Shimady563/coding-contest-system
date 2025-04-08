<template>
  <nav class="navbar">
    <div class="nav-content">
      <router-link to="/" class="logo">coding-contest-system</router-link>

      <ul class="nav-links" v-if="user">
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

      <ul class="nav-links" v-else>
        <li>
          <router-link to="/login">Войти</router-link>
        </li>
      </ul>
    </div>
  </nav>
</template>


  <script>
  import { getUserInfo } from "../js/auth";

  export default {
  data() {
    return {
      user: null,
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
        console.error("Ошибка при загрузке навбара:", err);
      }
    }
  },
  methods: {
    logout() {
      this.$router.push("/login");
    },
  },
};
  </script>

  <style scoped>
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
