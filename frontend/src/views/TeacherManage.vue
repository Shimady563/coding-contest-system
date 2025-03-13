<template>
    <div class="manage-container">
      <h1>Управление контрольными</h1>
  
      <!-- Кнопка для перехода на страницу создания контрольной -->
      <button @click="goToCreateTest">Создать контрольную</button>
  
      <!-- Список контрольных работ -->
      <ul>
        <li v-for="test in tests" :key="test.id">
          {{ test.name }}
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
          { id: 2, name: "ООП в C++" }
        ]
      };
    },
    methods: {
      goToCreateTest() {
        this.$router.push("/create-test"); // Переход на страницу создания контрольной
      }
    }
  };
  </script>
  
  <style scoped>
  .manage-container {
    max-width: 600px;
    margin: 20px auto;
    text-align: center;
  }
  
  button {
    background: #007bff;
    color: white;
    border: none;
    padding: 10px 15px;
    margin-bottom: 20px;
    cursor: pointer;
    border-radius: 5px;
  }
  
  button:hover {
    background: #0056b3;
  }
  </style>
  