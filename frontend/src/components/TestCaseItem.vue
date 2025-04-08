<template>
  <div class="test-case">
    <input v-model="testCase.input" placeholder="Входные данные" />
    <input v-model="testCase.expectedOutput" placeholder="Ожидаемый вывод" />
  </div>
</template>

<script>
export default {
  props: ['testCase', 'caseIndex']
}
</script>

<style scoped>
.test-case {
  display: flex;
  gap: 10px;
  margin: 6px 0;
}
.test-case input {
  flex: 1;
  padding: 6px;
  border-radius: 4px;
  border: 1px solid #bbb;
}
</style>
