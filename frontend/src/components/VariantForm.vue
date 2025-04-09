<template>
  <div>
    <h3 class="variant-title">{{ variant.name }}</h3>

    <TaskSelector :allTasks="allTasks" @add-task="addTask" />

    <ul class="task-list">
      <li v-for="(task, i) in variant.tasks" :key="i" class="task-item">
        {{ task.name }}
        <button class="btn-remove" @click="removeTask(i)">Удалить</button>
      </li>
    </ul>

    <button class="btn-remove-variant" @click="$emit('remove')">Удалить вариант</button>
  </div>
</template>

<script>
import TaskSelector from './TaskSelector.vue';

export default {
  components: { TaskSelector },
  props: ['variant', 'allTasks'],
  methods: {
    addTask(task) {
      if (!this.variant.tasks.some((t) => t.id === task.id)) {
        this.variant.tasks.push(task);
        this.$emit('update', this.variant);
      }
    },
    removeTask(index) {
      this.variant.tasks.splice(index, 1);
      this.$emit('update', this.variant);
    },
  },
};
</script>

<style scoped>
.variant-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 12px;
}

.task-list {
  list-style: none;
  padding: 0;
  margin: 12px 0;
}

.task-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 12px;
  background-color: #f0f0f0;
  margin-bottom: 8px;
  border-radius: 6px;
  align-items: center;
}

.btn-remove,
.btn-remove-variant {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 6px 10px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  margin-left: 8px;
}
</style>
