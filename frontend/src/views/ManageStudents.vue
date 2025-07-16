<template>
  <div class="manage-students-container">
    <div class="header">
      <h1><i class="fas fa-user-graduate"></i> Управление студентами</h1>
      <div class="stats" v-if="students.length">
        Показано {{ students.length }} из {{ totalElements }} студентов
      </div>
    </div>

    <form class="filters" @submit.prevent="onSearch">
      <div class="filter-group">
        <label>
          <span>Имя:</span>
          <input type="text" v-model="searchParams.firstName" class="text-input" placeholder="Поиск по имени">
        </label>
      </div>

      <div class="filter-group">
        <label>
          <span>Фамилия:</span>
          <input type="text" v-model="searchParams.lastName" class="text-input" placeholder="Поиск по фамилии">
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

    <div v-else-if="!students.length" class="empty-state">
      <i class="fas fa-user-slash"></i>
      <h3>Студенты не найдены</h3>
      <p>Попробуйте изменить параметры поиска</p>
    </div>

    <div v-else class="table-container">
      <table class="students-table">
        <thead>
          <tr>
            <th class="id-col">№</th>
            <th class="name-col">ФИО</th>
            <th class="email-col">Email</th>
            <th class="group-col">Группа</th>
            <th class="actions-col">Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(student, index) in students" :key="student.id">
            <td class="id-col">{{ currentPage * pageSize + index + 1 }}</td>
            <td class="name-col">
              {{ student.lastName }} {{ student.firstName }}
            </td>
            <td class="email-col">
              <a :href="`mailto:${student.email}`">{{ student.email }}</a>
            </td>
            <td class="group-col">
              <span :class="{'no-group': !student.groupName}">
                {{ student.groupName || 'Не указана' }}
              </span>
            </td>
            <td class="actions-col">
              <div class="action-buttons">
                <button @click="openEditModal(student)" class="btn-icon edit-btn" title="Редактировать">
                  <i class="fas fa-pencil-alt"></i>
                </button>
                <button @click="deleteStudent(student.id)" class="btn-icon delete-btn" title="Удалить">
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-container" v-if="totalPages > 1">
      <div class="pagination-info">
        Страница {{ currentPage + 1 }} из {{ totalPages }}
      </div>
      <div class="pagination-controls">
        <button 
          @click="prevPage" 
          :disabled="currentPage === 0" 
          class="pagination-btn"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        <div class="page-indicator">
          Страница {{ currentPage + 1 }} из {{ totalPages }}
        </div>
        <button 
          @click="nextPage" 
          :disabled="currentPage >= totalPages - 1" 
          class="pagination-btn"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>

    <div v-if="editingStudent" class="modal-backdrop">
      <div class="modal-dialog">
        <div class="modal-header">
          <h3><i class="fas fa-user-edit"></i> Редактирование студента</h3>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>Имя</label>
            <input v-model="editingStudent.firstName" class="form-input">
          </div>
          <div class="form-group">
            <label>Фамилия</label>
            <input v-model="editingStudent.lastName" class="form-input">
          </div>
          <div class="form-group">
            <label>Email</label>
            <input v-model="editingStudent.email" type="email" class="form-input">
          </div>
          <div class="form-group">
            <label>Группа</label>
            <select v-model="editingStudent.groupId" class="form-input">
              <option disabled value="">Выберите группу</option>
              <option 
                v-for="group in groups" 
                :key="group.id" 
                :value="group.id"
                :selected="editingStudent.groupId === group.id"
              >
                {{ group.name }}
              </option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeModal" class="btn-cancel">Отмена</button>
          <button @click="saveStudent" class="btn-save">Сохранить изменения</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { MANAGER_URL, fetchGroups } from "../js/auth";

