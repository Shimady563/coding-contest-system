<template>
  <div class="output-results">
    <h2>Результаты</h2>
    <table v-if="results.length">
      <thead>
        <tr>
          <th>№</th>
          <th>Статус</th>
          <th>Дата отправки</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(result, index) in results" :key="result.id">
          <td>{{ index + 1 }}</td>
          <td :class="statusClass(result.status)">{{ result.status }}</td>
          <td>{{ new Date(result.submittedAt).toLocaleString() }}</td>
        </tr>
      </tbody>
    </table>
    <p v-else>Пока нет результатов. Пожалуйста, отправьте свой код!</p>
  </div>
</template>

<script>
import { MANAGER_URL, getUserInfo } from "@/js/auth";

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
      const user = await getUserInfo();
      try {
        const user = await getUserInfo();
        if (!user?.id) throw new Error("User ID не получен");

        const queryParams = new URLSearchParams();
        
        queryParams.append("userId", user.id);
        queryParams.append("taskId", this.taskId);
        queryParams.append("pageNumber", 0);
        queryParams.append("pageSize", 1000);

        const response = await fetch(`${MANAGER_URL}/solutions?${queryParams.toString()}`, {
          credentials: "include",
        });

        if (!response.ok) throw new Error('Ошибка загрузки данных');
        const data = await response.json();
        this.results = data.content || [];
        console.log(this.results)
      } catch (error) {
        console.log(this.results)
        this.results = [];
      }
    },
    statusClass(status) {
      switch (status) {
        case "ACCEPTED": return "status-accepted";
        case "WRONG_ANSWER": return "status-wrong";
        case "COMPILE_ERROR": return "status-compile";
        case "RUNTIME_ERROR": return "status-runtime";
        case "TIMED_OUT": return "status-timeout";
        case "INTERNAL_ERROR": return "status-internal";
        default: return "";
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
  background-color: #2c3e50;
  color: white;
}

.status-accepted {
  color: #2ecc71; /* зелёный */
  font-weight: bold;
}
.status-wrong {
  color: #e67e22; /* оранжевый */
  font-weight: bold;
}
.status-compile {
  color: #3498db; /* синий */
  font-weight: bold;
}
.status-runtime {
  color: #e74c3c; /* красный */
  font-weight: bold;
}
.status-timeout {
  color: #9b59b6; /* фиолетовый */
  font-weight: bold;
}
.status-internal {
  color: #7f8c8d; /* серый */
  font-weight: bold;
}
</style>
