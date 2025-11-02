<template>
  <div class="manage-container">
    <h1>Управление контрольными и заданиями</h1>

    <div class="tabs">
      <button :class="{ active: isContestsActive }" @click="isContestsActive = true">Контрольные</button>
      <button :class="{ active: !isContestsActive }" @click="isContestsActive = false">Задания</button>
    </div>

    <!-- Контрольные -->
    <div v-if="isContestsActive" class="management-section">
      <form class="filters" @submit.prevent="fetchContests(0)">
        <div class="filter-group floating-label">
          <FloatingInput
            v-model="contestSearchParams.name"
            id="contestName"
            label="Название"
            type="text"
            placeholder=""
          />
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
        <div class="stats-container" v-if="contests.length">
          <div class="stats">
            Показано {{ contests.length }} из {{ contestPage.totalElements }} контрольных
          </div>
        </div>
        <ul class="items-list">
          <li v-for="contest in contests" :key="contest.id" class="item">
            <div class="item-content">
              <div class="item-info">
                <div class="item-title">{{ contest.name }}</div>
                <div class="item-description">{{ contest.description }}</div>
              </div>
              <div class="item-actions">
                <button
                  class="btn-icon edit-btn"
                  @click="editContest(contest)"
                  title="Редактировать контрольную"
                >
                  <i class="fas fa-pencil-alt"></i>
                </button>
                <button
                  class="btn-icon delete-btn"
                  @click="confirmDeleteContest(contest)"
                  title="Удалить контрольную"
                >
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
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
          <FloatingInput
            v-model="taskSearchParams.name"
            id="taskName"
            label="Название"
            type="text"
            placeholder=""
          />
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
        <div class="stats-container" v-if="tasks.length">
          <div class="stats">
            Показано {{ tasks.length }} из {{ taskPage.totalElements }} заданий
          </div>
        </div>
        <ul class="items-list">
          <li v-for="task in tasks" :key="task.id" class="item">
            <div class="item-content">
              <div class="item-info">
                <div class="item-title">{{ task.name }}</div>
                <div class="item-description">{{ task.description }}</div>
              </div>
              <div class="item-actions">
                <button
                  class="btn-icon edit-btn"
                  @click="editTask(task)"
                  title="Редактировать задание"
                >
                  <i class="fas fa-pencil-alt"></i>
                </button>
                <button
                  class="btn-icon delete-btn"
                  @click="confirmDeleteTask(task)"
                  title="Удалить задание"
                >
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </div>
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

    <ConfirmDialog
      v-if="showConfirmDialog"
      :title="confirmDialog.title"
      :message="confirmDialog.message"
      @confirm="executeDelete"
      @cancel="cancelDelete"
    />

    <Notification ref="notification" />
  </div>
</template>

<script>
import { 
  listContests, 
  deleteContest, 
  listTasks, 
  deleteTask 
} from "@/js/manager";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import Notification from "@/components/Notification.vue";
import FloatingInput from "@/components/FloatingInput.vue";

