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
  </div>
</template>

<script>
export default {
  name: "TasksPage",
  data() {
    return {
      tasks: [],
      loading: true,
    };
  },
  async mounted() {
    const versionId = this.$route.params.id;

    try {
      const response = await fetch(`http://localhost:8080/api/v1/tasks/contest-version?contestVersionId=${versionId}`, {
        credentials: "include"
      });

      if (!response.ok) {
        const errorBody = await response.json().catch(() => null);
        throw new Error(errorBody?.message || "Ошибка загрузки заданий");
      }

      const result = await response.json();
      this.tasks = result ?? [];
    } catch (e) {
      console.error("Ошибка при загрузке заданий:", e.message);
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
}

.task-link:hover {
  background-color: #e2e6ed;
  color: #1a73e8;
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
