<template>
  <div class="auth-container">
    <form @submit.prevent="register" class="auth-form">
      <h2>Регистрация</h2>

      <div class="floating-label">
        <input type="text" v-model="firstName" id="firstName" required placeholder=""/>
        <label for="firstName">Имя</label>
      </div>

      <div class="floating-label">
        <input type="text" v-model="lastName" id="lastName" required placeholder=""/>
        <label for="lastName">Фамилия</label>
      </div>

      <div class="floating-label">
        <input type="email" v-model="email" id="email" required autocomplete="email" placeholder="" />
        <label for="email">Email</label>
      </div>

      <div class="floating-label">
        <input
          type="password"
          v-model="password"
          id="password"
          required
          placeholder=""
          autocomplete="new-password"
        />
        <label for="password">Пароль</label>
        <div class="password-hints" v-if="password">
          <div :class="{ valid: hasMinLength }">
            <span class="hint-icon">✓</span>
            <span class="hint-text">Минимум 8 символов</span>
          </div>
          <div :class="{ valid: hasUpperCase }">
            <span class="hint-icon">✓</span>
            <span class="hint-text">Заглавная буква</span>
          </div>
          <div :class="{ valid: hasLowerCase }">
            <span class="hint-icon">✓</span>
            <span class="hint-text">Строчная буква</span>
          </div>
          <div :class="{ valid: hasDigit }">
            <span class="hint-icon">✓</span>
            <span class="hint-text">Цифра</span>
          </div>
          <div :class="{ valid: hasSpecialChar }">
            <span class="hint-icon">✓</span>
            <span class="hint-text">Спецсимвол @#$%^&+=!?*</span>
          </div>
        </div>
      </div>

      <div class="floating-label">
        <input
          type="password"
          v-model="confirmPassword"
          id="confirmPassword"
          required
          placeholder=""
          autocomplete="new-password"
        />
        <label for="confirmPassword">Повторите пароль</label>
        <small v-if="password && confirmPassword && password !== confirmPassword" class="error-message">
          Пароли не совпадают
        </small>
      </div>

      <div 
        class="floating-label multiselect-floating" 
        :class="{ active: groupId || $refs.groupSelect?.isOpen }"
      >
        <div class="custom-multiselect">
          <multiselect
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

      <p class="footer-link">
        Уже есть аккаунт? <router-link to="/login">Войти</router-link>
      </p>
    </form>
  </div>
</template>

<script>
import { signup } from "@/js/auth"
import { fetchGroups } from "@/js/manager";
import Multiselect from "vue-multiselect";
import "vue-multiselect/dist/vue-multiselect.min.css";

