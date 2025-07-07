<template>
  <div class="solutions-container">
    <div class="header">
      <h1>Решения студентов</h1>
      <div class="stats" v-if="solutions.content && solutions.content.length">
        Показано {{ solutions.content.length }} из {{ solutions.page.totalElements }} решений
      </div>
    </div>

    <form class="filters" @submit.prevent="fetchSolutions">
      <div class="filter-group">
        <label>
          <span>Статус:</span>
          <select v-model="filters.status" class="select-input">
            <option value="">Все</option>
            <option v-for="s in statuses" :key="s" :value="s">{{ s }}</option>
          </select>
        </label>
      </div>

      <div class="filter-group">
        <label>
          <span>User ID:</span>
          <input type="number" v-model.number="filters.userId" class="text-input" />
        </label>
      </div>

      <div class="filter-group">
        <label>
          <span>Contest ID:</span>
          <input type="number" v-model.number="filters.contestId" class="text-input" />
        </label>
      </div>

      <div class="filter-group">
        <label>
          <span>С начала:</span>
          <input type="datetime-local" v-model="filters.startTime" class="datetime-input" />
        </label>
      </div>

      <div class="filter-group">
        <label>
          <span>До:</span>
          <input type="datetime-local" v-model="filters.endTime" class="datetime-input" />
        </label>
      </div>

      <div class="filter-actions">
        <button type="submit" class="apply-btn">
          <i class="fas fa-filter"></i> Применить
        </button>
        <button type="button" @click="resetSearch" class="reset-btn">
          <i class="fas fa-broom"></i> Сбросить
        </button>
      </div>
    </form>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <span>Загрузка данных...</span>
    </div>

    <div v-else-if="!solutions.content || !solutions.content.length" class="empty-state">
      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="12" y1="8" x2="12" y2="12"></line>
        <line x1="12" y1="16" x2="12.01" y2="16"></line>
      </svg>
      <h3>Решения не найдены</h3>
      <p>Попробуйте изменить параметры фильтрации</p>
    </div>

    <div v-else class="table-container">
      <table class="solutions-table">
        <thead>
          <tr>
            <th class="task-col">Имя задачи</th>
            <th class="user-col">Пользователь</th>
            <th class="status-col">Статус</th>
            <th class="date-col">Отправлено</th>
            <th class="code-col">Код</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="solution in solutions.content" :key="solution.id">
            <td class="task-col">{{ solution.taskName }}</td>
            <td class="user-col">{{ solution.username }}</td>
            <td class="status-col">
              <span :class="getStatusClass(solution.status)">{{ solution.status }}</span>
            </td>
            <td class="date-col">{{ formatDate(solution.submittedAt) }}</td>
            <td class="code-col">
              <button @click="showCodeModal(solution.code)" class="code-toggle">
                Показать код
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-container" v-if="solutions.page && solutions.page.totalPages > 1">
      <div class="pagination-info">
        Страница {{ filters.pageNumber + 1 }} из {{ solutions.page.totalPages }}
      </div>
      <div class="pagination-controls">
        <button 
          @click="prevPage" 
          :disabled="filters.pageNumber === 0" 
          class="pagination-btn"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        <div class="page-indicator">
          Страница {{ filters.pageNumber + 1 }} из {{ solutions.page.totalPages }}
        </div>
        <button 
          @click="nextPage" 
          :disabled="filters.pageNumber >= solutions.page.totalPages - 1" 
          class="pagination-btn"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>
  </div>

  <div v-if="modalCode" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content code-modal">
      <div class="modal-header">
        <h3>Код решения</h3>
        <div class="modal-actions">
          <button @click="copyCode(modalCode)" class="icon-btn" title="Скопировать">
            <i class="fas fa-copy"></i>
          </button>
          <button @click="closeModal" class="icon-btn" title="Закрыть">
            <i class="fas fa-times"></i>
          </button>
        </div>
      </div>
      <ReadOnlyCodeMirror :code="modalCode" language="text/x-java" />
    </div>
  </div>
