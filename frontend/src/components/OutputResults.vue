<template>
  <div class="output-results">
    <h2>Результаты</h2>
    <table v-if="results.length">
      <thead>
        <tr>
          <th>ID</th>
          <th>Статус</th>
          <th>Тестов пройдено</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="result in results" :key="result.id">
          <td>{{ result.id }}</td>
          <td :class="result.status.toLowerCase()">{{ result.status }}</td>
          <td>{{ result.testsPassed }}</td>
        </tr>
      </tbody>
    </table>
    <p v-else>Пока нет результатов. Пожалуйста, отправьте свой код!</p>
  </div>
</template>

<script>

export default {
  name: "OutputResults",
  data() {
    return {
      results: [],
    };
  },
  methods: {
    async fetchResults() {
      try {
        const response = await fetch(`http://localhost:8080/test/solutions?taskId=${this.taskData.id}`);
        if (!response.ok) {
          throw new Error('Ошибка загрузки данных');
        }
        const data = await response.json();
        this.taskData.testCases = data.testCases;
      } catch (error) {
        console.error('Error fetching results:', error);
      }
    },
  },
  created() {
    this.fetchResults();
  }
};
</script>

<style scoped>
.output-results {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  font-size: 22px;
  margin-bottom: 12px;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
}

th, td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}

th {
  background-color: #333;
  color: white;
}

.accepted {
  color: green;
  font-weight: bold;
}

.failed {
  color: red;
  font-weight: bold;
}

.pending {
  color: orange;
  font-weight: bold;
}
</style>
