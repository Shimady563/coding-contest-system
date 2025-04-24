<template>
  <div class="tasks-container">
    <h1>Задания варианта</h1>

    <div v-if="loading" class="loading">Загрузка...</div>

    <div v-else-if="tasks.length === 0" class="empty-state">
      Задания пока не добавлены.
    </div>

    <ul v-else class="task-list">
      <li v-for="task in tasks" :key="task.id" class="task-item">
        <h3>{{ task.name }}</h3>
        <p>{{ task.description }}</p>
        <button @click="goToTask(task)">Открыть</button>
      </li>
    </ul>
  </div>
</template>

<script>
import { getAccessToken } from "@/js/auth";

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
    const token = getAccessToken();

    if (!versionId || !token) {
      console.error("Не удалось получить versionId или токен.");
      this.loading = false;
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/v1/tasks?contestVersionId=${versionId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!response.ok) throw new Error("Ошибка загрузки заданий");

      const result = await response.json();
      this.tasks = result.content;
      console.log(this.tasks)
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
        state: { task },
      });
    },
  },
};
</script>

<style scoped>
.tasks-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

h1 {
  text-align: center;
  font-size: 28px;
  color: #2f2f2f;
  margin-bottom: 30px;
}

.loading, .empty-state {
  text-align: center;
  font-size: 18px;
  color: #777;
}

.task-list {
  list-style: none;
  padding: 0;
}

.task-item {
  padding: 15px;
  margin-bottom: 15px;
  background: #f5f7fa;
  border-radius: 10px;
}

.task-item h3 {
  margin: 0;
  color: #2f80ed;
}

.task-item p {
  margin-top: 5px;
  color: #444;
}
</style>
