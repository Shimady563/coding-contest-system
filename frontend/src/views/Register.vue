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
        <select v-model="groupId" required>
          <option disabled value="">Выберите группу</option>
          <option v-for="group in groups" :key="group.id" :value="group.id">
            {{ group.name }}
          </option>
        </select>
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
import { fetchGroups } from "@/js/auth";

export default {
  data() {
    return {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      confirmPassword: "",
      groupId: "",
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
      this.errorMessage = "";
      this.$root.notify('Начата регистрация...', 'info');

      try {
        const response = await fetch("http://localhost:8081/api/v1/auth/signup", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include", // <-- cookie
          body: JSON.stringify({
            firstName: this.firstName,
            lastName: this.lastName,
            email: this.email,
            password: this.password,
            groupId: this.groupId,
          }),
        });

        if (!response.ok) {
          const err = await response.json();
          this.errorMessage = err.message || "Ошибка регистрации";
          this.$root.notify(this.errorMessage, 'error');
          return;
        }

        this.$root.notify('Регистрация прошла успешно!', 'success');
        this.$router.push("/").then(() => window.location.reload());
      } catch (err) {
        console.error("Ошибка при регистрации:", err);
        this.errorMessage = "Сервер недоступен или ошибка сети";
        this.$root.notify(this.errorMessage, 'error');
      }
    },

    async fetchGroupsList() {
      try {
        this.groups = await fetchGroups();
      } catch (error) {
        console.error("Ошибка загрузки групп:", error);
      }
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

input,
select {
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

@keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(15px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
  }
}
</style>
