<template>
  <div class="tasks-container">
    <h1>Задания варианта</h1>

    <div v-if="loading" class="loading">Загрузка...</div>
    <div v-else-if="tasks.length === 0" class="empty-state">
      Задания пока не добавлены.
    </div>

    <ul v-else class="task-list">
      <li v-for="task in tasks" :key="task.id" class="task-item">
        <div @click="goToTask(task)" class="task-link">
          <div class="item-title">{{ task.name }}</div>
          <div class="item-description">{{ task.description }}</div>
        </div>
      </li>
    </ul>

    <Modal v-if="errorModal" @close="errorModal = false">
      <template #header>
        <h2>Ошибка</h2>
      </template>
      <template #body>
        <p>{{ errorMessage }}</p>
      </template>
      <template #footer>
        <button class="btn" @click="errorModal = false">Ок</button>
      </template>
    </Modal>
  </div>
</template>

<script>
import { getContest, getTasksByContestVersion } from "@/js/api";
import Modal from "@/components/Modal.vue";

export default {
  name: "TasksPage",
  components: { Modal },
  data() {
    return {
      tasks: [],
      loading: true,
      contest: null,
      errorModal: false,
      errorMessage: ''
    };
  },
  async mounted() {
    const versionId = this.$route.params.versionId;
    const contestId = this.$route.params.contestId;
    try {
      this.contest = await getContest(contestId);
    } catch (e) {
      this.showError(e?.message || 'Не удалось загрузить данные контеста');
      return;
    }

    const start = new Date(this?.contest.startTime);
    const end =  new Date(this?.contest.endTime);
    const now = new Date();

    if (now < start || now > end) {
      this.$router.replace('/access-denied-contest');
       return;
    }

    try {
      const result = await getTasksByContestVersion(versionId);
      this.tasks = result ?? [];
    } catch (e) {
      this.showError(e?.message || 'Ошибка загрузки заданий');
    } finally {
      this.loading = false;
    }
  },
  methods: {
    goToTask(task) {
      this.$router.push({
        name: 'StudentContest',
        params: { taskId: task.id },
        query: { versionId: this.$route.params.id },  
      });
    },
    showError(message) {
      this.errorMessage = message;
      this.errorModal = true;
    }
  },
};
</script>

<style scoped>
.tasks-container {
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

.loading, .empty-state {
  text-align: center;
  font-size: 18px;
  color: #777;
}

.task-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.task-item {
  margin-bottom: 15px;
}

.task-link {
  display: block;
  padding: 12px 16px;
  background-color: #f5f7fa;
  color: #2d2d2d;
  text-decoration: none;
  font-weight: 500;
  border-radius: 8px;
  transition: background-color 0.2s ease;
  text-align: left;
  cursor: pointer;
  transition: transform 0.25s ease, 
  box-shadow 0.25s ease, 
  background-color 0.25s ease, 
  color 0.25s ease;
}

.task-link:hover {
  background-color: #e2e6ed;
  color: #1a73e8;
  transform: translateY(-3px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
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
</style>
