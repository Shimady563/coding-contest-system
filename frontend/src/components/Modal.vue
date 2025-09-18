<template>
  <div class="modal-overlay" @click.self="close" role="dialog" aria-modal="true">
    <div class="modal-container" ref="container">
      <div class="modal-header">
        <slot name="header"></slot>
        <button class="close-btn" type="button" aria-label="Закрыть" @click="close">&times;</button>
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
  mounted() {
    document.addEventListener('keydown', this.onKeydown);
  },
  beforeUnmount() {
    document.removeEventListener('keydown', this.onKeydown);
  },
  methods: {
    close() {
      this.$emit('close');
    },
    onKeydown(e) {
      if (e.key === 'Escape') {
        this.close();
      }
    }
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-container {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  width: 92%;
  max-width: 640px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: modalIn 0.18s ease-out;
}

.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid #eef1f4;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(180deg, #ffffff, #fafbfc);
}

.modal-header h2 {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #94a3b8;
  padding: 0;
  line-height: 1;
}

.close-btn:hover {
  color: #475569;
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
  flex-grow: 1;
}

.modal-footer {
  padding: 16px 20px;
  border-top: 1px solid #eef1f4;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@keyframes modalIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@media (max-width: 600px) {
  .modal-container {
    width: 95%;
  }

  .modal-header, .modal-body, .modal-footer {
    padding: 12px 15px;
  }
}
</style>
