<template>
  <div class="page-container">
    <div class="page-header">
      <h1><i class="fas fa-check-circle"></i> Решения студентов</h1>
    </div>

    <form class="filters" @submit.prevent="fetchSolutions">
      <div 
        class="filter-group floating-label multiselect-floating" 
        :class="{ active: selectedStatus || $refs.statusSelect?.isOpen }"
      >
        <div class="custom-multiselect full-width">
          <multiselect
            id="statusSelect"
            name="statusSelect"
            ref="statusSelect"
            v-model="selectedStatus"
            :options="statuses"
            :searchable="true"
            :show-labels="false"
            placeholder=""
            label="name"
            track-by="name"
            :append-to-body="true"
            open-direction="below"
            @select="forceCloseSelect('statusSelect')"
          />
        </div>
        <label for="statusSelect">Статус</label>
      </div>

      <div 
        class="filter-group floating-label multiselect-floating" 
        :class="{ active: selectedUser || $refs.userSelect?.isOpen }"
      >
        <div class="custom-multiselect full-width">
          <multiselect
            id="userSelect"
            name="userSelect"
            ref="userSelect"
            v-model="selectedUser"
            :options="users"
            :custom-label="userLabel"
            track-by="id"
            placeholder=""
            :searchable="true"
            :allow-empty="true"
            :multiple="false"
            :show-labels="false"
            :append-to-body="true"
            open-direction="below"
            @select="forceCloseSelect('userSelect')"
          />
        </div>
        <label for="userSelect">Пользователь</label>
      </div>

      <div 
        class="filter-group floating-label multiselect-floating" 
        :class="{ active: selectedTask || $refs.taskSelect?.isOpen }"
      >
        <div class="custom-multiselect full-width">
          <multiselect
            id="taskSelect"
            name="taskSelect"
            ref="taskSelect"
            v-model="selectedTask"
            :options="tasks"
            track-by="id"
            label="name"
            placeholder=""
            :searchable="true"
            :allow-empty="true"
            :multiple="false"
            :show-labels="false"
            :append-to-body="true"
            open-direction="below"
            @select="forceCloseSelect('taskSelect')"
          />
        </div>
        <label for="taskSelect">Задача</label>
      </div>

      <div class="filter-group floating-label">
        <input 
          type="datetime-local" 
          v-model="filters.startTime" 
          id="startTime"
          name="startTime"
          class="text-input datetime-input" 
          placeholder=""
        />
        <label for="startTime">С начала</label>
      </div>

      <div class="filter-group floating-label">
        <input 
          type="datetime-local" 
          v-model="filters.endTime" 
          id="endTime"
          name="endTime"
          class="text-input datetime-input" 
          placeholder=""
        />
        <label for="endTime">До</label>
      </div>

      <div class="filter-actions">
        <button type="submit" class="apply-btn">
          <i class="fas fa-filter"></i> Применить
        </button>
        <button type="button" @click="resetFilters" class="reset-btn">
          <i class="fas fa-broom"></i> Сбросить
        </button>
      </div>
    </form>

    <div class="stats-container" v-if="solutions.content && solutions.content.length">
      <div class="stats">
        Показано {{ solutions.content.length }} из {{ solutions.page.totalElements }} решений
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <span>Загрузка данных...</span>
    </div>

    <div v-else-if="!solutions.content || !solutions.content.length" class="empty-state">
      <i class="fas fa-file-code"></i>
      <h3>Решения не найдены</h3>
      <p>Попробуйте изменить параметры поиска</p>
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
              <button @click="showCodeModal(solution.code)" class="btn-icon code-btn" title="Показать код">
                <i class="fas fa-code"></i>
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

    <div v-if="modalCode" class="modal-backdrop" @click.self="closeModal">
      <div class="modal-dialog">
        <div class="modal-header">
          <h3><i class="fas fa-code"></i> Код решения</h3>
          <div class="modal-actions">
            <button @click="copyCode(modalCode)" class="btn-icon copy-btn" title="Скопировать">
              <i class="fas fa-copy"></i>
            </button>
            <button @click="closeModal" class="btn-icon close-btn" title="Закрыть">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
        <div class="modal-body">
          <ReadOnlyCodeMirror :code="modalCode" language="text/x-java" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ReadOnlyCodeMirror from "@/components/ReadOnlyCodeMirror.vue";