</template>

<script>
import ReadOnlyCodeMirror from "@/components/ReadOnlyCodeMirror.vue";

export default {
  name: "StudentSolutionsPage",
  components: {
    ReadOnlyCodeMirror,
  },
  data() {
    return {
      solutions: {
        content: [],
        page: {},
      },
      filters: {
        status: "",
        userId: "",
        contestId: "",
        startTime: "",
        endTime: "",
        pageNumber: 0,
        pageSize: 10,
      },
      statuses: [
        "TIMED_OUT",
        "COMPILE_ERROR",
        "RUNTIME_ERROR",
        "WRONG_ANSWER",
        "ACCEPTED",
        "INTERNAL_ERROR",
      ],
      loading: false,
      visibleCode: null,
      modalCode: null,
    };
  },
  methods: {
    async fetchSolutions() {
      this.loading = true;
      try {
        const queryParams = new URLSearchParams();
        for (const [key, value] of Object.entries(this.filters)) {
          if (value !== "" && value !== null) {
            queryParams.append(key, value);
          }
        }

        const response = await fetch(`http://localhost:8080/api/v1/solutions?${queryParams.toString()}`, {
          credentials: "include",
        });

        if (!response.ok) throw new Error("Ошибка загрузки решений");

        this.solutions = await response.json();
      } catch (err) {
        console.error("Ошибка:", err);
        this.$toast.error("Не удалось загрузить данные. Пожалуйста, попробуйте позже.");
      } finally {
        this.loading = false;
      }
    },
    resetFilters() {
      this.filters = {
        status: "",
        userId: "",
        contestId: "",
        startTime: "",
        endTime: "",
        pageNumber: 0,
        pageSize: 10,
      };
      this.fetchSolutions();
    },
    nextPage() {
      this.filters.pageNumber++;
      this.fetchSolutions();
    },
    prevPage() {
      if (this.filters.pageNumber > 0) {
        this.filters.pageNumber--;
        this.fetchSolutions();
      }
    },
    formatDate(date) {
      return new Date(date).toLocaleString();
    },
    toggleCode(id) {
      this.visibleCode = this.visibleCode === id ? null : id;
    },
    copyCode(code) {
      navigator.clipboard.writeText(code)
        .then(() => {
          this.$toast.success('Код скопирован в буфер обмена');
        })
        .catch(err => {
          console.error('Ошибка копирования:', err);
          this.$toast.error('Не удалось скопировать код');
        });
    },
    getStatusClass(status) {
      return {
        'status-badge': true,
        'status-accepted': status === 'ACCEPTED',
        'status-error': ['COMPILE_ERROR', 'RUNTIME_ERROR', 'INTERNAL_ERROR'].includes(status),
        'status-warning': ['TIMED_OUT', 'WRONG_ANSWER'].includes(status),
      };
    },
    showCodeModal(code) {
      this.modalCode = code;
    },
    closeModal() {
      this.modalCode = null;
    },
  },
  mounted() {
    this.fetchSolutions();
  }
};
</script>

<style scoped>
.solutions-container {
  max-width: 1152px;
  margin: 20px auto;
  padding: 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.stats {
  font-size: 14px;
  color: #7f8c8d;
}

.filters {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e0e0e0;
}

.filter-group {
  display: flex;
  flex-direction: column;
}

.filter-group label span {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
  display: block;
}

.text-input,
.select-input,
.datetime-input {
  padding: 8px 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
  background: white;
  width: 100%;
  box-sizing: border-box;
}

.text-input:focus,
.select-input:focus,
.datetime-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
}