export default {
  data() {
    return {
      students: [],
      groups: [],
      currentPage: 0,
      pageSize: 10,
      totalPages: 1,
      totalElements: 0,
      loading: false,
      editingStudent: null,
      searchParams: {
        firstName: '',
        lastName: '',
        role: 'ROLE_STUDENT'
      }
    };
  },
  async created() {
    await this.fetchStudents();
    await this.fetchGroups();
  },
  methods: {
    async fetchStudents() {
      this.loading = true;
      try {
        const params = {
          pageNumber: this.currentPage,
          pageSize: this.pageSize,
          role: this.searchParams.role
        };
        if (this.searchParams.firstName) params.firstName = this.searchParams.firstName;
        if (this.searchParams.lastName) params.lastName = this.searchParams.lastName;

        const query = new URLSearchParams(params).toString();
        const response = await fetch(`${MANAGER_URL}/users?${query}`, {
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include'
        });

        if (!response.ok) throw new Error(await response.text());

        const data = await response.json();
        this.students = data.content;
        this.totalPages = data.page.totalPages;
        this.totalElements = data.page.totalElements;
      } catch  {
        this.$toast?.error("Ошибка при загрузке студентов");
      } finally {
        this.loading = false;
      }
    },
    async fetchGroups() {
      this.groups = await fetchGroups();
    },
    onSearch() {
      this.currentPage = 0;
      this.fetchStudents();
    },
    resetSearch() {
      this.searchParams.firstName = '';
      this.searchParams.lastName = '';
      this.currentPage = 0;
      this.fetchStudents();
    },
    nextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++;
        this.fetchStudents();
      }
    },
    prevPage() {
      if (this.currentPage > 0) {
        this.currentPage--;
        this.fetchStudents();
      }
    },
    openEditModal(student) {
      this.editingStudent = { 
        ...student,
        groupId: this.groups.find(g => g.name === student.groupName)?.id || null
      };
    },
    closeModal() {
      this.editingStudent = null;
    },
    async saveStudent() {
      try {
        const { id, firstName, lastName, email, groupId } = this.editingStudent;
        const response = await fetch(`${MANAGER_URL}/users/${id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({ firstName, lastName, email, groupId })
        });
        if (!response.ok) throw new Error(await response.text());

        this.$toast?.success("Данные обновлены");
        this.closeModal();
        this.fetchStudents();
      } catch{
        this.$toast?.error("Ошибка при обновлении");
      }
    },
    async deleteStudent(id) {
      if (!confirm("Вы уверены, что хотите удалить этого студента?")) return;
      try {
        const response = await fetch(`${MANAGER_URL}/users/${id}`, {
          method: 'DELETE',
          credentials: 'include'
        });
        if (!response.ok) throw new Error(await response.text());

        this.$toast?.success("Студент удален");
        this.fetchStudents();
      } catch {
        this.$toast?.error("Ошибка при удалении");
      }
    }
  }
};
</script>

<style scoped>
.manage-students-container {
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
  display: flex;
  align-items: center;
  gap: 0.75rem;
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

.text-input {
  padding: 8px 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
  background: white;
  width: 100%;
  box-sizing: border-box;
}

.text-input:focus {
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
  display: flex;
  align-items: center;
  gap: 6px;
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
  border: 1px solid #eee;
}

.students-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.students-table th {
  background: #f8f9fa;
  color: #555;
  font-weight: 600;
  text-align: left;
  padding: 14px 16px;
  border-bottom: 2px solid #eee;
}

.students-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
  vertical-align: middle;
}

.students-table tr:hover td {
  background-color: #f8f9fa;
}

.id-col {
  min-width: 80px;
  color: #7f8c8d;
}

.name-col {
  min-width: 200px;
  align-items: center;
}

.email-col a {
  color: #3498db;
  text-decoration: none;
}

.email-col a:hover {
  text-decoration: underline;
}

.group-col .no-group {
  color: #95a5a6;
  font-style: italic;
}

.actions-col {
  min-width: 120px;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
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

.edit-btn {
  color: #3498db;
}

.edit-btn:hover {
  background-color: rgba(52, 152, 219, 0.1);
}

.delete-btn {
  color: #e74c3c;
}

.delete-btn:hover {
  background-color: rgba(231, 76, 60, 0.1);
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

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(3px);
}

.modal-dialog {
  background: white;
  border-radius: 12px;
  width: 500px;
  max-width: calc(100% - 40px); 
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  animation: modalFadeIn 0.3s ease;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background: white;
  z-index: 10;
}

.modal-body {
  padding: 1.5rem;
  box-sizing: border-box;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  color: #555;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  box-sizing: border-box; 
  max-width: 100%; 
}

.form-input:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
  outline: none;
}

select.form-input {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 1rem center;
  background-size: 1em;
}

.modal-footer {
  padding: 1.5rem;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  position: sticky;
  bottom: 0;
  background: white;
}

.btn-cancel, .btn-save {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-cancel {
  background-color: #f8f9fa;
  color: #333;
  border: 1px solid #ddd;
}

.btn-cancel:hover {
  background-color: #e9ecef;
}

.btn-save {
  background-color: #2ecc71;
  color: white;
  border: none;
}

.btn-save:hover {
  background-color: #27ae60;
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
  
  .students-table {
    font-size: 13px;
  }
  
  .students-table th,
  .students-table td {
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

@media (max-width: 480px) {
  .manage-students-container {
    padding: 1rem;
  }
  
  .name-col {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
}
</style>