import Multiselect from "vue-multiselect";
import "vue-multiselect/dist/vue-multiselect.min.css";
import { listSolutions, listTasksWithParams, listUsers } from "@/js/manager";

export default {
  name: "StudentSolutionsPage",
  components: {
    ReadOnlyCodeMirror,
    Multiselect,
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
        { name: "TIMED_OUT" },
        { name: "COMPILE_ERROR" },
        { name: "RUNTIME_ERROR" },
        { name: "WRONG_ANSWER" },
        { name: "ACCEPTED" },
        { name: "INTERNAL_ERROR" },
      ],
      tasks: [],
      users: [],
      selectedStatus: null,
      loading: false,
      visibleCode: null,
      modalCode: null,
      selectedUser: null,
      selectedTask: null,
    };
  },
  methods: {
    async fetchSolutions() {
      this.loading = true;
      try {
        const params = { ...this.filters };

        if (this.selectedStatus?.name) { 
          params.status = this.selectedStatus.name;
        }
        if (this.selectedUser) {
          params.userId = this.selectedUser.id;
        }
        if (this.selectedTask) {
          params.taskId = this.selectedTask.id;
        }

        this.solutions = await listSolutions(params);
      } catch {
        this.$root.notify("Не удалось загрузить данные. Пожалуйста, попробуйте позже.", 'error');
      } finally {
        this.loading = false;
      }
    },
    async fetchTasks() {
      try {
        const data = await listTasksWithParams({
          pageSize: 1000,
          pageNumber: 0,
        });
        this.tasks = data.content || [];
      } catch {
        this.$root.notify("Не удалось загрузить список задач", 'error');
      }
    },
    async fetchUsers() {
      try {
        const data = await listUsers({
          role: "ROLE_STUDENT",
          pageSize: 1000,
          pageNumber: 0,
        });
        this.users = data.content || [];
      } catch {
        this.$root.notify("Не удалось загрузить список пользователей", 'error');
      }
    },
    userLabel(user) {
      return `${user.firstName} ${user.lastName}`;
    },
    resetFilters() {
      this.filters = {
        status: "",
        userId: "",
        taskId: "",
        startTime: "",
        endTime: "",
        pageNumber: 0,
        pageSize: 10,
      };
      this.selectedUser = null;
      this.selectedTask = null;
      this.selectedStatus = null;
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
    copyCode(code) {
      navigator.clipboard.writeText(code)
        .then(() => {
          this.$root.notify('Код скопирован в буфер обмена', 'success');
        })
        .catch(err => {
          console.error('Ошибка копирования:', err);
          this.$root.notify('Не удалось скопировать код', 'error');
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
    forceCloseSelect(selectName) {
      setTimeout(() => {
        this.$refs[selectName]?.deactivate();
      }, 0);
    },
  },
  mounted() {
    this.fetchTasks();
    this.fetchUsers();
    this.fetchSolutions();
  }
};
</script>

<style scoped>
.page-container {
  padding: 20px;
  max-width: 1152px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  color: #2c3e50;
  font-size: 28px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
}

.filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  background: #f8f9fa;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 20px;
  border: 1px solid #e9ecef;
}

.filters .floating-label,
.modal-body .floating-label {
  position: relative;
  margin-bottom: 20px;
  background-color: #f8f9fa;
}

.filters .floating-label input,
.modal-body .floating-label input {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  outline: none;
  font-size: 14px;
  color: #333;
  box-sizing: border-box;
  background-color: #f8f9fa;
  transition: all 0.25s ease;
}

.filters .floating-label label,
.modal-body .floating-label label {
  position: absolute;
  left: 16px;
  top: 14px; 
  font-size: 14px;
  color: rgba(0,0,0,0.5);
  pointer-events: none;
  padding: 0 4px;
  transition: all 0.25s ease;
  background-color: #f8f9fa;
  z-index: 2;
}

.filters .floating-label input:focus + label,
.filters .floating-label input:not(:placeholder-shown) + label,
.modal-body .floating-label input:focus + label,
.modal-body .floating-label input:not(:placeholder-shown) + label {
  top: -8px; 
  left: 12px;
  font-size: 12px;
  color: #2f80ed;
  background-color: #f8f9fa;
  padding: 0 4px;
  z-index: 3;
}

.filters .floating-label input:focus,
.modal-body .floating-label input:focus {
  border-color: #2f80ed;
  box-shadow: 0 0 0 2px rgba(47, 128, 237, 0.1);
}

.filters .multiselect-floating,
.modal-body .multiselect-floating {
  position: relative;
}

.filters .multiselect-floating label,
.modal-body .multiselect-floating label {
  position: absolute;
  left: 16px;
  top: 14px;
  font-size: 14px;
  color: rgba(0,0,0,0.5);
  pointer-events: none;
  padding: 0 4px;
  transition: all 0.25s ease;
  background-color: #f8f9fa;
  z-index: 2;
}

.filters .multiselect-floating.active label,
.modal-body .multiselect-floating.active label {
  top: -8px;
  left: 12px;
  font-size: 12px;
  color: #2f80ed;
  background-color: #f8f9fa;
  z-index: 2;
}

.multiselect-floating :deep(.multiselect),
.multiselect-floating :deep(.multiselect__tags),
.multiselect-floating :deep(.multiselect__content-wrapper) {
  z-index: auto !important;    
}

.multiselect-floating :deep(.multiselect__content-wrapper) {
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  margin-top: 4px;
  z-index: 1000 !important; 
}

.custom-multiselect :deep(.multiselect__content-wrapper) {
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  margin-top: 4px;
  z-index: 1000 !important; 
}

.filter-actions {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.apply-btn, .reset-btn {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  height: fit-content;
}

.apply-btn {
  background: #2f80ed;
  color: white;
}

.apply-btn:hover {
  background: #256bcc;
  transform: translateY(-1px);
}

.reset-btn {
  background: #6c757d;
  color: white;
}

.reset-btn:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

.stats-container {
  margin: 16px 0;
  padding: 0 8px;
}

.stats {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
  background: #f8f9fa;
  padding: 8px 16px;
  border-radius: 6px;
  display: inline-block;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #7f8c8d;
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
  padding: 60px 20px;
  color: #7f8c8d;
}

.empty-state i {
  font-size: 3rem;
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
  border: 1px solid #e9ecef;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
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
  padding: 16px;
  border-bottom: 2px solid #e9ecef;
}

.solutions-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #e9ecef;
  vertical-align: middle;
}

.solutions-table tr:hover td {
  background-color: #f8f9fa;
}

.task-col { min-width: 200px; }
.user-col { min-width: 150px; }
.status-col { min-width: 120px; }
.date-col { min-width: 180px; white-space: nowrap; }
.code-col { min-width: 80px; }

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

.btn-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  background: transparent;
}

.code-btn { 
  color: #2f80ed; 
}

.code-btn:hover { 
  background-color: rgba(47, 128, 237, 0.1); 
}

.copy-btn { 
  color: #2f80ed; 
}

.copy-btn:hover { 
  background-color: rgba(47, 128, 237, 0.1); 
}

.close-btn { 
  color: #e74c3c; 
}

.close-btn:hover { 
  background-color: rgba(231, 76, 60, 0.1); 
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding: 20px 0;
  border-top: 1px solid #e9ecef;
}

.pagination-info { font-size: 14px; color: #7f8c8d; }
.pagination-controls { display: flex; align-items: center; gap: 12px; }
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

.pagination-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.pagination-btn:hover:not(:disabled) {
  background-color: #f8f9fa;
  border-color: #3498db;
  color: #3498db;
}
.page-indicator { font-size: 14px; color: #555; font-weight: 500; }

.modal-backdrop {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000; backdrop-filter: blur(3px);
  padding: 20px;
}

.modal-dialog {
  position: relative;
  background: #f8f9fa;
  border-radius: 12px;
  width: 800px; 
  max-width: calc(100% - 40px); 
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0,0,0,0.2);
  animation: modalFadeIn 0.3s ease;
}

@keyframes modalFadeIn {
  from { opacity: 0; transform: scale(0.9) translateY(-20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background: #f8f9fa;
  z-index: 10;
}

.modal-header h3 {
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.modal-body { 
  padding: 24px; 
  box-sizing: border-box; 
  overflow: visible !important;
}

.modal-actions {
  display: flex;
  gap: 8px;
}

.custom-multiselect :deep(.multiselect) { min-height: 48px; margin-top: 0; }
.custom-multiselect :deep(.multiselect__tags) {
  min-height: 48px; padding: 12px 40px 0 16px;
  border: 1px solid #ddd; border-radius: 8px; background: inherit; font-size: 14px;
}
.custom-multiselect :deep(.multiselect__tags:focus-within) {
  border-color: #2f80ed; box-shadow: 0 0 0 2px rgba(47,128,237,0.1); outline: none;
}
.custom-multiselect :deep(.multiselect__input),
.custom-multiselect :deep(.multiselect__single) {
  font-size: 14px; padding: 4px 0; margin: 0; background: transparent; border: none;
}
.custom-multiselect :deep(.multiselect__placeholder) {
  color: rgba(0,0,0,0.5); font-size: 14px; margin-top: 2px;
}
.custom-multiselect :deep(.multiselect__select) {
  height: 46px; right: 6px; top: 1px; width: 30px; background: transparent;
  border-radius: 0 8px 8px 0;
}
.custom-multiselect :deep(.multiselect__select:before) {
  content: ''; position: absolute; top: 50%; left: 50%;
  transform: translate(-50%,-50%); width: 0; height: 0;
  border-style: solid; border-width: 6px 5px 0 5px;
  border-color: #666 transparent transparent transparent; transition: transform 0.2s ease;
}
.custom-multiselect :deep(.multiselect--active .multiselect__select:before) {
  transform: translate(-50%,-50%) rotate(180deg);
}
.custom-multiselect :deep(.multiselect__select:hover) { background: rgba(0,0,0,0.05); }
.custom-multiselect :deep(.multiselect__select:hover:before) { border-color: #333 transparent transparent transparent; }
.custom-multiselect :deep(.multiselect--active .multiselect__select) { 
  background: rgba(47, 128, 237, 0.05); 
}
.custom-multiselect :deep(.multiselect__content-wrapper) {
  border: 1px solid #ddd; border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1); margin-top: 4px; z-index: 10;
}
.custom-multiselect :deep(.multiselect__option) { padding: 10px 12px; font-size: 14px; min-height: 40px; }
.custom-multiselect :deep(.multiselect__option--selected) { 
  background-color: rgba(47, 128, 237, 0.1); 
  color: #2f80ed; 
  font-weight: 500;
}
.custom-multiselect :deep(.multiselect__option--highlight) { 
  background: #2f80ed;
  color: white; 
}
.custom-multiselect :deep(.multiselect__option--selected.multiselect__option--highlight) { 
  background: #256bcc; 
  color: white; 
}
.multiselect-floating.active :deep(.multiselect__placeholder) { display: none; }

@media (max-width: 768px) {
  .page-container {
    padding: 15px;
  }

  .filters {
    grid-template-columns: 1fr;
    padding: 20px;
    gap: 16px;
  }

  .filter-actions {
    flex-direction: column;
    gap: 10px;
  }

  .apply-btn, .reset-btn {
    width: 100%;
    justify-content: center;
  }

  .pagination-container {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }

  .modal-dialog {
    width: 95%;
    margin: 20px;
  }
}

@media (max-width: 480px) {
  .page-header h1 {
    font-size: 24px;
  }

  .filters {
    padding: 16px;
  }

  .solutions-table {
    font-size: 13px;
  }

  .solutions-table th,
  .solutions-table td {
    padding: 12px 8px;
  }

  .btn-icon {
    width: 32px;
    height: 32px;
  }
}
</style>