export default {
  components: {
    ConfirmDialog,
    Notification,
    FloatingInput
  },
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
      },
      showConfirmDialog: false,
      confirmDialog: {
        title: '',
        message: ''
      },
      itemToDelete: null,
      deleteType: null
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
        
        const data = await listContests(params);
        this.contests = data.content || [];
        this.contestPage = {
          number: data.page.number,
          totalPages: data.page.totalPages,
          totalElements: data.page.totalElements
        };
      } catch (error) {
        console.error('Ошибка загрузки контрольных:', error);
        this.$refs.notification.show('Не удалось загрузить контрольные', 'error');
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
        
        const data = await listTasks(params);
        this.tasks = data.content || [];
        this.taskPage = {
          number: data.page.number,
          totalPages: data.page.totalPages,
          totalElements: data.page.totalElements
        };
      } catch (error) {
        console.error('Ошибка загрузки заданий:', error);
        this.$refs.notification.show('Не удалось загрузить задания', 'error');
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
      this.$router.push("/manage-contests/create-contest");
    },
    goToCreateTask() {
      this.$router.push("/manage-contests/create-task");
    },
    editContest(contest) {
      this.$router.push(`/manage-contests/edit-contest/${contest.id}`);
    },
    editTask(task) {
      this.$router.push(`/manage-contests/edit-task/${task.id}`);
    },
    confirmDeleteContest(contest) {
      this.itemToDelete = contest;
      this.deleteType = 'contest';
      this.confirmDialog = {
        title: 'Удаление контрольной',
        message: `Вы уверены, что хотите удалить контрольную "${contest.name}"? Это действие нельзя отменить.`
      };
      this.showConfirmDialog = true;
    },
    confirmDeleteTask(task) {
      this.itemToDelete = task;
      this.deleteType = 'task';
      this.confirmDialog = {
        title: 'Удаление задания',
        message: `Вы уверены, что хотите удалить задание "${task.name}"? Это действие нельзя отменить.`
      };
      this.showConfirmDialog = true;
    },
    async executeDelete() {
      try {
        if (this.deleteType === 'contest') {
          await deleteContest(this.itemToDelete.id);
          this.$refs.notification.show('Контрольная успешно удалена', 'success');
          this.fetchContests(this.contestPage.number);
        } else if (this.deleteType === 'task') {
          await deleteTask(this.itemToDelete.id);
          this.$refs.notification.show('Задание успешно удалено', 'success');
          this.fetchTasks(this.taskPage.number);
        }
      } catch (error) {
        console.error('Ошибка при удалении:', error);
        this.$refs.notification.show('Ошибка при удалении', 'error');
      } finally {
        this.showConfirmDialog = false;
        this.itemToDelete = null;
        this.deleteType = null;
      }
    },
    cancelDelete() {
      this.showConfirmDialog = false;
      this.itemToDelete = null;
      this.deleteType = null;
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

.filters :deep(.floating-label input) {
  background-color: #f8f9fa !important;
}

.filters :deep(.floating-label label) {
  background-color: #f8f9fa !important;
}

.management-section {
  margin-top: 20px;
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

.item-content {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background-color: #f5f7fa;
  border-radius: 8px;
  transition: all 0.2s ease;
  text-align: left;
}

.item-content:hover {
  background-color: #e2e6ed;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
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

@media (max-width: 768px) {
  .manage-container {
    padding: 15px;
  }

  h1 {
    font-size: 24px;
    margin-bottom: 15px;
    padding: 0 10px;
  }

  .tabs {
    flex-wrap: wrap;
    padding: 0 10px;
  }

  .tabs button {
    flex: 1;
    min-width: 120px;
    padding: 8px 10px;
    font-size: 14px;
  }

  .filters {
    grid-template-columns: 1fr;
    gap: 16px;
    padding: 20px;
    margin: 0 10px 20px 10px;
  }

  .filter-actions {
    flex-direction: column;
    gap: 8px;
    width: 100%;
  }

  .apply-btn,
  .reset-btn {
    width: 100%;
    justify-content: center;
    height: 36px;
  }

  .items-list {
    padding: 0 10px;
  }

  .item-content {
    padding: 12px;
    gap: 8px;
  }

  .item-title {
    font-size: 16px;
  }

  .item-description {
    font-size: 13px;
  }

  .item-actions {
    gap: 6px;
  }

  .btn-icon {
    width: 32px;
    height: 32px;
  }

  .empty-state {
    padding: 20px 10px;
  }

  .empty-state i {
    font-size: 2rem;
  }

  .empty-state h3 {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .manage-container {
    padding: 5px;
  }

  .tabs button {
    min-width: 100px;
    font-size: 13px;
  }

  .item-title {
    font-size: 15px;
  }

  .item-content {
    padding: 10px;
    gap: 6px;
  }

  .item-actions {
    gap: 4px;
  }

  .btn-icon {
    width: 28px;
    height: 28px;
  }

  .btn-icon i {
    font-size: 12px;
  }
}
</style>