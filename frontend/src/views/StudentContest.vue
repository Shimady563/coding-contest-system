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
import { getUserInfo } from "../js/auth";

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
      contest: null
    };
  },
  computed: {
    currentIndex() {
      return this.tasksList.findIndex(task => task.id === Number(this.taskData?.id));
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
  watch: {
    '$route.params.taskId': {
      immediate: true,
      handler() {
        this.loadTask();
      }
    }
  },
  methods: {
    async loadTask() {
      const taskId = this.$route.params.taskId;
      const versionId = this.$route.params.versionId;
      const contestId = this.$route.params.contestId;

      try {
        const contestResponse = await fetch(`http://localhost:8080/api/v1/contests/${contestId}`, {
          credentials: "include"
        });
        if (!contestResponse.ok) throw new Error("Не удалось загрузить данные контеста");
        this.contest = await contestResponse.json();
      } catch {
        return;
      }

      const start = new Date(this?.contest.startTime);
      const end =  new Date(this?.contest.endTime);
      const now = new Date();

      if (now < start || now > end) {
        this.$router.replace('/access-denied-contest');
        return;
      }

      if (!taskId || !versionId) {
        console.error("Не удалось получить необходимые параметры.");
        return;
      }

      try {
        this.loadingTasks = true;
        const response = await fetch(`http://localhost:8080/api/v1/tasks/contest-version?contestVersionId=${versionId}`, {
          credentials: "include",
        });
        const result = await response.json();
        this.tasksList = result ?? [];

        const task = this.tasksList.find(t => t.id === parseInt(taskId));
        if (task) {
          this.taskData = task;
        } else {
          const singleTaskResponse = await fetch(`http://localhost:8080/api/tasks/${taskId}`, {
            credentials: "include",
          });
          this.taskData = await singleTaskResponse.json();
        }
      } catch (e) {
        console.error("Ошибка при загрузке задания:", e.message);
      } finally {
        this.loadingTasks = false;
      }
    },
    async sendCode() {
      const codeEditor = this.$refs.codeEditor;
      if (!codeEditor || !codeEditor.editor) return;

      const code = codeEditor.editor.getValue().trim();
      if (!code) {
        this.$root.notify("Код не может быть пустым", "warning");
        return;
      }

      try {
        const userInfo = await getUserInfo();

        const payload = {
          code,
          taskId: this.taskData.id,
          userId: userInfo.id,
          contestVersionId: parseInt(this.$route.params.versionId),
          submittedAt: new Date().toISOString(),
        };

        const response = await fetch("http://localhost:8080/api/v1/submissions", {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(payload),
        });

        if (response.ok) {
          this.$root.notify("Код успешно отправлен", "success");
            
          this.$refs.outputResults.fetchResults();
          
          let count = 0;
          const interval = setInterval(() => {
            this.$refs.outputResults.fetchResults();
            count++;
            if (count >= 5) clearInterval(interval); 
          }, 2000);
        } else {
          const errorText = await response.text();
          if (response.status === 400) {
            this.$root.notify("Контрольная завершена. Отправка запрещена.", "error");
          } else {
            this.$root.notify("Не удалось отправить код: " + errorText, "error");
          }
          console.error("Ошибка при отправке кода:", errorText);
        }
      } catch (e) {
        console.error("Ошибка:", e);
        this.$root.notify("Произошла ошибка при отправке кода", "error");
      }
    },
    goToPrevTask() {
      if (this.prevTask) {
        this.$router.push({
          name: 'StudentContest',
          params: {
            contestId: this.$route.params.contestId,
            versionId: this.$route.params.versionId,
            taskId: this.prevTask.id,
          },
        });
      }
    },
    goToNextTask() {
      if (this.nextTask) {
        this.$router.push({
          name: 'StudentContest',
          params: {
            contestId: this.$route.params.contestId,
            versionId: this.$route.params.versionId,
            taskId: this.nextTask.id,
          },
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  justify-content: space-between;
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
