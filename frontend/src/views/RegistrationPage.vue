<template>
  <div class="auth-container">
    <form @submit.prevent="register" class="auth-form">
      <h2>Регистрация</h2>

      <div>
        <label>Имя:</label>
        <input type="text" v-model="firstName" required />
      </div>

      <div>
        <label>Фамилия:</label>
        <input type="text" v-model="lastName" required />
      </div>

      <div>
        <label>Email:</label>
        <input type="email" v-model="email" required autocomplete="email" />
      </div>

      <div>
        <label>Пароль:</label>
        <input type="password" v-model="password" required autocomplete="new-password" />
        <small v-if="password && !isPasswordValid" class="error-message">
          Пароль должен содержать минимум 8 символов, одну заглавную, одну строчную букву, цифру и спецсимвол
        </small>
      </div>

      <div>
        <label>Повторите пароль:</label>
        <input type="password" v-model="confirmPassword" required autocomplete="new-password" />
        <small v-if="password && confirmPassword && password !== confirmPassword" class="error-message">
          Пароли не совпадают
        </small>
      </div>

      <div>
        <label>Группа:</label>
        <multiselect
          v-model="groupId"
          :options="groups"
          :searchable="true"
          :allow-empty="false"
          :multiple="false"
          :select-label="''"
          :selected-label="''"
          :deselect-label="''"
          placeholder="Выберите группу"
          label="name"
          track-by="id"
          class="custom-multiselect"
          required
        >
        </multiselect>
      </div>

      <button type="submit" class="btn primary" :disabled="isSubmitDisabled">Зарегистрироваться</button>

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
    isPasswordValid() {
      return this.password.match(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=]).{8,}$/);
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
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
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

button:hover {
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
  margin-top: 8px;
}

form > div {
  margin-bottom: 1rem;
  padding-left: 12px;
  padding-right: 12px;
}

.custom-multiselect >>> .multiselect {
  min-height: 38px;
  margin-top: 6px;
}

.custom-multiselect >>> .multiselect__tags {
  min-height: 38px;
  padding: 8px 30px 8px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: white;
  font-size: 16px;
}

.custom-multiselect >>> .multiselect__tags:focus-within {
  border-color: #2f80ed;
  box-shadow: 0 0 0 2px rgba(47, 128, 237, 0.1);
  outline: none;
}

.custom-multiselect >>> .multiselect__input,
.custom-multiselect >>> .multiselect__single {
  font-size: 16px;
  padding: 0;
  margin: 0;
  background: transparent;
  border: none;
}

.custom-multiselect >>> .multiselect__input:focus {
  outline: none;
  box-shadow: none;
}

.custom-multiselect >>> .multiselect__placeholder {
  color: #999;
  margin: 0;
  padding: 0;
  font-size: 16px;
}

.custom-multiselect >>> .multiselect__select {
  height: 36px;
  right: 1px;
  top: 1px;
  width: 30px;
  padding: 0;
  background: transparent;
  border-radius: 0 8px 8px 0;
}

.custom-multiselect >>> .multiselect__select:before {
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

.custom-multiselect >>> .multiselect--active .multiselect__select:before {
  transform: translate(-50%, -50%) rotate(180deg);
}

.custom-multiselect >>> .multiselect__select:hover {
  background: rgba(0, 0, 0, 0.05);
}

.custom-multiselect >>> .multiselect__select:hover:before {
  border-color: #333 transparent transparent transparent;
}

.custom-multiselect >>> .multiselect--active .multiselect__select {
  background: rgba(0, 0, 0, 0.05);
}

.custom-multiselect >>> .multiselect__content-wrapper {
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-top: 4px;
  z-index: 10;
}

.custom-multiselect >>> .multiselect__option {
  padding: 8px 12px;
  font-size: 16px;
  min-height: 36px;
}

.custom-multiselect >>> .multiselect__option--selected {
  background-color: #d0ebff;
  color: #333;
  font-weight: normal;
}

.custom-multiselect >>> .multiselect__option--highlight {
  background: #2f80ed;
  color: white;
}

.custom-multiselect >>> .multiselect__option--selected.multiselect__option--highlight {
  background: #1366d6;
  color: white;
}
</style>