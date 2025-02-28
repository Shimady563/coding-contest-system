<template>
  <div>
    <textarea ref="codeEditor"></textarea>
  </div>
</template>

<script>
import CodeMirror from "codemirror";
import "codemirror/mode/clike/clike.js";
import "codemirror/lib/codemirror.css";
import "codemirror/theme/dracula.css";

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

    console.log("CodeMirror initialized:", this.editor);

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
