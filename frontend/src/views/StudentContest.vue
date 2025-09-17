<template>
  <div v-if="taskData" class="task-container">
    <div v-if="timeLeft" class="timer">
      Времени осталось: {{ formattedTime }}
    </div>
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
import { MANAGER_URL } from "../js/api";

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
      contest: null,
      timeLeft: null,
      timerInterval: null
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
    formattedTime() {
      if (this.timeLeft <= 0) return "00:00:00";
      const totalSeconds = Math.floor(this.timeLeft / 1000);
      const hours = String(Math.floor(totalSeconds / 3600)).padStart(2, '0');
      const minutes = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, '0');
      const seconds = String(totalSeconds % 60).padStart(2, '0');
      return `${hours}:${minutes}:${seconds}`;
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
        const contestResponse = await fetch(`${MANAGER_URL}/contests/${contestId}`, {
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

      this.timeLeft = end - now;
      this.startTimer();

      if (!taskId || !versionId) {
        return;
      }

      try {
        this.loadingTasks = true;
        const response = await fetch(`${MANAGER_URL}/tasks/contest-version?contestVersionId=${versionId}`, {
          credentials: "include",
        });
        const result = await response.json();
        this.tasksList = result ?? [];

        const task = this.tasksList.find(t => t.id === parseInt(taskId));
        if (task) {
          this.taskData = task;
        } else {
          const singleTaskResponse = await fetch(`${MANAGER_URL}/tasks/${taskId}`, {
            credentials: "include",
          });
          this.taskData = await singleTaskResponse.json();
        }
      } catch {
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
        const outputComponent = this.$refs.outputResults;

        await outputComponent.fetchResults();
        const initialLength = outputComponent.results.length;

        const moscowTime = new Date().toLocaleString('sv-SE', {
          timeZone: 'Europe/Moscow',
          hour12: false,
        }).replace(' ', 'T');

        const payload = {
          code,
          taskId: this.taskData.id,
          userId: userInfo.id,
          contestVersionId: parseInt(this.$route.params.versionId),
          submittedAt: moscowTime,
        };

        const response = await fetch(`${MANAGER_URL}/submissions`, {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(payload),
        });

        if (response.ok) {
          this.$root.notify("Код успешно отправлен", "success");

          const waitForNewResult = async () => {
            const MAX_RETRIES = 30;
            const DELAY = 2000;
            let retries = 0;

            while (retries < MAX_RETRIES) {
              await outputComponent.fetchResults();
              const currentLength = outputComponent.results.length;

              if (currentLength > initialLength) {
                this.$root.notify("Результат получен", "success");
                return;
              }

              retries++;
              await new Promise(resolve => setTimeout(resolve, DELAY));
            }

            this.$root.notify("Истекло время ожидания результата", "warning");
          };

          waitForNewResult();

        } else {
          const errorText = await response.text();
          if (response.status === 400) {
            this.$root.notify("Контрольная завершена. Отправка запрещена.", "error");
          } else {
            this.$root.notify("Не удалось отправить код.", "error");
          }
        }
      } catch (e) {
        this.$root.notify("Произошла ошибка при отправке кода.", "error");
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
    startTimer() {
      if (this.timerInterval) clearInterval(this.timerInterval);
      this.timerInterval = setInterval(() => {
        this.timeLeft -= 1000;
        if (this.timeLeft <= 0) {
          clearInterval(this.timerInterval);
          this.timeLeft = 0;
          this.$root.notify("Время вышло. Страница будет перезагружена.", "error");
          setTimeout(() => {
            location.reload();
          }, 2000);
        }
      }, 1000);
    },
    beforeDestroy() {
      if (this.timerInterval) {
        clearInterval(this.timerInterval);
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
  animation: fadeIn 0.4s ease-in-out;
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

.timer {
  font-size: 20px;
  font-weight: bold;
  color: #e53935;
  background-color: #fce4ec;
  padding: 10px 20px;
  border-radius: 8px;
  text-align: center;
  width: fit-content;
  align-self: center;
}
</style>
