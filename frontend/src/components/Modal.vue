<template>
  <div class="modal-overlay" @click.self="close">
    <div class="modal-container" ref="container">
      <div class="modal-header">
        <slot name="header"></slot>
        <button class="close-btn" @click="close">&times;</button>
      </div>
      <div class="modal-body">
        <slot name="body"></slot>
      </div>
      <div class="modal-footer">
        <slot name="footer"></slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Modal',
  mounted() { document.addEventListener('keydown', this.onKeydown); },
  beforeUnmount() { document.removeEventListener('keydown', this.onKeydown); },
  methods: {
    close() { this.$emit('close'); },
    onKeydown(e) { if (e.key === 'Escape') this.close(); }
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-container {
  background-color: #ffffff;
  border-radius: 14px;
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.25);
  width: 90%;
  max-width: 600px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: fadeInUp 0.2s ease-out;
}

.modal-header {
  padding: 18px 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f9fafb;
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  color: #111827;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #9ca3af;
  cursor: pointer;
}

.close-btn:hover { color: #6b7280; }

.modal-body {
  padding: 20px 24px;
  overflow-y: auto;
  flex-grow: 1;
  color: #4b5563;
}

.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 600px) {
  .modal-container {
    width: 95%;
  }
  .modal-header, .modal-body, .modal-footer { padding: 12px 16px; }
}
</style>