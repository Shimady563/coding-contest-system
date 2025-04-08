<template>
  <div class="manage-container">
    <h1>Управление контрольными</h1>

    <!-- Кнопка создания новой КР -->
    <button @click="goToCreateTest">Создать контрольную</button>

    <!-- Список КР -->
    <ul class="test-list">
      <li v-for="test in tests" :key="test.id" class="test-item">
        <router-link :to="`/edit-test/${test.id}`" class="test-link">
          {{ test.name }}
        </router-link>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tests: [
        { id: 1, name: "Контрольная по алгоритмам" },
        { id: 2, name: "ООП в C++" },
        { id: 3, name: "Базы данных — часть 1" }
      ]
    };
  },
  methods: {
    goToCreateTest() {
      this.$router.push("/create-test");
    }
  }
};
</script>

<style scoped>
.manage-container {
  max-width: 600px;
  margin: 40px auto;
  padding: 20px;
  text-align: center;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  box-sizing: border-box;
}

h1 {
  font-size: 28px;
  margin-bottom: 20px;
  color: #333;
}

button {
  background: #2f80ed;
  color: #fff;
  border: none;
  padding: 12px 20px;
  font-size: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-bottom: 30px;
}

button:hover {
  background-color: #1366d6;
}

.test-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.test-item {
  margin-bottom: 15px;
}

.test-link {
  display: block;
  padding: 12px 16px;
  background-color: #f5f7fa;
  color: #2d2d2d;
  text-decoration: none;
  font-weight: 500;
  border-radius: 8px;
  transition: background-color 0.2s ease;
  text-align: left;
  overflow-wrap: break-word; /* гарантирует перенос длинных слов */
  box-sizing: border-box;
}

.test-link:hover {
  background-color: #e2e6ed;
  color: #1a73e8;
}
</style>
