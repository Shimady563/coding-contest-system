<template>
  <div class="task-selector">
    <label for="task-select">Выберите задание:</label>
    <select id="task-select" v-model="selectedTaskId">
      <option disabled value="">-- Задание --</option>
      <option v-for="task in tasksArray" :key="task.id" :value="task.id">
        {{ task.name }}
      </option>
    </select>

    <button
      class="btn-add"
      :disabled="!selectedTaskId"
      @click="addSelectedTask"
    >
      ➕ Добавить
    </button>

    <transition name="fade">
      <div v-if="selectedTask" class="task-preview">
        <strong>{{ selectedTask.name }}</strong>
        <p>{{ selectedTask.description }}</p>
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  props: {
    allTasks: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      selectedTaskId: '',
    };
  },
  computed: {
    tasksArray() {
      return Array.isArray(this.allTasks) ? this.allTasks : [];
    },
    selectedTask() {
      return this.tasksArray.find((t) => t.id === this.selectedTaskId);
    },
  },
  methods: {
    addSelectedTask() {
      if (this.selectedTask) {
        this.$emit('add-task', this.selectedTask);
        this.selectedTaskId = '';
      }
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
  margin-bottom: 10px;
}

.btn-add {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  margin-bottom: 10px;
}

.btn-add:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.task-preview {
  padding: 12px;
  background: #e7f3ff;
  border-left: 4px solid #007bff;
  border-radius: 8px;
}
</style>
