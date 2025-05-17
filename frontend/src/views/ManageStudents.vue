<template>
  <div class="manage-students-container">
    <h1>Управление студентами</h1>

    <div class="controls">
      <div class="search-filters">
        <input
          v-model="searchParams.firstName"
          placeholder="Имя"
          class="filter-input"
        >
        <input
          v-model="searchParams.lastName"
          placeholder="Фамилия"
          class="filter-input"
        >
        <input
          v-model="searchParams.email"
          placeholder="Email"
          class="filter-input"
        >
        <select v-model="searchParams.groupName" class="filter-select">
          <option value="">Все группы</option>
          <option v-for="group in groups" :value="group.name" :key="group.id">
            {{ group.name }}
          </option>
        </select>
        <button @click="fetchStudents" class="search-btn">Поиск</button>
      </div>

      <button @click="showAddStudentModal = true" class="add-btn">
        Добавить студента
      </button>
    </div>

    <div class="students-table">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Имя</th>
            <th>Фамилия</th>
            <th>Email</th>
            <th>Группа</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="student in students" :key="student.id">
            <td>{{ student.id }}</td>
            <td>{{ student.firstName }}</td>
            <td>{{ student.lastName }}</td>
            <td>{{ student.email }}</td>
            <td>{{ student.groupName }}</td>
            <td class="actions">
              <button @click="editStudent(student)" class="edit-btn">✏️</button>
              <button @click="confirmDelete(student.id)" class="delete-btn">🗑️</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="pagination">
        <button
          @click="prevPage"
          :disabled="currentPage === 0"
          class="page-btn"
        >
          Назад
        </button>
        <span>Страница {{ currentPage + 1 }} из {{ totalPages }}</span>
        <button
          @click="nextPage"
          :disabled="currentPage >= totalPages - 1"
          class="page-btn"
        >
          Вперед
        </button>
      </div>
    </div>

    <!-- Модальное окно добавления/редактирования -->
    <div v-if="showAddStudentModal" class="modal">
      <div class="modal-content">
        <span class="close" @click="closeModal">&times;</span>
        <h2>{{ editingStudent ? 'Редактировать студента' : 'Добавить студента' }}</h2>

        <form @submit.prevent="submitStudent">
          <div class="form-group">
            <label>Имя:</label>
            <input v-model="formData.firstName" required>
          </div>
          <div class="form-group">
            <label>Фамилия:</label>
            <input v-model="formData.lastName" required>
          </div>
          <div class="form-group">
            <label>Email:</label>
            <input v-model="formData.email" type="email" required>
          </div>
          <div class="form-group">
            <label>Пароль:</label>
            <input
              v-model="formData.password"
              type="password"
              :required="!editingStudent"
            >
          </div>
          <div class="form-group">
            <label>Группа:</label>
            <select v-model="formData.groupId" required>
              <option
                v-for="group in groups"
                :value="group.id"
                :key="group.id"
              >
                {{ group.name }}
              </option>
            </select>
          </div>

          <div class="form-actions">
            <button type="button" @click="closeModal" class="cancel-btn">
              Отмена
            </button>
            <button type="submit" class="submit-btn">
              {{ editingStudent ? 'Сохранить' : 'Добавить' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Модальное окно подтверждения удаления -->
    <div v-if="showDeleteConfirm" class="modal">
      <div class="modal-content confirm-modal">
        <p>Вы уверены, что хотите удалить этого студента?</p>
        <div class="confirm-actions">
          <button @click="deleteStudent" class="confirm-btn">Да, удалить</button>
          <button @click="showDeleteConfirm = false" class="cancel-btn">Отмена</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAccessToken } from '@/js/auth';

export default {
  data() {
    return {
      students: [],
      groups: [],
      currentPage: 0,
      pageSize: 10,
      totalPages: 1,
      searchParams: {
        firstName: '',
        lastName: '',
        email: '',
        groupName: '',
        role: 'ROLE_STUDENT'
      },
      showAddStudentModal: false,
      showDeleteConfirm: false,
      editingStudent: null,
      studentToDelete: null,
      formData: {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        groupId: ''
      }
    };
  },
  async created() {
    await this.fetchStudents();
    await this.fetchGroups();
  },
  methods: {
    async fetchStudents() {
      const token = getAccessToken();
      const queryParams = new URLSearchParams({
        ...this.searchParams,
        pageNumber: this.currentPage,
        pageSize: this.pageSize
      }).toString();

      try {
        const response = await fetch(`http://localhost:8080/api/users?${queryParams}`, {
          headers: { Authorization: `Bearer ${token}` }
        });

        if (response.ok) {
          const data = await response.json();
          this.students = data.content;
          this.totalPages = data.totalPages;
        } else {
          console.error('Ошибка при загрузке студентов');
        }
      } catch (error) {
        console.error('Ошибка:', error);
      }
    },

    async fetchGroups() {
      const token = getAccessToken();
      try {
        const response = await fetch('http://localhost:8080/api/groups', {
          headers: { Authorization: `Bearer ${token}` }
        });

        if (response.ok) {
          this.groups = await response.json();
        }
      } catch (error) {
        console.error('Ошибка при загрузке групп:', error);
      }
    },

    async submitStudent() {
      const token = getAccessToken();
      const url = this.editingStudent
        ? `http://localhost:8080/api/users/${this.editingStudent.id}`
        : 'http://localhost:8080/api/users';

      const method = this.editingStudent ? 'PUT' : 'POST';

      try {
        const response = await fetch(url, {
          method,
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify(this.formData)
        });

        if (response.ok) {
          this.closeModal();
          await this.fetchStudents();
        } else {
          console.error('Ошибка при сохранении студента');
        }
      } catch (error) {
        console.error('Ошибка:', error);
      }
    },

    async deleteStudent() {
      const token = getAccessToken();
      try {
        const response = await fetch(`http://localhost:8080/api/users/${this.studentToDelete}`, {
          method: 'DELETE',
          headers: { Authorization: `Bearer ${token}` }
        });

        if (response.ok) {
          this.showDeleteConfirm = false;
          await this.fetchStudents();
        }
      } catch (error) {
        console.error('Ошибка при удалении студента:', error);
      }
    },

    editStudent(student) {
      this.editingStudent = student;
      this.formData = {
        firstName: student.firstName,
        lastName: student.lastName,
        email: student.email,
        password: '',
        groupId: student.groupId
      };
      this.showAddStudentModal = true;
    },

    confirmDelete(id) {
      this.studentToDelete = id;
      this.showDeleteConfirm = true;
    },

    closeModal() {
      this.showAddStudentModal = false;
      this.editingStudent = null;
      this.resetForm();
    },

    resetForm() {
      this.formData = {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        groupId: ''
      };
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
    }
  }
};
</script>

<style scoped>
.manage-students-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  color: #2c3e50;
  margin-bottom: 20px;
}

