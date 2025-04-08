<template>
  <div class="home-page">
    <header class="header">
      <h1>Добро пожаловать 👋</h1>
      <p class="subtitle">В систему контроля работ — пишите код, отправляйте решения, получайте результат моментально!</p>
      <div class="actions">
        <router-link to="/login" class="btn primary">Войти</router-link>
        <router-link to="/register" class="btn secondary">Регистрация</router-link>
      </div>
    </header>
  </div>
</template>

<script>
export default {
  name: "HomePage",
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
  font-size: 36px;
  color: #343a40;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 18px;
  color: #6c757d;
  margin-bottom: 30px;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
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
