<template>
  <div class="manage-students-container">
    <h1>Управление студентами</h1>

    <div class="controls">
      <div class="search-filters">
        <input v-model="searchParams.firstName" placeholder="Имя" class="filter-input">
        <input v-model="searchParams.lastName" placeholder="Фамилия" class="filter-input">
        <button @click="fetchStudents" class="search-btn">Поиск</button>
        <button @click="resetSearch" class="reset-btn">Сбросить</button>
      </div>
    </div>

    <div v-if="loading" class="loading-indicator">Загрузка...</div>

    <div v-else class="students-table">
      <div v-if="students.length === 0" class="no-results">
        Студенты не найдены
      </div>

      <table v-else>
        <thead>
          <tr>
            <th>ID</th>
            <th>ФИО</th>
            <th>Email</th>
            <th>Группа</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="student in students" :key="student.id">
            <td>{{ student.id }}</td>
            <td>{{ student.lastName }} {{ student.firstName }}</td>
            <td>{{ student.email }}</td>
            <td>{{ student.groupName || 'Не указана' }}</td>
          </tr>
        </tbody>
      </table>

      <div class="pagination">
        <button @click="prevPage" :disabled="currentPage === 0" class="page-btn">
          Назад
        </button>
        <span>Страница {{ currentPage + 1 }} из {{ totalPages }}</span>
        <button @click="nextPage" :disabled="currentPage >= totalPages - 1" class="page-btn">
          Вперед
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      students: [],
      currentPage: 0,
      pageSize: 10,
      totalPages: 1,
      loading: false,
      searchParams: {
        firstName: '',
        lastName: '',
        role: 'ROLE_STUDENT'
      }
    };
  },
  async created() {
    await this.fetchStudents();
  },
  methods: {
    async fetchStudents() {
      this.loading = true;

      try {
        const query = new URLSearchParams({
          firstName: this.searchParams.firstName,
          lastName: this.searchParams.lastName,
          role: 'ROLE_STUDENT',
          pageNumber: this.currentPage,
          pageSize: this.pageSize
        }).toString();

        const response = await fetch(`/api/users?${query}`, {
          headers: {
            'Content-Type': 'application/json'
          },
          credentials: 'include',
        });

        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}));
          throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        this.students = data.content;
        this.totalPages = data.totalPages;
      } catch (error) {
        console.error('Fetch error:', error);
        this.$toast.error(error.message.includes('Failed to fetch')
          ? 'Не удалось подключиться к серверу'
          : error.message);
      } finally {
        this.loading = false;
      }
    },

    resetSearch() {
      this.searchParams = {
        firstName: '',
        lastName: '',
        role: 'ROLE_STUDENT'
      };
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
  margin-bottom: 24px;
  font-size: 28px;
}

.controls {
  display: flex;
  justify-content: space-between;
  margin-bottom: 24px;
  gap: 16px;
  flex-wrap: wrap;
}

.search-filters {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-input {
  padding: 10px 14px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  min-width: 200px;
}

.search-btn, .reset-btn {
  padding: 10px 16px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.search-btn {
  background-color: #4a89dc;
  color: white;
}

.search-btn:hover {
  background-color: #3a70c2;
}

.reset-btn {
  background-color: #f5f7fa;
  border: 1px solid #ddd;
}

.reset-btn:hover {
  background-color: #e6e9ed;
}

.loading-indicator, .no-results {
  padding: 40px;
  text-align: center;
  color: #666;
  font-size: 16px;
}

.students-table {
  margin-top: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

th, td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #555;
}

tr:hover {
  background-color: #f8f9fa;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
}

.page-btn {
  padding: 8px 16px;
  background-color: #f5f7fa;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .controls {
    flex-direction: column;
  }

  .search-filters {
    width: 100%;
  }

  .filter-input {
    width: 100%;
  }
}
</style>
