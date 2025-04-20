<template>
  <div class="test-cases">
    <h2>Test Cases</h2>
    <ul>
      <li v-for="(testCase, index) in testCases" :key="index">
        <strong>Test {{ index + 1 }}:</strong>
        <pre>{{ testCase.input }}</pre>
        <p><strong>Expected Output:</strong> {{ testCase.expectedOutput }}</p>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: "TestCases",
  data() {
    return {
      testCases: [
        {
          input: "5\n1 2 3 4 5\n",  // Example input
          expectedOutput: "15",    // Expected output
        },
        {
          input: "3\n2 2 2\n",
          expectedOutput: "6",
        },
      ],
    };
  },
};
</script>

<style scoped>
.test-cases {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.test-cases h2 {
  font-size: 22px;
  margin-bottom: 12px;
}

.test-cases pre {
  background-color: #333;
  color: #fff;
  padding: 12px;
  border-radius: 8px;
  white-space: pre-wrap;
}

.test-cases p {
  margin-top: 12px;
  font-size: 16px;
  color: #333;
}
</style>