.filter-actions {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

.apply-btn,
.reset-btn {
  padding: 10px 16px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  align-self: flex-end;
  height: 40px;
}

.apply-btn {
  background-color: #3498db;
  color: white;
  border: none;
}

.apply-btn:hover {
  background-color: #2980b9;
}

.reset-btn {
  background-color: transparent;
  color: #7f8c8d;
  border: 1px solid #ddd;
  margin-left: 8px;
}

.reset-btn:hover {
  background-color: #f1f1f1;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #7f8c8d;
}

.empty-state svg {
  margin-bottom: 16px;
  color: #bdc3c7;
}

.empty-state h3 {
  font-size: 18px;
  margin-bottom: 8px;
  color: #2c3e50;
}

.empty-state p {
  font-size: 14px;
}

.table-container {
  overflow-x: auto;
  border-radius: 12px;
  border: 1px solid #eee;
}

.solutions-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.solutions-table th {
  background: #f8f9fa;
  color: #555;
  font-weight: 600;
  text-align: left;
  padding: 14px 16px;
  border-bottom: 2px solid #eee;
}

.solutions-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
  vertical-align: top;
}

.solutions-table tr:hover td {
  background-color: #f8f9fa;
}

.task-col {
  min-width: 200px;
}

.user-col {
  min-width: 150px;
}

.status-col {
  min-width: 120px;
}

.date-col {
  min-width: 180px;
  white-space: nowrap;
}

.code-col {
  min-width: 150px;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-accepted {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-error {
  background-color: #ffebee;
  color: #c62828;
}

.status-warning {
  background-color: #fff8e1;
  color: #f57f17;
}

.code-toggle {
  background: none;
  border: none;
  color: #3498db;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;
}

.code-toggle:hover {
  background: #ebf5fb;
}

.code-block {
  margin-top: 8px;
  position: relative;
}

.code-block pre {
  background: #f8f9fa;
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  font-family: 'Courier New', Courier, monospace;
  font-size: 13px;
  line-height: 1.4;
  margin: 8px 0;
}

.copy-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  align-items: center;
  gap: 4px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.copy-btn:hover {
  background: #f1f1f1;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.pagination-info {
  font-size: 14px;
  color: #7f8c8d;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.pagination-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #ddd;
  background: white;
  color: #333;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #f8f9fa;
}

.page-indicator {
  font-size: 0.9rem;
  color: #555;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  max-width: 800px;
  width: 90%;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  position: relative;
}

.modal-content h3 {
  margin-top: 0;
  font-size: 20px;
  margin-bottom: 12px;
}

.modal-content pre {
  max-height: 400px;
  overflow: auto;
  background: #f4f4f4;
  padding: 12px;
  border-radius: 8px;
  font-family: monospace;
  white-space: pre-wrap;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  gap: 10px;
}

.copy-btn,
.close-btn {
  padding: 8px 14px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
}

.copy-btn {
  background-color: #3498db;
  color: white;
}

.copy-btn:hover {
  background-color: #2980b9;
}

.close-btn {
  background-color: #e0e0e0;
}

.close-btn:hover {
  background-color: #ccc;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 16px;
}

.code-modal {
  background: #fefefe;
  border-radius: 12px;
  max-width: 800px;
  width: 100%;
  padding: 16px 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  animation: fadeIn 0.3s ease-out;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.modal-actions {
  display: flex;
  gap: 12px;
}

.icon-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #555;
  transition: color 0.2s;
}

.icon-btn:hover {
  color: #000;
}

pre {
  background: #f5f5f5;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  font-family: 'Fira Code', monospace;
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-word;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .filters {
    gap: 12px;
  }
  
  .filter-group {
    width: 100%;
    min-width: auto;
  }
  
  .filter-actions {
    width: 100%;
    flex-direction: column;
  }
  
  .apply-btn,
  .reset-btn {
    width: 100%;
    margin-left: 0;
  }
  
  .solutions-table {
    font-size: 13px;
  }
  
  .solutions-table th,
  .solutions-table td {
    padding: 8px 12px;
  }

  .pagination-container {
    flex-direction: column;
    gap: 1rem;
  }

  .pagination-controls {
    width: 100%;
    justify-content: center;
  }
}
</style>