<template>
    <div v-if="password" class="password-hints">
        <div v-for="hint in hints" :key="hint.text" :class="{ valid: hint.valid }">
        <span class="hint-icon">✓</span>
        <span class="hint-text">{{ hint.text }}</span>
        </div>
    </div>
</template>
  
  
  <script>
  export default {
    props: { password: String },
    computed: {
      hints() {
        return [
          { text: 'Минимум 8 символов', valid: this.password?.length >= 8 },
          { text: 'Заглавная буква', valid: /[A-Z]/.test(this.password) },
          { text: 'Строчная буква', valid: /[a-z]/.test(this.password) },
          { text: 'Цифра', valid: /\d/.test(this.password) },
          { text: 'Спецсимвол @#$%^&+=!?*', valid: /[@#$%^&+=!?*]/.test(this.password) },
        ];
      },
    },
  };
  </script>
  
<style scoped>
.password-hints { 
    margin-top: -25px; 
    margin-bottom: 1.8rem;
    font-size: 13.5px; 
    line-height: 1.4; 
}

.password-hints div { 
    display: flex; 
    align-items: center; 
    margin: 4px 0; 
    color: #888; 
}

.hint-icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 16px;
    height: 16px;
    margin-right: 8px;
    font-size: 12px;
    border: 1.5px solid #ddd;
    border-radius: 50%;
    color: transparent;
    transition: all 0.2s ease;
}

.valid { color: #27ae60; }

.valid .hint-icon {
    background-color: #27ae60;
    border-color: #27ae60;
    color: white;
}

.hint-text { flex: 1; }
</style>