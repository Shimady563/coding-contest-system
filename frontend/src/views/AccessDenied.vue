<template>
  <div class="access-denied-container">
    <div class="access-denied-card">
      <div class="icon-container">
        <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#e74c3c" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="4.93" y1="4.93" x2="19.07" y2="19.07"></line>
        </svg>
      </div>
      <h1>Доступ запрещен</h1>
      <p class="message">У вас недостаточно прав для просмотра этой страницы.</p>
      <p class="details">Эта страница доступна только преподавателям. Если вы считаете, что это ошибка, обратитесь к администратору системы.</p>
      <div class="actions">
        <router-link to="/" class="home-btn">На главную</router-link>
        <button v-if="isStudent" @click="logout" class="logout-btn">Выйти и войти как преподаватель</button>
      </div>
    </div>
  </div>
</template>

<script>
import { getUserInfo } from '@/js/auth';

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
    logout() {
      localStorage.removeItem('tokenData');
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.access-denied-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.access-denied-card {
  max-width: 500px;
  width: 100%;
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.icon-container {
  margin-bottom: 24px;
}

h1 {
  color: #e74c3c;
  font-size: 28px;
  margin-bottom: 16px;
  font-weight: 600;
}

.message {
  font-size: 18px;
  color: #333;
  margin-bottom: 12px;
  font-weight: 500;
}

.details {
  color: #666;
  margin-bottom: 24px;
  line-height: 1.5;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 24px;
}

.home-btn, .logout-btn {
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  display: block;
}

.home-btn {
  background-color: #3498db;
  color: white;
  border: none;
}

.home-btn:hover {
  background-color: #2980b9;
}

.logout-btn {
  background-color: transparent;
  color: #e74c3c;
  border: 1px solid #e74c3c;
}

.logout-btn:hover {
  background-color: #fdeaea;
}

@media (max-width: 600px) {
  .access-denied-card {
    padding: 30px 20px;
  }

  h1 {
    font-size: 24px;
  }

  .message {
    font-size: 16px;
  }
}
</style>
