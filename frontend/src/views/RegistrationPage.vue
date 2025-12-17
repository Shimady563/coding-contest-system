<template>
  <div class="auth-container">
    <form @submit.prevent="register" class="auth-form">
      <h2>Регистрация</h2>

      <FloatingInput
        v-model="firstName"
        id="firstName"
        name="firstName"
        label="Имя"
        type="text"
        required
        placeholder=""
      />

      <FloatingInput
        v-model="lastName"
        id="lastName"
        name="lastName"
        label="Фамилия"
        type="text"
        required
        placeholder=""
      />

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
        id="password"
        name="password"
        label="Пароль"
        type="password"
        required
        placeholder=""
        autocomplete="password"
      >
        <PasswordHints :password="password" />
      </FloatingInput>

      <FloatingInput
        v-model="confirmPassword"
        id="confirmPassword"
        name="confirmPassword"
        label="Повторите пароль"
        type="password"
        required
        placeholder=""
        autocomplete="new-password"
        :error="confirmPassword !== '' && password !== '' && password !== confirmPassword"
      >
        <template v-if="confirmPassword && password !== confirmPassword">
          <small class="error-message">Пароли не совпадают</small>
        </template>
      </FloatingInput>

      <div
        class="floating-label multiselect-floating"
        :class="{ active: groupId || $refs.groupSelect?.isOpen }"
      >
        <div class="custom-multiselect">
          <multiselect
            id="group"
            name="group"
            ref="groupSelect"
            v-model="groupId"
            :options="groups"
            :searchable="true"
            :allow-empty="false"
            :multiple="false"
            :select-label="''"
            :selected-label="''"
            :deselect-label="''"
            :append-to-body="true"
            open-direction="below"
            placeholder=""
            label="name"
            track-by="id"
            required
            @open="$forceUpdate()"
            @close="$forceUpdate()"
          />
        </div>
        <label for="group">Группа</label>
      </div>

      <button type="submit" class="btn primary" :disabled="isSubmitDisabled">
        Зарегистрироваться
      </button>

      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>

      <p class="footer-link">Уже есть аккаунт? <router-link to="/login">Войти</router-link></p>
    </form>
  </div>
</template>

<script>
import { signup } from '@/js/auth'
import { fetchGroups } from '@/js/manager'
import { validatePassword } from '@/js/password'
import Multiselect from 'vue-multiselect'
import FloatingInput from '@/components/FloatingInput.vue'
import PasswordHints from '@/components/PasswordHints.vue'
import 'vue-multiselect/dist/vue-multiselect.min.css'

export default {
  components: {
    Multiselect,
    FloatingInput,
    PasswordHints,
  },
  data() {
    return {
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      confirmPassword: '',
      groupId: null,
      groups: [],
      errorMessage: '',
    }
  },
  computed: {
    passwordValidation() {
      return validatePassword(this.password)
    },
    isPasswordValid() {
      return this.passwordValidation.isValid
    },
    isSubmitDisabled() {
      return (
        !this.firstName ||
        !this.lastName ||
        !this.email ||
        !this.password ||
        !this.confirmPassword ||
        !this.groupId ||
        this.password !== this.confirmPassword ||
        !this.isPasswordValid
      )
    },
  },
  methods: {
    async register() {
      try {
        this.$root.notify('Начата регистрация...', 'info')

        await signup({
          firstName: this.firstName,
          lastName: this.lastName,
          email: this.email,
          password: this.password,
          groupId: this.groupId.id,
        })

        this.$root.notify('Регистрация прошла успешно!', 'success')
        this.$router.push('/').then(() => window.location.reload())
      } catch (err) {
        this.errorMessage = err.message || 'Ошибка регистрации'
        this.$root.notify(this.errorMessage, 'error')
      }
    },
    async fetchGroupsList() {
      this.groups = await fetchGroups()
    },
  },
  mounted() {
    this.fetchGroupsList()
  },
}
</script>

<style scoped>
.error-message {
  margin-top: -20px;
  margin-bottom: 1.8rem;
}
</style>
