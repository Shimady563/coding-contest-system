<template>
    <div>
      <textarea ref="codeViewer"></textarea>
    </div>
  </template>
  
  <script>
  import CodeMirror from "codemirror";
  import "codemirror/mode/clike/clike.js"; // Для C++, C
  import "codemirror/lib/codemirror.css";
  import "codemirror/theme/monokai.css"; // Или любой другой
  
  export default {
    name: "ReadOnlyCodeMirror",
    props: {
      code: {
        type: String,
        required: true,
      },
      language: {
        type: String,
        default: "text/x-c++src",
      },
    },
    mounted() {
      this.editor = CodeMirror.fromTextArea(this.$refs.codeViewer, {
        value: this.code,
        mode: this.language,
        theme: "monokai",
        lineNumbers: true,
        readOnly: true,
      });
  
      this.editor.setValue(this.code);
    },
    watch: {
      code(newCode) {
        if (this.editor) {
          this.editor.setValue(newCode);
        }
      }
    }
  };
  </script>
  
  <style scoped>
  .CodeMirror {
    height: auto;
    max-height: 500px;
    width: 100%;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-family: monospace;
  }
  </style>
  