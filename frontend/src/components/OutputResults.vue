<template>
  <div class="output-results">
    <h2>Output Results</h2>
    <table v-if="results.length">
      <thead>
        <tr>
          <th>ID</th>
          <th>Status</th>
          <th>Tests Passed</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="result in results" :key="result.id">
          <td>{{ result.id }}</td>
          <td>{{ result.status }}</td>
          <td>{{ result.testsPassed }}</td>
        </tr>
      </tbody>
    </table>
    <p v-else>No results yet. Please send the code!</p>
  </div>
</template>

<script>
import { onMounted } from "vue";

export default {
  name: "OutputResults",
  data() {
    return {
      results: [],
    };
  },
  methods: {
    async fetchResults() {
      console.log("Fetching results...");

      try {
        const response = await fetch("http://localhost:8080/test/solutions?taskId=1");

        if (!response.ok) {
          throw new Error("Failed to fetch results");
        }

        this.results = await response.json();
        console.log("Results received:", this.results);
      } catch (error) {
        console.error("Error fetching results:", error);
      }
    },
  },
  onMounted() {
    this.fetchResults();
  }
};
</script>

<style scoped>
.output-results {
  background-color: #f4f4f4;
  padding: 20px;
  border-radius: 5px;
}

h2 {
  margin-bottom: 10px;
  font-size: 20px;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #333;
  color: white;
}

.accepted {
  color: green;
  font-weight: bold;
}

.failed {
  color: red;
  font-weight: bold;
}

.pending {
  color: orange;
  font-weight: bold;
}
</style>
