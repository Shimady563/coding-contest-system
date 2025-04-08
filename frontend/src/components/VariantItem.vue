<template>
  <div class="variant-item">
    <h3>{{ variant.title }}</h3>
    <button class="add-task-btn" @click="addTask">➕ Добавить задание</button>
    <TaskItem
      v-for="(task, taskIndex) in variant.tasks"
      :key="taskIndex"
      :task="task"
      :taskIndex="taskIndex"
    />
  </div>
</template>

<script>
import TaskItem from './TaskItem.vue'
export default {
  props: ['variant', 'index'],
  components: { TaskItem },
  methods: {
    addTask() {
      this.variant.tasks.push({ title: `Задание ${this.variant.tasks.length + 1}`, description: '', testCases: [] })
    }
  }
}
</script>

<style scoped>
.variant-item {
  padding: 15px;
  margin-bottom: 20px;
  background: #ffffff;
  border: 1px solid #ddd;
  border-radius: 6px;
}
.add-task-btn {
  margin-bottom: 10px;
  padding: 5px 10px;
  background: #ffc107;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.add-task-btn:hover {
  background: #e0a800;
}
</style>
