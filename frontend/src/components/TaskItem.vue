<template>
  <div class="task-item">
    <label>{{ task.title }}</label>
    <textarea v-model="task.description" placeholder="Описание задания..."></textarea>
    <button class="add-case-btn" @click="addCase">➕ Добавить тест-кейс</button>
    <TestCaseItem
      v-for="(testCase, caseIndex) in task.testCases"
      :key="caseIndex"
      :testCase="testCase"
      :caseIndex="caseIndex"
    />
  </div>
</template>

<script>
import TestCaseItem from './TestCaseItem.vue'
export default {
  props: ['task', 'taskIndex'],
  components: { TestCaseItem },
  methods: {
    addCase() {
      this.task.testCases.push({ input: '', expectedOutput: '' })
    }
  }
}
</script>

<style scoped>
.task-item {
  padding: 10px;
  background: #f0f8ff;
  border: 1px solid #ccc;
  border-radius: 5px;
  margin-bottom: 15px;
}
textarea {
  width: 100%;
  margin-bottom: 10px;
  border-radius: 6px;
  padding: 6px;
}
.add-case-btn {
  background: #17a2b8;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}
.add-case-btn:hover {
  background: #138496;
}
</style>
