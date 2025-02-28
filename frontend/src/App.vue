<template>
  <div>
    <TaskDescription />
    <TestCases />
    <CodeEditor ref="codeEditor" />
     <button @click="sendCode">
        Send
     </button>
    <OutputResults ref="outputResults" />
  </div>
</template>

<script>
import TaskDescription from "./components/TaskDescription.vue";
import TestCases from "./components/TestCases.vue";
import CodeEditor from "./components/CodeEditor.vue";
import OutputResults from "./components/OutputResults.vue";

export default {
  components: {
    TaskDescription,
    TestCases,
    CodeEditor,
    OutputResults,
  },
  methods: {
     async sendCode() {
       const codeEditor = this.$refs.codeEditor;

       if (!codeEditor || !codeEditor.editor) {
         console.error("Code editor is not initialized!");
         return;
       }

       const code = codeEditor.editor.getValue();
       console.log("Sending code:", code);

       try {
         const response = await fetch("http://localhost:8080/test/submit", {
           method: "POST",
           headers: {
             "Content-Type": "application/json",
           },
           body: JSON.stringify({ code }),
           mode: "cors"
         });

         if (!response.ok) {
           throw new Error("Failed to submit code");
         }

         console.log("Code submitted successfully");

         this.$refs.outputResults.fetchResults();
       } catch (error) {
         console.error("Error submitting code:", error);
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
