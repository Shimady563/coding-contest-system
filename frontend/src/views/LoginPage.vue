<template>
  <div class="auth-container">
    <form @submit.prevent="login" class="auth-form">
      <h2>Вход с фронтенд-капчей</h2>

      <div>
        <label>Email:</label>
        <input type="email" v-model="email" required />
      </div>

      <div>
        <label>Пароль:</label>
        <input type="password" v-model="password" required />
      </div>

      <!-- Контейнер для капчи -->
      <div id="captcha-container" style="height: 100px; margin: 20px 0;"></div>

      <button type="submit" class="btn primary" :disabled="isSubmitDisabled">
        Войти
      </button>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
    </form>
  </div>
</template>

<script>
import { config } from '../config';

export default {
  data() {
    return {
      email: "",
      password: "",
      captchaToken: null,
      widgetId: null,
      errorMessage: "",
      successMessage: "",
      clientKey: config.SMART_CAPTCHA_CLIENT_KEY || "",
      serverKey: config.SMART_CAPTCHA_SECRET_KEY || "",
    };
  },
  computed: {
    isSubmitDisabled() {
      return !this.email || !this.password;
    },
  },
  mounted() {
    // Callback для инициализации капчи
    window.onloadFunction = () => {
      if (window.smartCaptcha && this.clientKey) {
        const container = document.getElementById("captcha-container");
        this.widgetId = window.smartCaptcha.render(container, {
          sitekey: this.clientKey,
          hl: "ru",
          callback: (token) => {
            this.captchaToken = token;
          },
        });
      }
    };

    // Динамическая загрузка скрипта капчи
    const script = document.createElement("script");
    script.src =
      "https://smartcaptcha.yandexcloud.net/captcha.js?render=onload&onload=onloadFunction";
    script.defer = true;
    script.onerror = () => console.error("Ошибка загрузки SmartCaptcha");
    document.body.appendChild(script);
  },
  methods: {
    async login() {
      this.errorMessage = "";
      this.successMessage = "";

      if (!this.captchaToken) {
        this.errorMessage = "Подтвердите, что вы не робот";
        return;
      }

      if (!this.serverKey) {
        this.errorMessage = "Серверный ключ капчи не задан";
        return;
      }

      try {
        // Проверка капчи через API Яндекс
        const params = new URLSearchParams({
          secret: this.serverKey,
          token: this.captchaToken,
          ip: "127.0.0.1", // для демонстрации
        });

        const res = await fetch(
          "https://smartcaptcha.yandexcloud.net/validate",
          {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params.toString(),
          }
        );

        const data = await res.json();

        if (data.status !== "ok") {
          throw new Error("Капча не пройдена или истекла");
        }

        // Имитируем проверку логина
        if (this.email === "test@example.com" && this.password === "123456") {
          this.successMessage = "Успешный вход!";
        } else {
          throw new Error("Неверный email или пароль");
        }
      } catch (err) {
        this.errorMessage = err.message;

        // Сброс капчи
        if (this.widgetId && window.smartCaptcha) {
          window.smartCaptcha.reset(this.widgetId);
        }
        this.captchaToken = null;
      }
    },
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
}
input {
  width: 100%;
  padding: 10px;
  margin-bottom: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
}
button {
  width: 100%;
  padding: 12px;
  background-color: #2f80ed;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
.error-message {
  color: red;
  margin-top: 10px;
  text-align: center;
}
.success-message {
  color: green;
  margin-top: 10px;
  text-align: center;
}
</style>