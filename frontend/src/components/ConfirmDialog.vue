<template>
  <Modal>
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
  components: {
    Modal
  },
  props: {
    title: {
      type: String,
      default: 'Подтверждение'
    },
    message: {
      type: String,
      required: true
    }
  },
  methods: {
    confirm() {
      this.$emit('confirm');
    },
    cancel() {
      this.$emit('cancel');
    }
  }
};
</script>

<style scoped>
p {
  margin: 0;
  font-size: 16px;
  color: #555;
}

.btn {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn.cancel {
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  color: #333;
}

.btn.cancel:hover {
  background-color: #e0e0e0;
}

.btn.confirm {
  background-color: #e74c3c;
  color: white;
  border: none;
}

.btn.confirm:hover {
  background-color: #c0392b;
}
</style>
