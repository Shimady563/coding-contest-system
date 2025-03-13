<template>
    <div class="teacher-panel">
      <h2>Управление контрольными</h2>
      <button @click="createTest" class="btn btn-primary">Создать контрольную</button>
  
      <h3>Мои контрольные работы</h3>
      <ul>
        <li v-for="test in tests" :key="test.id">
          {{ test.name }} (Студентов: {{ test.students }})
        </li>
      </ul>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        tests: [
          { id: 1, name: "Контрольная 1", students: 15 },
          { id: 2, name: "Контрольная 2", students: 20 },
        ],
      };
    },
    methods: {
      createTest() {
        console.log("Создание новой контрольной...");
      },
    },
  };
  </script>
  
  <style scoped>
  .teacher-panel {
    margin-top: 20px;
  }
  .btn-primary {
    background: blue;
    color: white;
    border: none;
    padding: 10px;
    cursor: pointer;
  }
  </style>
  