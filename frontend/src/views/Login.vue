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
      <button type="submit" class="btn primary">Войти</button>
      <p v-if="errorMessage" class="error-message">{{ errorMessage }}</p>
      <p class="footer-link">
        Нет аккаунта? <router-link to="/register">Зарегистрироваться</router-link>
      </p>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      email: "",
      password: "",
      errorMessage: "",
    };
  },
  methods: {
    async login() {
      try {
        const response = await fetch("http://localhost:8081/api/v1/auth/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email: this.email, password: this.password }),
        });

        if (!response.ok) {
          const err = await response.json();
          this.errorMessage = err.message || "Ошибка входа";
          return;
        }

        const data = await response.json();
        localStorage.setItem("tokenData", JSON.stringify(data));
        console.log("Успешный вход", data);

        this.$router.push("/profile").then(() => {
          window.location.reload(); 
        });

        const authHeader = `${data.type} ${data.accessToken}`;
        console.log("Authorization header:", authHeader);

        const meResponse = await fetch("http://localhost:8081/api/v1/auth/me", {
          headers: {
            "Authorization": authHeader, 
          },
        });

        if (!meResponse.ok) {
          const errText = await meResponse.text();
          console.error("Ошибка /auth/me:", meResponse.status, errText);
          if (meResponse.status === 401) {
            this.errorMessage = "Токен истек или неверный. Пожалуйста, войдите снова.";
          } else {
            this.errorMessage = "Ошибка авторизации. Попробуйте ещё раз.";
          }
          return;
        }

        const userData = await meResponse.json();
        console.log("Пользователь:", userData);

        this.$router.push("/");

      } catch (err) {
        console.error("Ошибка при входе", err);
        this.errorMessage = "Сервер недоступен или ошибка сети";
      }
    },

    async getUserData() {
      const tokenData = JSON.parse(localStorage.getItem("tokenData"));
      if (!tokenData || !tokenData.accessToken) {
        this.errorMessage = "Пользователь не авторизован";
        return;
      }

      console.log("Получаем данные пользователя с токеном:", tokenData.accessToken);

      const authHeader = `Bearer ${tokenData.accessToken}`;
      const meResponse = await fetch("http://localhost:8081/api/v1/auth/me", {
        headers: {
          "Authorization": authHeader, 
        },
      });

      if (!meResponse.ok) {
        const errText = await meResponse.text();
        console.error("Ошибка при получении данных пользователя", errText);
        return;
      }

      const userData = await meResponse.json();
      console.log("Данные пользователя:", userData);
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
  background-color: #f4f4f4;
}

.auth-form {
  background-color: #fff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
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

button:hover {
  background-color: #1366d6;
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
</style>
