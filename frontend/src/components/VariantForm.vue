<template>
  <div class="variant-form">
    <h3 class="variant-title">{{ variant.name }}</h3>

    <TaskSelector :allTasks="allTasks" @add-task="addTask" />

    <div class="task-card" v-for="(task, i) in variant.tasks" :key="task.id">
      <div class="task-info">
        <div class="task-name">{{ task.name }}</div>
        <div class="task-desc">{{ task.description }}</div>
      </div>
      <button class="btn-remove" @click="removeTask(i)">🗑 Удалить задание</button>
    </div>

    <button class="btn-remove-variant" @click="$emit('remove')">🗑 Удалить вариант</button>
  </div>
</template>

<script>
import TaskSelector from './TaskSelector.vue';

export default {
  components: { TaskSelector },
  props: {
    variant: {
      type: Object,
      required: true,
    },
    allTasks: {
      type: Array,
      required: true,
    },
  },
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
.variant-form {
  padding: 20px;
  border-radius: 12px;
  background-color: #f8f9fa;
  margin-bottom: 24px;
  border: 1px solid #dee2e6;
}

.variant-title {
  font-size: 22px;
  margin-bottom: 16px;
  font-weight: 600;
}

.task-card {
  background: #fff;
  border: 1px solid #dcdcdc;
  border-left: 5px solid #60a5fa;
  padding: 12px 16px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  border-radius: 8px;
  transition: box-shadow 0.2s;
}

.task-card:hover {
  box-shadow: 0 0 10px rgba(0, 123, 255, 0.1);
}

.task-info {
  max-width: 85%;
}

.task-name {
  font-weight: bold;
  font-size: 16px;
}

.task-desc {
  font-size: 14px;
  color: #555;
  margin-top: 4px;
}

.btn-remove,
.btn-remove-variant {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  margin-top: 8px;
}
</style>
