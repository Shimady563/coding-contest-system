<template>
  <div class="auth-container">
    <form @submit.prevent="login" class="auth-form">
      <h2>Вход в систему</h2>

      <FloatingInput
        v-model="email"
        id="email"
        label="Email"
        type="email"
        required
        placeholder=""
        autocomplete="email"
      />

      <FloatingInput
        v-model="password"
        id="current-password"
        name="current-password"
        label="Пароль"
        type="password"
        required
        placeholder=""
        autocomplete="current-password"
      />

      <button type="submit" class="btn primary" :disabled="isSubmitDisabled">Войти</button>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

      <p class="footer-link">
        Нет аккаунта? <router-link to="/register">Зарегистрироваться</router-link>
      </p>
    </form>
  </div>
</template>

<script>
import { login } from '@/js/auth'
import FloatingInput from '@/components/FloatingInput.vue'

export default {
  components: {
    FloatingInput,
  },
  data() {
    return {
      email: '',
      password: '',
      errorMessage: '',
    }
  },
  computed: {
    isSubmitDisabled() {
      return !this.email || !this.password
    },
  },
  methods: {
    async login() {
      this.errorMessage = ''
      try {
        this.$root.notify('Попытка входа...', 'info')
        await login({ email: this.email, password: this.password })

        this.$root.notify('Вход выполнен успешно!', 'success')
        this.$router.push('/').then(() => window.location.reload())
      } catch (err) {
        this.errorMessage = err.message || 'Ошибка при входе'
        this.$root.notify(this.errorMessage, 'error')
      }
    },
  },
}
</script>

<style scoped>
.error-message {
  margin-top: 16px;
}
</style>
