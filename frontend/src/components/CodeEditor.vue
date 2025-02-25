<template>
  <div>
    <textarea ref="codeEditor"></textarea>
  </div>
</template>

<script>
import CodeMirror from "codemirror";
import "codemirror/mode/clike/clike.js";  // C++ mode
import "codemirror/lib/codemirror.css";  // Default styles
import "codemirror/theme/dracula.css";  // Theme

export default {
  name: "CodeEditor",
  mounted() {
    this.editor = CodeMirror.fromTextArea(this.$refs.codeEditor, {
      mode: "text/x-c++src",
      theme: "dracula",
      lineNumbers: true,
      tabSize: 4,
      indentWithTabs: true,
    });

    // Убедитесь, что редактор проинициализирован
    console.log("CodeMirror initialized:", this.editor);

    // Set initial content
    this.editor.setValue(`#include <iostream>\nusing namespace std;\nint main() {\n    cout << "Hello, World!";\n    return 0;\n}`);
  },
};
</script>

<style>
.code-editor-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px;
}

.CodeMirror {
  height: 100%;
  width: 100%;
  border: 1px solid #ccc;
  border-radius: 5px;
  font-family: 'Courier New', Courier, monospace;
}

@media (max-width: 768px) {
  .CodeMirror {
    height: 300px;
  }
}
</style>
