<template>
  <div class="navbar-container">
    <nav class="navbar">
      <div class="container">
        <router-link to="/" class="logo">🧠 Coding Contest</router-link>

        <div v-if="user" class="dropdown-wrapper">
          <button class="dropdown-toggle" @mouseenter="openDropdown" @mouseleave="closeDropdown">
            👤 {{ user.role === 'teacher' ? 'Преподаватель' : 'Студент' }} ▾
          </button>
          
          <transition name="fade">
            <ul class="dropdown-menu" v-show="isDropdownOpen" 
                @mouseenter="openDropdown" @mouseleave="closeDropdown">
              <li v-if="user.role === 'student'">
                <router-link to="/solve-contest" @click.native="closeDropdown">Контрольные</router-link>
              </li>
              <li v-if="user.role === 'teacher'">
                <router-link to="/manage-contests" @click.native="closeDropdown">Задания</router-link>
              </li>
              <li v-if="user.role === 'teacher'">
                <router-link to="/manage-students" @click.native="closeDropdown">Студенты</router-link>
              </li>
              <li v-if="user.role === 'teacher'">
                <router-link to="/student-results" @click.native="closeDropdown">Оценки</router-link>
              </li>
              <li><router-link to="/profile" @click.native="closeDropdown">Профиль</router-link></li>
              <li><a @click.prevent="logout" style="cursor: pointer;">Выйти</a></li>
            </ul>
          </transition>
        </div>

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
  </div>
</template>

<script>
import { getUserInfo, logoutUser } from "../js/auth";

export default {
  data() {
    return {
      user: null,
      isDropdownOpen: false
    };
  },
  async created() {
    try {
      const userInfo = await getUserInfo();
      this.user = userInfo;
    } catch (e) {
      this.user = null;
    }
  },
  methods: {
    async logout() {
      const success = await logoutUser();
      if (success) {
        this.user = null;
        this.$router.push("/login");
      }
    },
    openDropdown() {
      this.isDropdownOpen = true;
    },
    closeDropdown() {
      this.isDropdownOpen = false;
    }
  }
};
</script>

<style scoped>
.navbar-container {
  position: relative;
}

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
  position: relative;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #222;
  text-decoration: none;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 16px;
  list-style: none;
  padding: 0;
  margin: 0;
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

.dropdown-wrapper {
  position: relative;
  display: inline-block;
}

.dropdown-toggle {
  cursor: pointer;
  font-size: 14px;
  padding: 6px 12px;
  color: #333;
  background: #f9f9f9;
  border: 1px solid #ccc;
  border-radius: 50px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: background 0.2s;
  position: relative;
  z-index: 101;
}

.dropdown-toggle:hover {
  background: #eaeaea;
}

.dropdown-menu {
  position: absolute;
  right: 0;
  top: calc(100% + 5px);
  background: white;
  border: 1px solid #ddd;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-radius: 8px;
  padding: 8px 0;
  min-width: 220px;
  z-index: 1000;
  list-style: none;
  margin: 0;
}

.dropdown-menu li {
  padding: 0;
}

.dropdown-menu a {
  color: #333;
  text-decoration: none;
  font-size: 14px;
  display: block;
  padding: 8px 20px;
  transition: all 0.2s;
}

.dropdown-menu a:hover {
  background: #f0f4ff;
  color: #2f80ed;
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}

ul {
  list-style-type: none;
}
</style>