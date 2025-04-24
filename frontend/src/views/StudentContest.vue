<template>
  <div class="task-container" v-if="taskData">
    <TaskDescription :description="taskData.description" />
    <TestCases :testCases="taskData.testCases" />
    <CodeEditor ref="codeEditor" />
    <button @click="sendCode">Send</button>
    <OutputResults ref="outputResults" :taskId="taskData.id" />
  </div>
</template>

<script>
import TaskDescription from "../components/TaskDescription.vue";
import TestCases from "../components/TestCases.vue";
import CodeEditor from "../components/CodeEditor.vue";
import OutputResults from "../components/OutputResults.vue";

export default {
  components: {
    TaskDescription,
    TestCases,
    CodeEditor,
    OutputResults,
  },
  props: [],
  data() {
    return {
      taskData: null,
    };
  },
  methods: {
    async sendCode() {
      const codeEditor = this.$refs.codeEditor;
      if (!codeEditor || !codeEditor.editor) return;

      const code = codeEditor.editor.getValue();
      const response = await fetch("http://localhost:8080/test/submit", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ code, taskId: this.taskData.id }),
      });

      if (response.ok) this.$refs.outputResults.fetchResults();
    },
  },
  created() {
    const state = window.history.state;
    if (state?.task) {
      this.taskData = state.task;
    } else {
      // fallback — если обновили страницу, или перешли напрямую по URL
      const taskId = this.$route.params.taskId;
      fetch(`http://localhost:8080/api/tasks/${taskId}`)
        .then(res => res.json())
        .then(data => {
          this.taskData = data;
        });
    }
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
</style>
