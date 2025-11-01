<template>
  <div class="auth-container">
    <form @submit.prevent="login" class="auth-form">
      <h2>Вход в систему</h2>
      
      <div class="floating-label">
        <input 
          name="email" 
          type="email" 
          v-model="email" 
          id="email"
          required 
          autocomplete="email" 
          placeholder=""
        />
        <label for="email">Email</label>
      </div>
      
      <div class="floating-label">
        <input 
          name="current-password" 
          type="password" 
          v-model="password" 
          id="password"
          required 
          autocomplete="current-password" 
          placeholder=""
        />
        <label for="password">Пароль</label>
      </div>
      
      <button 
        type="submit" 
        class="btn primary" 
        :disabled="isSubmitDisabled"
      >
        Войти
      </button>
      
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      
      <p class="footer-link">
        Нет аккаунта? <router-link to="/register">Зарегистрироваться</router-link>
      </p>
    </form>
  </div>
</template>

<script>
import { login } from "@/js/auth";

export default {
  data() {
    return {
      email: "",
      password: "",
      errorMessage: "",
    };
  },
  computed: {
    isSubmitDisabled() {
      return !this.email || !this.password;
    }
  },
  methods: {
    async login() {
      this.errorMessage = "";
      try {
        this.$root.notify("Попытка входа...", "info");
        await login({ email: this.email, password: this.password });

        this.$root.notify("Вход выполнен успешно!", "success");
        this.$router.push("/").then(() => window.location.reload());
      } catch (err) {
        this.errorMessage = err.message || "Ошибка при входе";
        this.$root.notify(this.errorMessage, "error");
      }
    },
  },
};
</script>

<style scoped>
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.auth-form {
  background-color: #fff;
  padding: 2rem 2.5rem;
  border-radius: 10px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  width: 100%;
  max-width: 420px;
  animation: fadeIn 0.4s ease-in-out;
}

h2 {
  text-align: center;
  margin-bottom: 28px;
  color: #2f3640;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.floating-label {
  position: relative;
  margin-bottom: 1.8rem;
}

.floating-label input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #ccc;
  border-radius: 8px;
  outline: none;
  transition: all 0.25s ease;
  background: #fff;
  font-size: 15px;
  color: #333;
  box-sizing: border-box;
}

.floating-label label {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(0, 0, 0, 0.5);
  pointer-events: none;
  padding: 0 4px;
  font-size: 15px;
  transition: all 0.25s ease;
  background: transparent;
}

.floating-label input:focus + label,
.floating-label input:not(:placeholder-shown) + label {
  top: -8px;
  left: 10px;
  font-size: 12px;
  color: #2f80ed;
  background: #fff;
  transform: none;
}

.floating-label input:focus {
  border-color: #2f80ed;
  box-shadow: 0 0 0 2px rgba(47, 128, 237, 0.12);
}

.floating-label input:invalid:not(:focus):not(:placeholder-shown) {
  border-color: #f44336;
}

.floating-label input:invalid:not(:focus):not(:placeholder-shown) + label {
  color: #f44336;
}

button {
  width: 100%;
  padding: 12px;
  background-color: #2f80ed;
  color: white;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.25s ease, transform 0.1s ease;
  font-size: 15px;
  margin-top: 0.5rem;
}

button:hover:not(:disabled) {
  background-color: #1366d6;
  transform: translateY(-1px);
}

button:disabled {
  background-color: #cfcfcf;
  cursor: not-allowed;
  transform: none;
}

.error-message {
  color: #e74c3c;
  font-size: 14px;
  margin-top: 16px;
  text-align: center;
  line-height: 1.4;
}

.footer-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.footer-link a {
  color: #2f80ed;
  text-decoration: none;
  font-weight: 500;
}

.footer-link a:hover {
  text-decoration: underline;
}
</style>