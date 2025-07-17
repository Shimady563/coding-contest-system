<template>
  <div class="task-selector">
    <label>Выберите задание:</label>
    <multiselect
      v-model="selectedTaskId"
      :options="tasksArray"
      :searchable="true"
      :allow-empty="false"
      :multiple="false"
      :select-label="''"
      :selected-label="''"
      :deselect-label="''"
      placeholder="-- Задание --"
      label="name"
      track-by="id"
      class="custom-multiselect"
    >
    </multiselect>

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
import Multiselect from "vue-multiselect";
import "vue-multiselect/dist/vue-multiselect.min.css";

export default {
  components: {
    Multiselect,
  },
  props: {
    allTasks: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      selectedTaskId: null,
    };
  },
  computed: {
    tasksArray() {
      return Array.isArray(this.allTasks) ? this.allTasks : [];
    },
    selectedTask() {
      return this.selectedTaskId ? this.tasksArray.find((t) => t.id === this.selectedTaskId.id) : null;
    },
  },
  methods: {
    addSelectedTask() {
      if (this.selectedTask) {
        this.$emit('add-task', this.selectedTask);
        this.selectedTaskId = null;
      }
    },
  },
};
</script>

<style scoped>
.task-selector {
  margin-bottom: 16px;
}

.btn-add {
  background-color: #60a5fa;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  margin-top: 10px;
  margin-bottom: 10px;
}

.btn-add:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.task-preview {
  padding: 12px;
  background: #e7f3ff;
  border-left: 4px solid #60a5fa;
  border-radius: 8px;
}

.custom-multiselect >>> .multiselect {
  min-height: 38px;
  margin-top: 6px;
}

.custom-multiselect >>> .multiselect__tags {
  min-height: 38px;
  padding: 8px 30px 8px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: white;
  font-size: 16px;
}

.custom-multiselect >>> .multiselect__tags:focus-within {
  border-color: #60a5fa;
  box-shadow: 0 0 0 2px rgba(96, 165, 250, 0.1);
  outline: none;
}

.custom-multiselect >>> .multiselect__input,
.custom-multiselect >>> .multiselect__single {
  font-size: 16px;
  padding: 0;
  margin: 0;
  background: transparent;
  border: none;
}

.custom-multiselect >>> .multiselect__input:focus {
  outline: none;
  box-shadow: none;
}

.custom-multiselect >>> .multiselect__placeholder {
  color: #999;
  margin: 0;
  padding: 0;
  font-size: 16px;
}

.custom-multiselect >>> .multiselect__select {
  height: 36px;
  right: 1px;
  top: 1px;
  width: 30px;
  padding: 0;
  background: transparent;
  border-radius: 0 8px 8px 0;
}

.custom-multiselect >>> .multiselect__select:before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 6px 5px 0 5px;
  border-color: #666 transparent transparent transparent;
  transition: transform 0.2s ease;
}

.custom-multiselect >>> .multiselect--active .multiselect__select:before {
  transform: translate(-50%, -50%) rotate(180deg);
}

.custom-multiselect >>> .multiselect__select:hover {
  background: rgba(0, 0, 0, 0.05);
}

.custom-multiselect >>> .multiselect__select:hover:before {
  border-color: #333 transparent transparent transparent;
}

.custom-multiselect >>> .multiselect--active .multiselect__select {
  background: rgba(0, 0, 0, 0.05);
}

.custom-multiselect >>> .multiselect__content-wrapper {
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-top: 4px;
  z-index: 10;
}

.custom-multiselect >>> .multiselect__option {
  padding: 8px 12px;
  font-size: 16px;
  min-height: 36px;
}

.custom-multiselect >>> .multiselect__option--selected {
  background-color: #d0ebff;
  color: #333;
  font-weight: normal;
}

.custom-multiselect >>> .multiselect__option--highlight {
  background: #60a5fa;
  color: white;
}

.custom-multiselect >>> .multiselect__option--selected.multiselect__option--highlight {
  background: #3b82f6;
  color: white;
}
</style>