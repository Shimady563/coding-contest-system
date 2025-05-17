<template>
  <div v-if="taskData" class="task-container">
    <TaskDescription :description="taskData.description" />
    <TestCases :testCases="taskData.testCases" />
    <CodeEditor ref="codeEditor" />
    <button @click="sendCode">Отправить</button>
    <OutputResults ref="outputResults" :taskId="taskData.id" />

    <div class="navigation-buttons">
      <button @click="goToPrevTask" :disabled="!prevTask">← Назад</button>
      <button @click="goToNextTask" :disabled="!nextTask">Вперед →</button>
    </div>
  </div>
</template>

<script>
import TaskDescription from "../components/TaskDescription.vue";
import TestCases from "../components/TestCases.vue";
import CodeEditor from "../components/CodeEditor.vue";
import OutputResults from "../components/OutputResults.vue";
import { getAccessToken } from "@/js/auth";

export default {
  components: {
    TaskDescription,
    TestCases,
    CodeEditor,
    OutputResults,
  },
  data() {
    return {
      taskData: null,
      tasksList: [],
      loadingTasks: false,
    };
  },
  watch: {
    // Добавляем вотчер для параметров маршрута
    '$route.params.taskId': {
      immediate: true,
      handler(newTaskId) {
        if (newTaskId) {
          this.loadTaskData(newTaskId);
        }
      }
    }
  },
  async created() {
    await this.loadTasksList();
    const taskId = this.$route.params.taskId;
    if (taskId) {
      await this.loadTaskData(taskId);
    }
  },
  computed: {
    currentIndex() {
      if (!this.taskData || !this.tasksList.length) return -1;
      return this.tasksList.findIndex(task => task.id === parseInt(this.taskData.id));
    },
    prevTask() {
      if (this.currentIndex > 0) {
        return this.tasksList[this.currentIndex - 1];
      }
      return null;
    },
    nextTask() {
      if (this.currentIndex >= 0 && this.currentIndex < this.tasksList.length - 1) {
        return this.tasksList[this.currentIndex + 1];
      }
      return null;
    },
  },
  methods: {
    async loadTasksList() {
      const versionId = this.$route.query.versionId;
      const token = getAccessToken();

      if (!versionId || !token) {
        console.error("Не удалось получить необходимые параметры.");
        return;
      }

      try {
        this.loadingTasks = true;
        const response = await fetch(`http://localhost:8080/api/v1/tasks/contest-version?contestVersionId=${versionId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        const result = await response.json();
        this.tasksList = result ?? [];
      } catch (e) {
        console.error("Ошибка при загрузке списка заданий:", e.message);
      } finally {
        this.loadingTasks = false;
      }
    },
    async loadTaskData(taskId) {
      const token = getAccessToken();

      try {
        // Сначала ищем задачу в уже загруженном списке
        const taskFromList = this.tasksList.find(t => t.id === parseInt(taskId));
        if (taskFromList) {
          this.taskData = taskFromList;
          return;
        }

        // Если не нашли в списке, загружаем отдельно
        const response = await fetch(`http://localhost:8080/api/tasks/${taskId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        this.taskData = await response.json();
      } catch (e) {
        console.error("Ошибка при загрузке задания:", e.message);
      }
    },
    goToPrevTask() {
      if (this.prevTask) {
        this.$router.push({
          name: 'StudentContest',
          params: { taskId: this.prevTask.id },
          query: { versionId: this.$route.query.versionId },
        });
      }
    },
    goToNextTask() {
      if (this.nextTask) {
        this.$router.push({
          name: 'StudentContest',
          params: { taskId: this.nextTask.id },
          query: { versionId: this.$route.query.versionId },
        });
      }
    },
  },
};
</script>

<style scoped>
.task-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  border-radius: 8px;
  background: linear-gradient(to right, #dfe9f3, #ffffff);
}

button {
  padding: 12px 25px;
  font-size: 18px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #45a049;
}

button span {
  margin-left: 2px;
}

.navigation-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.navigation-buttons button {
  padding: 10px 20px;
  font-size: 16px;
  background-color: #1976d2;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.navigation-buttons button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

</style>
