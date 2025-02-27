<template>
  <div class="output-results">
    <h2>Output Results</h2>
    <div v-if="results">
      <pre>{{ results }}</pre>
    </div>
    <div v-else>
      <p>No results yet. Please run the code!</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "OutputResults",
  data() {
    return {
      results: "",  // Результаты выполнения программы
    };
  },
  methods: {
    setResults(output) {
      this.results = output;
    },
  },
};
</script>

<style scoped>
.output-results {
  background-color: #f4f4f4;
  padding: 20px;
  border-radius: 5px;
}

.output-results h2 {
  margin-bottom: 10px;
  font-size: 20px;
}

.output-results pre {
  background-color: #333;
  color: #fff;
  padding: 10px;
  border-radius: 5px;
  white-space: pre-wrap;
}

.output-results p {
  font-size: 16px;
  color: #333;
}
</style>
