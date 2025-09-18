<template>
  <Modal @close="cancel">
    <template #header>
      <h2>{{ title }}</h2>
    </template>

    <template #body>
      <p>{{ message }}</p>
    </template>

    <template #footer>
      <button class="btn cancel" @click="cancel">Отмена</button>
      <button class="btn confirm" @click="confirm">Подтвердить</button>
    </template>
  </Modal>
</template>

<script>
import Modal from './Modal.vue';

export default {
  name: 'ConfirmDialog',
  components: { Modal },
  props: {
    title: { type: String, default: 'Подтверждение' },
    message: { type: String, required: true }
  },
  methods: {
    confirm() { this.$emit('confirm'); },
    cancel() { this.$emit('cancel'); }
  }
};
</script>

<style scoped>
p {
  margin: 0;
  font-size: 16px;
  color: #4b5563;
  line-height: 1.5;
}

.btn {
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.2s ease;
  min-width: 100px;
}

.btn.cancel {
  background-color: #f3f4f6;
  border: 1px solid #d1d5db;
  color: #374151;
}

.btn.cancel:hover {
  background-color: #e5e7eb;
}

.btn.confirm {
  background-color: #ef4444;
  color: white;
  border: none;
  box-shadow: 0 4px 10px rgba(239, 68, 68, 0.3);
}

.btn.confirm:hover {
  background-color: #dc2626;
  box-shadow: 0 6px 14px rgba(220, 38, 38, 0.35);
}
</style>