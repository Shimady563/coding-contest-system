<template>
  <div class="home-page">
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
      <h1>Привет, {{ user.name }} 🎓</h1>
      <p class="subtitle">Ты можешь:</p>
      <ul class="features">
        <li>📝 Решать контрольные работы и задания</li>
        <li>📃 Просматривать список доступных контрольных</li>
        <li>📊 Следить за своими результатами</li>
      </ul>
    </header>

    <!-- Преподаватель -->
    <header class="header" v-else-if="user.role === 'teacher'">
      <h1>Здравствуйте, {{ user.name }} 👨‍🏫</h1>
      <p class="subtitle">Вам доступны следующие функции:</p>
      <ul class="features">
        <li>➕ Создание контрольных работ и заданий</li>
        <li>🛠 Управление содержанием заданий</li>
        <li>📈 Просмотр результатов студентов</li>
      </ul>
    </header>
  </div>
</template>

<script>
import { getUserInfo } from "@/js/auth";

export default {
  name: "HomePage",
  data() {
    return {
      user: null,
    };
  },
  async mounted() {
    this.user = await getUserInfo();
  },
};
</script>

<style scoped>
.home-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 90vh;
  background: linear-gradient(to right, #f8f9fa, #e9ecef);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  padding: 20px;
}

.header {
  text-align: center;
  background: white;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.1);
  max-width: 700px;
  width: 100%;
}

h1 {
  font-size: 32px;
  color: #343a40;
  margin-bottom: 15px;
}

.subtitle {
  font-size: 18px;
  color: #6c757d;
  margin-bottom: 20px;
}

.features {
  list-style: none;
  padding: 0;
  font-size: 16px;
  text-align: left;
  color: #495057;
}

.features li {
  margin-bottom: 10px;
  padding-left: 20px;
  position: relative;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
  margin-top: 20px;
}

.btn {
  padding: 12px 30px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  box-shadow: 0 5px 15px rgba(0, 123, 255, 0.2);
}

.btn.primary {
  background-color: #007bff;
  color: white;
}

.btn.primary:hover {
  background-color: #0069d9;
}

.btn.secondary {
  background-color: #6c757d;
  color: white;
}

.btn.secondary:hover {
  background-color: #5a6268;
}
</style>
