<template>
  <div class="task-selector">
    <div class="floating-label multiselect-floating" :class="{ active: isActive }">
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

    <button class="btn-add" :disabled="!selectedTaskId" @click="addSelectedTask">
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
import Multiselect from 'vue-multiselect'
import 'vue-multiselect/dist/vue-multiselect.min.css'

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
    }
  },
  computed: {
    tasksArray() {
      return Array.isArray(this.allTasks) ? this.allTasks : []
    },
    selectedTask() {
      return this.selectedTaskId
        ? this.tasksArray.find((t) => t.id === this.selectedTaskId.id)
        : null
    },
    isActive() {
      return this.selectedTaskId || this.isOpen
    },
  },
  methods: {
    addSelectedTask() {
      if (this.selectedTask) {
        this.$emit('add-task', this.selectedTask)
        this.selectedTaskId = null
      }
    },
  },
}
</script>

<style scoped>
.task-selector {
  margin-bottom: 16px;
  margin-top: 20px;
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

.multiselect-floating :deep(.multiselect__content-wrapper) {
  z-index: 1000 !important;
}

.multiselect-floating :deep(.multiselect),
.multiselect-floating :deep(.multiselect__tags) {
  z-index: auto !important;
}
</style>
