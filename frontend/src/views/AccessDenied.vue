<template>
  <div class="access-denied-container">
    <div class="access-denied-card">
      <div class="icon-container">
        <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#e53935" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="4.93" y1="4.93" x2="19.07" y2="19.07"></line>
        </svg>
      </div>
      <h1>Доступ запрещён</h1>
      <p class="message-access-denied">У вас недостаточно прав для просмотра этой страницы.</p>
      <p class="details">Эта страница доступна только преподавателям. Если вы считаете, что это ошибка, обратитесь к администратору системы.</p>
      <div class="actions">
        <router-link to="/" class="btn primary-btn">На главную</router-link>
        <button v-if="isStudent" @click="logout" class="btn secondary-btn">Выйти и войти как преподаватель</button>
      </div>
    </div>
  </div>
</template>

<script>
import { getUserInfo, logoutUser } from "../js/auth";
import '@/assets/styles/access-denied.css';

export default {
  name: 'AccessDenied',
  data() {
    return {
      userRole: null
    }
  },
  computed: {
    isStudent() {
      return this.userRole === 'student';
    }
  },
  async created() {
    const user = await getUserInfo();
    this.userRole = user?.role;
  },
  methods: {
    async logout() {
      const success = await logoutUser();
      if (success) {
        this.$router.push("/login");
      }
    },
  },
};
</script>