<template>
  <div class="home-page">
    <div v-if="isLoading" class="loading-container">
      <div class="loader"></div>
    </div>

    <!-- Контент -->
    <template v-else>
      <!-- Гостевой доступ -->
      <header class="header" v-if="!user">
        <h1>Добро пожаловать 👋</h1>
        <p class="subtitle">
          В системе контроля работ вы можете создавать и решать задания, получать мгновенную обратную связь и отслеживать свой прогресс!
        </p>
        <div class="actions">
          <router-link to="/login" class="btn primary">Войти</router-link>
          <router-link to="/register" class="btn secondary">Регистрация</router-link>
        </div>
      </header>

      <!-- Студент -->
      <header class="header" v-else-if="user.role === 'student'">
        <h1>Привет, {{ user.firstName }} {{ user.lastName }} 🎓</h1>
        <p class="subtitle">Ты можешь:</p>
        <ul class="features">
          <li><i class="icon">📝</i> Решать контрольные работы и задания</li>
          <li><i class="icon">📃</i> Просматривать список доступных контрольных</li>
          <li><i class="icon">📊</i> Следить за своими результатами</li>
        </ul>
      </header>

      <!-- Преподаватель -->
      <header class="header" v-else-if="user.role === 'teacher'">
        <h1>Здравствуйте, {{ user.firstName }} {{ user.lastName }} 👨‍🏫</h1>
        <p class="subtitle">Вам доступны следующие функции:</p>
        <ul class="features">
          <li><i class="icon">➕</i> Создание контрольных работ и заданий</li>
          <li><i class="icon">🛠</i> Управление содержанием заданий</li>
          <li><i class="icon">📈</i> Просмотр результатов студентов</li>
        </ul>
      </header>
    </template>
  </div>
</template>

<script>
import { getUserInfo } from "@/js/auth";

export default {
  name: "HomePage",
  data() {
    return {
      user: null,
      isLoading: true,
      error: null
    };
  },
  async created() {
    try {
      this.user = await getUserInfo();
      if (this.user) {
        this.$root.notify(`Добро пожаловать, ${this.user.firstName}!`, 'success');
      }
    } catch (err) {
      this.error = err.message;
      console.error("Ошибка загрузки данных пользователя:", err);
    } finally {
      this.isLoading = false;
    }
  }
};
</script>

<style scoped>
.home-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 90vh;
  padding: 20px;
  }

.header {
  text-align: center;
  background: white;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
  max-width: 700px;
  width: 100%;
  animation: fadeIn 0.5s ease-out;
}

h1 {
  font-size: 2.2rem;
  color: #2c3e50;
  margin-bottom: 1rem;
  font-weight: 600;
}

.subtitle {
  font-size: 1.1rem;
  color: #7f8c8d;
  margin-bottom: 2rem;
  line-height: 1.5;
}

.features {
  list-style: none;
  padding: 0;
  font-size: 1rem;
  text-align: left;
  color: #34495e;
  margin: 0 auto;
  max-width: 80%;
}

.features li {
  margin-bottom: 1rem;
  padding-left: 1.5rem;
  position: relative;
  line-height: 1.6;
}

.icon {
  position: absolute;
  left: -1.5rem;
  top: 0.1rem;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
  margin-top: 2rem;
}

.btn {
  padding: 0.8rem 2rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.btn.primary {
  background-color: #3498db;
  color: white;
}

.btn.primary:hover {
  background-color: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(41, 128, 185, 0.3);
}

.btn.secondary {
  background-color: #95a5a6;
  color: white;
}

.btn.secondary:hover {
  background-color: #7f8c8d;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(127, 140, 141, 0.3);
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.loader {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 600px) {
  .header {
    padding: 25px;
  }

  h1 {
    font-size: 1.8rem;
  }

  .actions {
    flex-direction: column;
    gap: 12px;
  }

  .btn {
    width: 100%;
  }
}
</style>
