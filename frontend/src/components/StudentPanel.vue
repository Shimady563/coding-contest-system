<template>
    <div class="student-panel">
      <h2>Мои контрольные работы</h2>
      <ul>
        <li v-for="test in tests" :key="test.id">
          {{ test.name }} — {{ test.status }}
        </li>
      </ul>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        tests: [
          { id: 1, name: "Контрольная 1", status: "Пройдено" },
          { id: 2, name: "Контрольная 2", status: "В процессе" },
        ],
      };
    },
  };
  </script>
  
  <style scoped>
  .student-panel {
    margin-top: 20px;
  }
  </style>
  