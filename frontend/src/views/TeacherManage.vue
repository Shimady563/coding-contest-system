<template>
  <div class="manage-container">
    <h1>Управление контрольными и заданиями</h1>

    <!-- Меню для переключения -->
    <div class="tabs">
      <button :class="{ active: isContestsActive }" @click="isContestsActive = true">Контрольные</button>
      <button :class="{ active: !isContestsActive }" @click="isContestsActive = false">Задания</button>
    </div>

    <!-- Контрольные -->
    <div v-if="isContestsActive" class="management-section">
      <form class="filters" @submit.prevent="fetchContests(0)">
        <div class="filter-group">
          <label>
            <span>Название:</span>
            <input 
              type="text" 
              v-model="contestSearchParams.name" 
              class="text-input" 
              placeholder="Поиск по названию"
            >
          </label>
        </div>

        <div class="filter-actions">
          <button type="submit" class="apply-btn">
            <i class="fas fa-filter"></i> Применить
          </button>
          <button type="button" @click="resetContestSearch" class="reset-btn">
            <i class="fas fa-broom"></i> Сбросить
          </button>
        </div>
      </form>

      <button class="create-btn" @click="goToCreateContest">Создать контрольную</button>
      
      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <span>Загрузка данных...</span>
      </div>

      <div v-else-if="!contests.length" class="empty-state">
        <i class="fas fa-clipboard-list"></i>
        <h3>Контрольные не найдены</h3>
        <p>Попробуйте изменить параметры поиска</p>
      </div>

      <div v-else>
        <div class="stats" v-if="contests.length">
          Показано {{ contests.length }} из {{ contestPage.totalElements }} контрольных
        </div>
        <ul class="items-list">
          <li v-for="contest in contests" :key="contest.id" class="item">
            <div class="item-link">
              <div class="item-title">{{ contest.name }}</div>
              <div class="item-description">{{ contest.description }}</div>
            </div>
          </li>
        </ul>
        <div class="pagination-container" v-if="contestPage.totalPages > 1">
          <div class="pagination-info">
            Страница {{ contestPage.number + 1 }} из {{ contestPage.totalPages }}
          </div>
          <div class="pagination-controls">
            <button 
              @click="changeContestPage(-1)" 
              :disabled="contestPage.number === 0" 
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            <div class="page-indicator">
              Страница {{ contestPage.number + 1 }} из {{ contestPage.totalPages }}
            </div>
            <button 
              @click="changeContestPage(1)" 
              :disabled="contestPage.number + 1 >= contestPage.totalPages" 
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Задания -->
    <div v-else class="management-section">
      <form class="filters" @submit.prevent="fetchTasks(0)">
        <div class="filter-group">
          <label>
            <span>Название:</span>
            <input 
              type="text" 
              v-model="taskSearchParams.name" 
              class="text-input" 
              placeholder="Поиск по названию"
            >
          </label>
        </div>

        <div class="filter-actions">
          <button type="submit" class="apply-btn">
            <i class="fas fa-filter"></i> Применить
          </button>
          <button type="button" @click="resetTaskSearch" class="reset-btn">
            <i class="fas fa-broom"></i> Сбросить
          </button>
        </div>
      </form>

      <button class="create-btn" @click="goToCreateTask">Создать задание</button>
      
      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <span>Загрузка данных...</span>
      </div>

      <div v-else-if="!tasks.length" class="empty-state">
        <i class="fas fa-tasks"></i>
        <h3>Задания не найдены</h3>
        <p>Попробуйте изменить параметры поиска</p>
      </div>

      <div v-else>
        <div class="stats" v-if="tasks.length">
          Показано {{ tasks.length }} из {{ taskPage.totalElements }} заданий
        </div>
        <ul class="items-list">
          <li v-for="task in tasks" :key="task.id" class="item">
            <router-link :to="`/edit-task/${task.id}`" class="item-link">
              <div class="item-title">{{ task.name }}</div>
              <div class="item-description">{{ task.description }}</div>
            </router-link>
          </li>
        </ul>
        <div class="pagination-container" v-if="taskPage.totalPages > 1">
          <div class="pagination-info">
            Страница {{ taskPage.number + 1 }} из {{ taskPage.totalPages }}
          </div>
          <div class="pagination-controls">
            <button 
              @click="changeTaskPage(-1)" 
              :disabled="taskPage.number === 0" 
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            <div class="page-indicator">
              Страница {{ taskPage.number + 1 }} из {{ taskPage.totalPages }}
            </div>
            <button 
              @click="changeTaskPage(1)" 
              :disabled="taskPage.number + 1 >= taskPage.totalPages" 
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { MANAGER_URL } from "@/js/auth";

