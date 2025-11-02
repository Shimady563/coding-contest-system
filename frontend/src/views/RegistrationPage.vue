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
        :error="confirmPassword && password !== confirmPassword"
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

      <p class="footer-link">
        Уже есть аккаунт? <router-link to="/login">Войти</router-link>
      </p>
    </form>
  </div>
</template>

<script>
import { signup } from "@/js/auth"
import { fetchGroups } from "@/js/manager";
import { validatePassword } from "@/js/password";
import Multiselect from "vue-multiselect";
import FloatingInput from "@/components/FloatingInput.vue";
import PasswordHints from "@/components/PasswordHints.vue";
import "vue-multiselect/dist/vue-multiselect.min.css";

export default {
  components: {
    Multiselect,
    FloatingInput,
    PasswordHints
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
    passwordValidation() {
    return validatePassword(this.password);
  },
  isPasswordValid() {
    return this.passwordValidation.isValid;
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

.error-message {
  color: #e74c3c;
  font-size: 14px;
  margin-top: -20px;
  margin-bottom: 1.8rem;
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
</style>