.controls {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.search-filters {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-input, .filter-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.filter-select {
  min-width: 150px;
}

.search-btn, .add-btn, .page-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.2s;
}

.search-btn {
  background-color: #2f80ed;
  color: white;
}

.search-btn:hover {
  background-color: #1e63c5;
}

.add-btn {
  background-color: #27ae60;
  color: white;
}

.add-btn:hover {
  background-color: #219653;
}

.students-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

th, td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

th {
  background-color: #f2f2f2;
  font-weight: 600;
}

tr:hover {
  background-color: #f5f5f5;
}

.actions {
  display: flex;
  gap: 10px;
}

.edit-btn, .delete-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 5px;
}

.edit-btn:hover {
  color: #2f80ed;
}

.delete-btn:hover {
  color: #e74c3c;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.page-btn {
  padding: 6px 12px;
  background-color: #f0f0f0;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Модальные окна */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  padding: 25px;
  border-radius: 8px;
  width: 100%;
  max-width: 500px;
  position: relative;
}

.confirm-modal {
  text-align: center;
}

.close {
  position: absolute;
  top: 15px;
  right: 20px;
  font-size: 24px;
  cursor: pointer;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

.form-group input, .form-group select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-actions, .confirm-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-btn {
  background-color: #f0f0f0;
  color: #333;
}

.cancel-btn:hover {
  background-color: #e0e0e0;
}

.submit-btn, .confirm-btn {
  background-color: #2f80ed;
  color: white;
}

.submit-btn:hover, .confirm-btn:hover {
  background-color: #1e63c5;
}

@media (max-width: 768px) {
  .controls {
    flex-direction: column;
  }

  .search-filters {
    flex-direction: column;
  }

  .modal-content {
    width: 90%;
    padding: 20px 15px;
  }
}
</style>
