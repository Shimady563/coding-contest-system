<template>
  <nav class="navbar">
    <div class="container">
      <router-link to="/" class="logo">🧠 Coding Contest</router-link>

      <ul class="nav-links" v-if="user">
        <li v-if="user.role === 'student'">
          <router-link class="links" to="/solve-contest">Контрольные</router-link>
        </li>
        <li v-if="user.role === 'teacher'">
          <router-link class="links" to="/manage-contests">Управление заданиями</router-link>
        </li>
        <li v-if="user.role === 'teacher'">
          <router-link class="links" to="/manage-students">Управление студентами</router-link>
        </li>
        <li>
          <router-link class="links" to="/profile">Профиль</router-link>
        </li>
        <li>
          <button @click="logout" class="btn btn-logout">Выйти</button>
        </li>
      </ul>

      <ul class="nav-links" v-else>
        <li>
          <router-link to="/login" class="btn btn-light">Войти</router-link>
        </li>
        <li>
          <router-link to="/register" class="btn btn-dark">Регистрация</router-link>
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
    const userInfo = await getUserInfo();
    this.user = userInfo;
  },
  methods: {
    logout() {
      localStorage.removeItem("tokenData");
      this.user = null;
      this.$router.push("/login");
    },
  },
};
</script>

<style scoped>
.navbar {
  background: white;
  border-bottom: 1px solid #eee;
  padding: 12px 0;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.03);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #222;
  text-decoration: none;
}

.links {
  color: #2f80ed;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 16px;
  list-style: none;
}

.nav-links a {
  text-decoration: none;
  font-size: 15px;
  transition: color 0.2s;
}

.nav-links a:hover {
  color: #2f80ed;
}

.btn {
  padding: 6px 14px;
  border-radius: 6px;
  font-size: 14px;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-light {
  background: #f0f0f0;
  color: #333;
}

.btn-light:hover {
  background: #e0e0e0;
}

.btn-dark {
  background: #2f80ed;
  color: white;
}

.btn-dark:hover {
  background: #1e63c5;
}

.btn-logout {
  background: #ff4d4f;
  color: white;
}

.btn-logout:hover {
  background: #d9363e;
}
</style>
