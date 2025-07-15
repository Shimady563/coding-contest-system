<template>
  <div class="test-cases-card">
    <h2 class="test-cases-title">Пример тест-кейса</h2>
    <div class="test-case-example">
      <div class="io-section">
        <div class="input-section">
          <h3 class="io-title">Входные данные</h3>
          <pre class="io-content">{{ firstTestCase.input }}</pre>
        </div>
        <div class="output-section">
          <h3 class="io-title">Выходные данные</h3>
          <pre class="io-content">{{ firstTestCase.output }}</pre>
        </div>
      </div>
      <p class="test-cases-hint">*Ваше решение будет проверено на нескольких тест-кейсах</p>
    </div>
  </div>
</template>

<script>
export default {
  name: "TestCases",
  props: {
    testCases: {
      type: Array,
      default: () => [],
      validator: value => value.length > 0  
    }
  },
  computed: {
    firstTestCase() {
      return this.testCases[0] || { input: '', output: '' };
    }
  }
};
</script>

<style scoped>
.test-cases-card {
  background-color: #ffffff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 24px;
  border: 1px solid #eaeaea;
}

.test-cases-title {
  font-size: 1.4rem;
  color: #2c3e50;
  margin-bottom: 20px;
  font-weight: 600;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 10px;
}

.test-case-example {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e0e0e0;
}

.io-section {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
}

.input-section, .output-section {
  flex: 1;
  min-width: 0;
}

.io-title {
  font-size: 1rem;
  color: #555;
  margin-bottom: 8px;
  font-weight: 500;
}

.io-content {
  background-color: #2d2d2d;
  color: #f8f8f2;
  padding: 12px;
  border-radius: 6px;
  font-family: 'Consolas', 'Monaco', monospace;
  overflow-x: auto;
  margin: 0;
  line-height: 1.5;
}

.test-cases-hint {
  font-size: 0.9rem;
  color: #666;
  font-style: italic;
  margin-top: 16px;
  text-align: right;
}

@media (max-width: 768px) {
  .io-section {
    flex-direction: column;
    gap: 15px;
  }
}
</style>
