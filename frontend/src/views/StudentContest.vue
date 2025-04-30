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
  async created() {
    const taskId = this.$route.params.taskId;
    const versionId = this.$route.query.versionId;
    const token = getAccessToken();

    if (!taskId || !versionId || !token) {
      console.error("Не удалось получить необходимые параметры.");
      return;
    }

    try {
      // Загружаем все задания этой версии контеста
      this.loadingTasks = true;
      const response = await fetch(`http://localhost:8080/api/v1/tasks/contest-version?contestVersionId=${versionId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const result = await response.json();
      this.tasksList = result ?? [];

      // Загружаем текущую задачу
      const task = this.tasksList.find(t => t.id === parseInt(taskId));
      if (task) {
        this.taskData = task;
      } else {
        const singleTaskResponse = await fetch(`http://localhost:8080/api/tasks/${taskId}`);
        this.taskData = await singleTaskResponse.json();
      }
    } catch (e) {
      console.error("Ошибка при загрузке задания:", e.message);
    } finally {
      this.loadingTasks = false;
    }
  },
  computed: {
    currentIndex() {
      return this.tasksList.findIndex(task => task.id === this.taskData?.id);
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
    async sendCode() {
      const codeEditor = this.$refs.codeEditor;
      if (!codeEditor || !codeEditor.editor) return;

      const code = codeEditor.editor.getValue();
      const token = getAccessToken();

      if (!token) {
        console.error("Токен не найден.");
        return;
      }

      // Получаем информацию о пользователе
      const userInfo = await import("@/js/auth").then(module => module.getUserInfo());
      if (!userInfo || !userInfo.id) {
        console.error("Не удалось получить информацию о пользователе.");
        return;
      }

      // Берем startTime и endTime из задачи
      const startTime = this.taskData?.startTime ?? new Date().toISOString();
      const endTime = this.taskData?.endTime ?? new Date().toISOString();

      const now = new Date().toISOString(); // время отправки

      const payload = {
        code: code,
        taskId: this.taskData.id,
        userId: userInfo.id, // <-- реальный ID пользователя
        contestVersionId: parseInt(this.$route.query.versionId),
        startTime: startTime,
        endTime: endTime,
        submittedAt: now,
      };

      try {
        const response = await fetch("http://localhost:8080/api/v1/submissions", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify(payload),
        });

        if (response.ok) {
          const submission = await response.json();
          if (submission && submission.id) {
            this.$refs.outputResults.fetchResults(submission.id);
          }
        } else {
          console.error("Ошибка при отправке кода:", await response.text());
        }
      } catch (error) {
        console.error("Ошибка при отправке кода:", error);
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