export default {
  data() {
    return {
      isContestsActive: true,
      contests: [],
      tasks: [],
      loading: false,
      contestSearchParams: {
        name: ''
      },
      taskSearchParams: {
        name: ''
      },
      contestPage: {
        number: 0,
        totalPages: 1,
        totalElements: 0
      },
      taskPage: {
        number: 0,
        totalPages: 1,
        totalElements: 0
      }
    };
  },
  mounted() {
    this.fetchContests(0);
    this.fetchTasks(0);
  },
  methods: {
    async fetchContests(pageNumber = 0) {
      this.loading = true;
      try {
        const params = {
          name: this.contestSearchParams.name.trim(),
          pageNumber,
          pageSize: 10,
        };
        const query = new URLSearchParams(params).toString();

        const response = await fetch(`${MANAGER_URL}/contests?${query}`, {
          credentials: "include"
        });
        if (!response.ok) throw new Error('Не удалось загрузить контрольные');
        const data = await response.json();
        this.contests = data.content || [];
        this.contestPage = {
          number: data.page.number,
          totalPages: data.page.totalPages,
          totalElements: data.page.totalElements
        };
      } catch (error) {
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
    async fetchTasks(pageNumber = 0) {
      this.loading = true;
      try {
        const params = {
          name: this.taskSearchParams.name.trim(),
          pageNumber,
          pageSize: 10,
        };
        const query = new URLSearchParams(params).toString();

        const response = await fetch(`${MANAGER_URL}/tasks?${query}`, {
          credentials: "include"
        });
        if (!response.ok) throw new Error('Не удалось загрузить задания');
        const data = await response.json();
        this.tasks = data.content || [];
        this.taskPage = {
          number: data.page.number,
          totalPages: data.page.totalPages,
          totalElements: data.page.totalElements
        };
      } catch (error) {
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
    resetContestSearch() {
      this.contestSearchParams.name = '';
      this.fetchContests(0);
    },
    resetTaskSearch() {
      this.taskSearchParams.name = '';
      this.fetchTasks(0);
    },
    changeContestPage(offset) {
      const newPage = this.contestPage.number + offset;
      if (newPage >= 0 && newPage < this.contestPage.totalPages) {
        this.fetchContests(newPage);
      }
    },
    changeTaskPage(offset) {
      const newPage = this.taskPage.number + offset;
      if (newPage >= 0 && newPage < this.taskPage.totalPages) {
        this.fetchTasks(newPage);
      }
    },
    goToCreateContest() {
      this.$router.push("/create-contest");
    },
    goToCreateTask() {
      this.$router.push("/create-task");
    }
  }
};
</script>

<style scoped>
.manage-container {
  max-width: 900px;
  margin: 40px auto;
  padding: 20px;
  text-align: center;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  box-sizing: border-box;
  animation: fadeIn 0.4s ease-in-out;
}

h1 {
  font-size: 28px;
  margin-bottom: 20px;
  color: #333;
}

.tabs {
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.tabs button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  border: 1px solid #ddd;
  background-color: #f0f0f0;
  border-radius: 6px;
  transition: all 0.3s;
  margin: 0;
}

.tabs button.active {
  background-color: #2f80ed;
  color: white;
  border-color: #2f80ed;
}

.management-section {
  margin-top: 20px;
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
  text-align: initial;
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

.stats {
  font-size: 14px;
  color: #7f8c8d;
  margin-bottom: 16px;
  text-align: left;
}

.create-btn {
  display: block;
  width: 100%;
  max-width: 300px;
  margin: 0 auto 30px auto;
  padding: 12px 20px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  color: white;
  background-color: #34d399;
}

.create-btn:hover {
  opacity: 0.9;
  background-color: #10b981;
}

.items-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.item {
  margin-bottom: 15px;
}

.item-link {
  display: block;
  padding: 16px;
  background-color: #f5f7fa;
  color: #2d2d2d;
  text-decoration: none;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.2s ease;
  text-align: left;
}

.item-link:hover {
  background-color: #e2e6ed;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.item-title {
  font-size: 18px;
  font-weight: 600;
  color: #2d2d2d;
  margin-bottom: 6px;
}

.item-description {
  font-size: 14px;
  color: #888;
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

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .filters {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .filter-actions {
    width: 100%;
    justify-content: center;
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