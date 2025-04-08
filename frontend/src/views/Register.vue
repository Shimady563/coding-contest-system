<template>
  <div class="auth-container">
    <h2>Регистрация</h2>
    <form @submit.prevent="register" class="auth-form">
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
        <input type="email" v-model="email" required />
      </div>
      <div>
        <label>Пароль:</label>
        <input type="password" v-model="password" required />
      </div>
      <div>
        <label>ID группы:</label>
        <input type="text" v-model="groupId" required />
      </div>
      <button type="submit" class="btn primary">Зарегистрироваться</button>
    </form>
    <p class="footer-link">Уже есть аккаунт? <router-link to="/login">Войти</router-link></p>
    <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
  </div>
</template>

<script>
export default {
  data() {
    return {
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      groupId: "",
      errorMessage: ""
    };
  },
  methods: {
    async register() {
      try {
        const response = await fetch("http://localhost:8081/api/v1/auth/signup", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify({ firstName: this.firstName, lastName: this.lastName, email: this.email, password: this.password, groupId: this.groupId })
        });

        if (!response.ok) {
          const err = await response.json();
          this.errorMessage = err.message || "Ошибка регистрации";
          return;
        }

        const data = await response.json();
        console.log("Регистрация успешна:", data);
        this.$router.push("/profile");
      } catch (err) {
        console.error("Ошибка при регистрации", err);
        this.errorMessage = "Сервер недоступен или ошибка сети";
      }
    }
  }
};
</script>

<style scoped>
.auth-container {
  max-width: 400px;
  margin: auto;
  padding: 2rem;
  text-align: left;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
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

p {
  margin-top: 1rem;
  text-align: center;
}


h2 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
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
}
</style>
