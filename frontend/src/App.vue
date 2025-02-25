<template>
  <div>
    <TaskDescription />
    <TestCases />
    <button @click="runCode">
      Run <span style="font-size: 18px;">&#9654;</span>
    </button>
    <CodeEditor ref="codeEditor" />
    <OutputResults ref="outputResults" />
  </div>
</template>

<script>
import TaskDescription from './components/TaskDescription.vue';
import TestCases from './components/TestCases.vue';
import CodeEditor from './components/CodeEditor.vue';
import OutputResults from './components/OutputResults.vue';

export default {
  components: {
    TaskDescription,
    TestCases,
    CodeEditor,
    OutputResults,
  },
 methods: {
   runCode() {
     const codeEditor = this.$refs.codeEditor;

     // Проверка, инициализирован ли редактор
     if (codeEditor && codeEditor.editor) {
       const code = codeEditor.editor.getValue();
       console.log("Running code:", code);

       // Симуляция вывода
       const simulatedOutput = "Hello, World!";  // Здесь можно вставить реальный вывод
       this.$refs.outputResults.setResults(simulatedOutput);
     } else {
       console.error("Code editor is not initialized!");
     }
   },
 },
};
</script>

<style scoped>
button {
  margin: 10px;
  padding: 10px 20px;
  font-size: 16px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}

button span {
  margin-left: 2px;
}
</style>
