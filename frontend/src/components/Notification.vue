<template>
    <transition name="fade">
      <div v-if="isVisible" :class="['notification', type]">
        <span class="message">{{ message }}</span>
        <button class="close-btn" @click="hide">×</button>
      </div>
    </transition>
  </template>
  
  <script>
  export default {
    data() {
      return {
        isVisible: false,
        message: '',
        type: 'info',
        timeout: null
      }
    },
    methods: {
      show(message, type = 'info', duration = 5000) {
        this.message = message
        this.type = type
        this.isVisible = true
        
        clearTimeout(this.timeout)
        this.timeout = setTimeout(() => {
          this.hide()
        }, duration)
      },
      hide() {
        this.isVisible = false
      }
    }
  }
  </script>
  
  <style>
  .notification {
    position: absolute;
    top: 20px;
    right: 20px;
    padding: 15px 20px;
    border-radius: 5px;
    color: white;
    display: flex;
    align-items: center;
    justify-content: space-between;
    min-width: 250px;
    max-width: 350px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    z-index: 1000;
  }
  
  .notification.success {
    background-color: #34d399;
  }
  
  .notification.error {
    background-color: #dc3545;
  }
  
  .notification.info {
    background-color: #60a5fa;
  }
  
  .notification.warning {
    background-color: #f59e0b;
  }
  
  .close-btn {
    background: none;
    border: none;
    color: white;
    font-size: 20px;
    cursor: pointer;
    margin-left: 15px;
    padding: 0;
  }
  
  .fade-enter-active, .fade-leave-active {
    transition: opacity 0.3s, transform 0.3s;
  }
  .fade-enter-from, .fade-leave-to {
    opacity: 0;
    transform: translateX(20px);
  }
  </style>