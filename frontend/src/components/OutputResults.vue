<template>
  <div class="output-results">
    <h2>Результаты</h2>
    <div class="table-container">
      <table class="results-table" v-if="results.length">
      <thead>
        <tr>
          <th class="number-col">№</th>
          <th class="status-col">Статус</th>
          <th class="date-col">Дата отправки</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(result, index) in results" :key="result.id">
          <td class="number-col">{{ index + 1 }}</td>
          <td class="status-col" :class="statusClass(result.status)">{{ result.status }}</td>
          <td class="date-col">{{ new Date(result.submittedAt).toLocaleString() }}</td>
        </tr>
      </tbody>
    </table>
    <p v-else class="empty-message">Пока нет результатов. Пожалуйста, отправьте свой код!</p>
    </div>
  </div>
</template>

<script>
import { getUserInfo } from "@/js/auth";
import { MANAGER_URL } from "@/js/api";

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
      } catch {
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
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
}

h2 {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 24px 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.table-container {
  overflow-x: auto;
  border-radius: 12px;
  border: 1px solid #eee;
}

.results-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.results-table th {
  background: #f8f9fa;
  color: #555;
  font-weight: 600;
  text-align: left;
  padding: 14px 16px;
  border-bottom: 2px solid #eee;
}

.results-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
  vertical-align: middle;
}

.results-table tr:hover td {
  background-color: #f8f9fa;
}

.number-col {
  min-width: 60px;
  color: #7f8c8d;
}

.status-col {
  min-width: 150px;
}

.date-col {
  min-width: 180px;
}

.empty-message {
  text-align: center;
  padding: 20px;
  color: #7f8c8d;
}

/* Стили статусов */
.status-accepted {
  color: #2ecc71;
  font-weight: bold;
}

.status-wrong {
  color: #e67e22;
  font-weight: bold;
}

.status-compile {
  color: #3498db;
  font-weight: bold;
}

.status-runtime {
  color: #e74c3c;
  font-weight: bold;
}

.status-timeout {
  color: #9b59b6;
  font-weight: bold;
}

.status-internal {
  color: #7f8c8d;
  font-weight: bold;
}

@media (max-width: 768px) {
  .output-results {
    padding: 20px;
  }
  
  h2 {
    font-size: 24px;
    margin-bottom: 20px;
  }
  
  .results-table th,
  .results-table td {
    padding: 10px 12px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .output-results {
    padding: 16px;
  }
  
  h2 {
    font-size: 22px;
  }
  
  .results-table th,
  .results-table td {
    padding: 8px 10px;
    font-size: 12px;
  }
  
  .number-col {
    min-width: 50px;
  }
  
  .status-col {
    min-width: 120px;
  }
  
  .date-col {
    min-width: 150px;
  }
}
</style>