export default {
  components: {
    Multiselect
  },
  data() {
    return {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      confirmPassword: "",
      groupId: null,
      groups: [],
      errorMessage: "",
    };
  },
  computed: {
    hasMinLength() { return this.password.length >= 8; },
    hasUpperCase() { return /[A-Z]/.test(this.password); },
    hasLowerCase() { return /[a-z]/.test(this.password); },
    hasDigit() { return /\d/.test(this.password); },
    hasSpecialChar() { return /[@#$%^&+=!?*]/.test(this.password); },
    isPasswordValid() {
      return !this.password || (this.hasMinLength && this.hasUpperCase && this.hasLowerCase && this.hasDigit && this.hasSpecialChar);
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
      );
    }
  },
  methods: {
    async register() {
      try {
        this.$root.notify("Начата регистрация...", "info");

        await signup({
          firstName: this.firstName,
          lastName: this.lastName,
          email: this.email,
          password: this.password,
          groupId: this.groupId.id,
        });

        this.$root.notify("Регистрация прошла успешно!", "success");
        this.$router.push("/").then(() => window.location.reload());
      } catch (err) {
        this.errorMessage = err.message || "Ошибка регистрации";
        this.$root.notify(this.errorMessage, "error");
      }
    },
    async fetchGroupsList() {
      this.groups = await fetchGroups();
    }
  },
  mounted() {
    this.fetchGroupsList();
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
.floating-label input:not(:placeholder-shown) + label,
.floating-label.active label,
.floating-label:focus-within label {
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

.password-hints {
  margin-top: 8px;
  font-size: 13.5px;
  line-height: 1.4;
}

.password-hints div {
  display: flex;
  align-items: center;
  margin: 4px 0;
  color: #888;
  transition: color 0.2s ease;
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

.password-hints .valid {
  color: #27ae60;
}

.password-hints .valid .hint-icon {
  background-color: #27ae60;
  border-color: #27ae60;
  color: white;
}

.hint-text {
  flex: 1;
}

.error-message {
  color: #e74c3c;
  font-size: 14px;
  margin-top: 6px;
  text-align: center;
  line-height: 1.4;
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
}

button:hover:not(:disabled) {
  background-color: #1366d6;
  transform: translateY(-1px);
}

button:disabled {
  background-color: #cfcfcf;
  cursor: not-allowed;
}

.footer-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
}
.footer-link a {
  color: #2f80ed;
  text-decoration: none;
  font-weight: 500;
}
.footer-link a:hover {
  text-decoration: underline;
}

.custom-multiselect :deep(.multiselect) {
  min-height: 44px;
  margin-top: 6px;
  border-radius: 8px;
}

.custom-multiselect :deep(.multiselect__tags) {
  min-height: 44px;
  padding: 8px 36px 8px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: #fff;
  font-size: 15px;
  transition: all 0.25s ease;
}

.custom-multiselect :deep(.multiselect__tags:focus-within) {
  border-color: #2f80ed;
  box-shadow: 0 0 0 2px rgba(47, 128, 237, 0.12);
  outline: none;
}

.custom-multiselect :deep(.multiselect__input),
.custom-multiselect :deep(.multiselect__single) {
  font-size: 15px;
  padding: 3px;
  margin: 0;
  background: transparent;
  border: none;
}

.custom-multiselect :deep(.multiselect__placeholder) {
  color: rgba(0, 0, 0, 0.5);
  font-size: 15px;
}

.custom-multiselect :deep(.multiselect__select) {
  height: 42px;
  right: 6px;
  top: 1px;
  width: 30px;
  background: transparent;
  border-radius: 0 8px 8px 0;
}

.custom-multiselect :deep(.multiselect__select:before) {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 6px 5px 0 5px;
  border-color: #666 transparent transparent transparent;
  transition: transform 0.2s ease;
}

.custom-multiselect :deep(.multiselect--active .multiselect__select:before) {
  transform: translate(-50%, -50%) rotate(180deg);
}

.custom-multiselect :deep(.multiselect__select:hover) {
  background: rgba(0, 0, 0, 0.05);
}

.custom-multiselect :deep(.multiselect__select:hover:before) {
  border-color: #333 transparent transparent transparent;
}

.custom-multiselect :deep(.multiselect--active .multiselect__select) {
  background: rgba(0, 0, 0, 0.05);
}

.custom-multiselect :deep(.multiselect__content-wrapper) {
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-top: 4px;
  z-index: 10;
}

.custom-multiselect :deep(.multiselect__option) {
  padding: 8px 12px;
  font-size: 15px;
  min-height: 36px;
}

.custom-multiselect :deep(.multiselect__option--selected) {
  background-color: #d0ebff;
  color: #333;
}

.custom-multiselect :deep(.multiselect__option--highlight) {
  background: #2f80ed;
  color: white;
}

.floating-label.active label,
.floating-label:focus-within label {
  top: -8px !important;
  left: 10px !important;
  font-size: 12px !important;
  color: #2f80ed !important;
  background: #fff;
  transform: none;
}

.multiselect-floating {
  position: relative;
}

.multiselect-floating label {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(0, 0, 0, 0.5);
  pointer-events: none;
  padding: 0 4px;
  font-size: 15px;
  transition: all 0.25s ease;
  background: #fff;
  z-index: 3;
}

.multiselect-floating.active label {
  top: -8px;
  left: 10px;
  font-size: 12px;
  color: #2f80ed;
  background: #fff;
  transform: none;
}

.multiselect-floating.active :deep(.multiselect__placeholder) {
  display: none;
}

.multiselect-floating :deep(.multiselect),
.multiselect-floating :deep(.multiselect__tags),
.multiselect-floating :deep(.multiselect__content-wrapper) {
  z-index: auto !important;
}
</style>