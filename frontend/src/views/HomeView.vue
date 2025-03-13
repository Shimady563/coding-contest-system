<template>
  <div class="home-page">
    <header class="header">
      <h1>Добро пожаловать в систему контроля работ</h1>
      <p>Пишите код, отправляйте решения, получайте результат моментально!</p>
      <div class="actions">
        <router-link to="/login" class="btn">Войти</router-link>
        <router-link to="/register" class="btn btn-secondary">Регистрация</router-link>
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
  text-align: center;
  padding: 50px;
}

.header {
  max-width: 600px;
  margin: 0 auto;
}

.actions {
  margin-top: 20px;
}

.btn {
  display: inline-block;
  padding: 10px 20px;
  background: #007bff;
  color: white;
  text-decoration: none;
  border-radius: 5px;
  margin: 5px;
}

.btn-secondary {
  background: #6c757d;
}
</style>
