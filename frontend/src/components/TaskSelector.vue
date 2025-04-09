<template>
  <div class="task-selector">
    <select v-model="selectedTaskId" @change="onSelect">
      <option disabled value="">-- Выберите задание --</option>
      <option v-for="task in allTasks" :key="task.id" :value="task.id">
        {{ task.name }}
      </option>
    </select>

    <div v-if="selectedTask" class="task-desc">
      <strong>Описание:</strong>
      <p>{{ selectedTask.description }}</p>
      <button class="btn-add" @click="$emit('add-task', selectedTask)">Добавить</button>
    </div>
  </div>
</template>

<script>
export default {
  props: ['allTasks'],
  data() {
    return {
      selectedTaskId: '',
      selectedTask: null,
    };
  },
  methods: {
    onSelect() {
      this.selectedTask = this.allTasks.find(t => t.id === this.selectedTaskId);
    },
  },
};
</script>

<style scoped>
.task-selector {
  margin-bottom: 16px;
}

select {
  width: 100%;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ccc;
  font-size: 16px;
}

.task-desc {
  margin-top: 12px;
  padding: 12px;
  background: #e7f3ff;
  border-left: 4px solid #007bff;
  border-radius: 8px;
}

.btn-add {
  margin-top: 8px;
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
}
</style>
