<template>
  <div class="auth-container">
    <form @submit.prevent="login" class="auth-form">
      <h2>Вход в систему</h2>

      <div>
        <label>Email:</label>
        <input type="email" v-model="email" required />
      </div>

      <div>
        <label>Пароль:</label>
        <input type="password" v-model="password" required />
      </div>

      <div id="captcha-container" style="height: 100px; margin: 20px 0;">
         <input
                  type="hidden"
                  name="smart-token"
                  ref="captchaInput"
                  :value="captchaToken"
                />
      </div>

      <button type="submit" class="btn primary" :disabled="isSubmitDisabled">
        Войти
      </button>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <p v-if="successMessage" class="success-message">{{ successMessage }}</p>
    </form>
  </div>
</template>

<script>
import { login, captcha } from "@/js/auth"
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
      clientKey: config.SMART_CAPTCHA_CLIENT_KEY || ""
    };
  },
  computed: {
    isSubmitDisabled() {
      return !this.email || !this.password;
    },
  },
  mounted() {
    window.onloadFunction = () => {
      if (window.smartCaptcha && this.clientKey) {
        const container = document.getElementById("captcha-container");
        this.widgetId = window.smartCaptcha.render(container, {
          sitekey: this.clientKey,
          hl: "ru",
          callback: (token) => {
             this.captchaToken = token;
            if (this.$refs.captchaInput) {
              this.$refs.captchaInput.value = token;
            }
          },
        });
      }
    };

    const scriptElement = document.createElement('script');
    scriptElement.src = 'https://smartcaptcha.yandexcloud.net/captcha.js?render=onload&onload=onloadFunction';
    scriptElement.defer = true;
    scriptElement.onerror = () => console.error("Ошибка загрузки SmartCaptcha");
    document.body.appendChild(scriptElement);
  },
  methods: {
    async login() {
      this.errorMessage = "";
      this.successMessage = "";

      if (!this.captchaToken) {
        this.errorMessage = "Подтвердите, что вы не робот";
        return;
      }

      try {
        const res = await captcha({
          token: this.captchaToken
        });

        if (res.status !== "ok") {
          this.errorMessage = "Капча не пройдена";
           return;
        }

        try {
          this.$root.notify("Попытка входа...", "info");
          await login({ email: this.email, password: this.password });

          this.$root.notify("Вход выполнен успешно!", "success");
          window.location.href = "/";
        } catch (err) {
          this.errorMessage = err.message || "Ошибка входа";
          this.$root.notify(this.errorMessage, "error");
        }

        if (this.widgetId && window.smartCaptcha) {
          window.smartCaptcha.reset(this.widgetId);
        }
        this.captchaToken = null;
      } catch(e) {console.log(e);}
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
