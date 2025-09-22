<template>
  <div class="auth-container">
    <form @submit.prevent="login" class="auth-form">
      <h2>Вход в систему</h2>
      <div>
        <label>Email:</label>
        <input name="email" type="email" v-model="email" required autocomplete="email" />
      </div>
      <div>
        <label>Пароль:</label>
        <input name="current-password" type="password" v-model="password" required autocomplete="current-password" />
      </div>

      <!-- Контейнер для капчи -->
      <div 
        id="captcha-container" 
        style="height: 100px; margin: 20px 0;"
      ></div>

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
      captchaToken: null,
      widgetId: null,
    };
  },
  computed: {
    isSubmitDisabled() {
      return !this.email || !this.password;
    }
  },
  mounted() {
    // Callback для инициализации капчи
    window.onloadFunction = () => {
      if (window.smartCaptcha) {
        const container = document.getElementById('captcha-container');
        this.widgetId = window.smartCaptcha.render(container, {
          sitekey: '<ключ_клиента>', // ваш client key
          hl: 'ru',
          callback: (token) => {
            this.captchaToken = token;
          },
        });
      }
    };

    // Динамическая загрузка скрипта
    const script = document.createElement('script');
    script.src = 'https://smartcaptcha.yandexcloud.net/captcha.js?render=onload&onload=onloadFunction';
    script.defer = true;
    script.onerror = () => console.error('Ошибка загрузки SmartCaptcha');
    document.body.appendChild(script);
  },
  methods: {
    async login() {
      if (!this.captchaToken) {
        this.errorMessage = "Подтвердите, что вы не робот";
        return;
      }

      try {
        this.$root.notify("Попытка входа...", "info");
        await login({ 
          email: this.email, 
          password: this.password, 
          captchaToken: this.captchaToken
        });

        this.$root.notify("Вход выполнен успешно!", "success");
        this.$router.push("/").then(() => window.location.reload());
      } catch (err) {
        this.errorMessage = err.message || "Ошибка входа";
        this.$root.notify(this.errorMessage, "error");

        // сброс капчи после ошибки
        if (this.widgetId && window.smartCaptcha) {
          window.smartCaptcha.reset(this.widgetId);
        }
        this.captchaToken = null;
      }
    }
  },
};
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  }

.auth-form {
  background-color: #fff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  animation: fadeIn 0.4s ease-in-out;
}

h2 {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

form > div {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-sizing: border-box;
}

button {
  width: 100%;
  padding: 12px;
  background-color: #2f80ed;
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

button:hover:not(:disabled) {
  background-color: #1366d6;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

p {
  margin-top: 1rem;
  text-align: center;
}

.footer-link {
  margin-top: 20px;
}

.footer-link a {
  color: #007bff;
  text-decoration: none;
}

.footer-link a:hover {
  text-decoration: underline;
}

.error-message {
  color: red;
  font-size: 14px;
  margin-top: 10px;
  text-align: center;
}

form > div {
  margin-bottom: 1rem;
  padding-left: 12px;
  padding-right: 12px;
}
</style>