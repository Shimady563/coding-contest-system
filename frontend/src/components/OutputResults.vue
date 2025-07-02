<template>
  <div class="output-results">
    <h2>Результаты</h2>
    <table v-if="results.length">
      <thead>
        <tr>
          <th>ID</th>
          <th>Статус</th>
          <th>Дата отправки</th>
          <th>Имя задачи</th>
          <th>Пользователь</th>
          <th>Код</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="result in results" :key="result.id">
          <td>{{ result.id }}</td>
          <td :class="result.status.toLowerCase()">{{ result.status }}</td>
          <td>{{ new Date(result.submittedAt).toLocaleString() }}</td>
          <td>{{ result.taskName }}</td>
          <td>{{ result.username }}</td>
          <td><pre>{{ result.code }}</pre></td>
        </tr>
      </tbody>
    </table>
    <p v-else>Пока нет результатов. Пожалуйста, отправьте свой код!</p>
  </div>
</template>

<script>
export default {
  name: "OutputResults",
  props: {
    taskId: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      results: [],
    };
  },
  methods: {
    async fetchResults() {
      if (!this.taskId) return;
      try {
        const response = await fetch(`http://localhost:8080/api/v1/solutions/task?taskId=${this.taskId}`, {
          credentials: "include"
        });
        if (!response.ok) throw new Error('Ошибка загрузки данных');
        this.results = await response.json();
      } catch (error) {
        console.error('Error fetching results:', error);
        this.results = [];
      }
    },
  },
  watch: {
    taskId: {
      immediate: true,
      handler() {
        this.fetchResults();
      },
    },
  },
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
pre {
  white-space: pre-wrap;
  max-width: 300px;
  overflow-x: auto;
}
</style>
