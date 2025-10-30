<template>
  <div class="task-selector">
    <div
      class="floating-label multiselect-floating"
      :class="{ active: isActive }"
    >
      <div class="custom-multiselect">
        <multiselect
          ref="taskSelect"
          v-model="selectedTaskId"
          :options="tasksArray"
          :searchable="true"
          :allow-empty="false"
          :multiple="false"
          :select-label="''"
          :selected-label="''"
          :deselect-label="''"
          :append-to-body="true"
          open-direction="below"
          placeholder=""
          label="name"
          track-by="id"
          class="custom-multiselect-inner"
          @open="isOpen = true"
          @close="isOpen = false"
        />
      </div>
      <label>Задание</label>
    </div>

    <button
      class="btn-add"
      :disabled="!selectedTaskId"
      @click="addSelectedTask"
    >
      <i class="fas fa-plus"></i> Добавить
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
  components: { Multiselect },
  props: {
    allTasks: {
      type: Array,
      required: true,
    },
  },
  data() {
    return {
      selectedTaskId: null,
      isOpen: false,
    };
  },
  computed: {
    tasksArray() {
      return Array.isArray(this.allTasks) ? this.allTasks : [];
    },
    selectedTask() {
      return this.selectedTaskId
        ? this.tasksArray.find((t) => t.id === this.selectedTaskId.id)
        : null;
    },
    isActive() {
      // Активное состояние: есть значение или открыт список
      return this.selectedTaskId || this.isOpen;
    },
  },
  methods: {
    addSelectedTask() {
      if (this.selectedTask) {
        this.$emit("add-task", this.selectedTask);
        this.selectedTaskId = null;
      }
    },
  },
};
</script>

<style scoped>
.task-selector {
  margin-bottom: 16px;
  position: relative;
  z-index: 1;
}

.btn-add {
  background-color: #2f80ed;
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
  border-left: 4px solid #2f80ed;
  border-radius: 8px;
}

.floating-label {
  position: relative;
  margin-top: 1rem;
  width: 100%;
}

.floating-label label {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  background: #fff;
  padding: 0 4px;
  color: rgba(0, 0, 0, 0.55);
  pointer-events: none;
  font-size: 16px;
  transition: all 0.25s ease;
  z-index: 2;
}

.floating-label.active label {
  top: -8px;
  font-size: 12px;
  color: #60a5fa;
  transform: none;
}

.custom-multiselect :deep(.multiselect) {
  min-height: 44px;
  margin-top: 0;
  border-radius: 8px;
}

.custom-multiselect :deep(.multiselect__tags) {
  min-height: 44px;
  padding: 8px 36px 8px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: #fff;
  font-size: 15px;
  transition: all 0.25s ease;
}

.custom-multiselect :deep(.multiselect__tags:focus-within) {
  border-color: #2f80ed;
  box-shadow: 0 0 0 2px rgba(47, 128, 237, 0.12);
  outline: none;
}

.custom-multiselect :deep(.multiselect__input),
.custom-multiselect :deep(.multiselect__single) {
  font-size: 15px;
  padding: 3px;
  margin: 0;
  background: transparent;
  border: none;
}

.custom-multiselect :deep(.multiselect__placeholder) {
  color: rgba(0, 0, 0, 0.5);
  font-size: 15px;
}

.custom-multiselect :deep(.multiselect__select) {
  height: 42px;
  right: 6px;
  top: 1px;
  width: 30px;
  background: transparent;
  border-radius: 0 8px 8px 0;
}

.custom-multiselect :deep(.multiselect__select:before) {
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

.custom-multiselect :deep(.multiselect--active .multiselect__select:before) {
  transform: translate(-50%, -50%) rotate(180deg);
}

.custom-multiselect :deep(.multiselect__select:hover) {
  background: rgba(0, 0, 0, 0.05);
}

.custom-multiselect :deep(.multiselect__select:hover:before) {
  border-color: #333 transparent transparent transparent;
}

.custom-multiselect :deep(.multiselect--active .multiselect__select) {
  background: rgba(0, 0, 0, 0.05);
}

.custom-multiselect :deep(.multiselect__content-wrapper) {
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-top: 4px;
  z-index: 10;
}

.custom-multiselect :deep(.multiselect__option) {
  padding: 8px 12px;
  font-size: 15px;
  min-height: 36px;
}

.custom-multiselect :deep(.multiselect__option--selected) {
  background-color: #d0ebff;
  color: #333;
}

.custom-multiselect :deep(.multiselect__option--highlight) {
  background: #2f80ed;
  color: white;
}

.multiselect-floating :deep(.multiselect),
.multiselect-floating :deep(.multiselect__tags) {
  z-index: auto !important;    
}
</style>