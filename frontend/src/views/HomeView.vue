<template>
  <div class="home-page">
    <div v-if="isLoading" class="loading-container">
      <div class="loader"></div>
    </div>

    <template v-else>
      <!-- Гостевой доступ -->
      <header class="header" v-if="!user">
        <h1>Добро пожаловать 👋</h1>
        <p class="subtitle">
          В системе контроля работ вы можете создавать и решать задания, получать мгновенную обратную связь и отслеживать свой прогресс!
        </p>
        <div class="actions">
          <router-link to="/login" class="btn primary">Войти</router-link>
          <router-link to="/register" class="btn secondary">Регистрация</router-link>
        </div>
      </header>

      <!-- Студент -->
      <header class="header" v-else-if="user.role === 'student'">
        <h1>Привет, {{ user.firstName }} {{ user.lastName }} 🎓</h1>
        <p class="subtitle">Доступные функции:</p>

        <div class="cards">
          <div class="card">
            <div class="card-top-line"></div>
            <div class="icon-container">
              <svg class="icon" viewBox="0 0 24 24">
                <path fill="currentColor" d="M5,3H19A2,2 0 0,1 21,5V19A2,2 0 0,1 19,21H5A2,2 0 0,1 3,19V5A2,2 0 0,1 5,3M7,7V9H17V7H7M7,11V13H17V11H7M7,15V17H14V15H7Z" />
              </svg>
            </div>
            <h3>Контрольные</h3>
            <p>Просматривай доступные контрольные и задания.</p>
          </div>
          <div class="card">
            <div class="card-top-line"></div>
            <div class="icon-container">
              <svg class="icon" viewBox="0 0 24 24">
                <path fill="currentColor" d="M22,21H2V3H4V19H6V10H10V19H12V6H16V19H18V14H22V21Z" />
              </svg>
            </div>
            <h3>Результаты</h3>
            <p>Решай контрольные и получай мгновенную обратную связь.</p>
          </div>
        </div>
      </header>

      <!-- Преподаватель -->
      <header class="header" v-else-if="user.role === 'teacher'">
        <h1>Здравствуйте, {{ user.firstName }} {{ user.lastName }} 👨‍🏫</h1>
        <p class="subtitle">Вот что вы можете делать:</p>

        <div class="cards">
          <div class="card">
            <div class="card-top-line"></div>
            <div class="icon-container">
              <svg class="icon" viewBox="0 0 24 24">
                <path fill="currentColor" d="M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z" />
              </svg>
            </div>
            <h3>Контрольные</h3>
            <p>Формируйте новые задания и контрольные работы для студентов.</p>
          </div>
          
          <div class="card">
            <div class="card-top-line"></div>
            <div class="icon-container">
              <svg class="icon" viewBox="0 0 24 24">
                <path fill="currentColor" d="M20.71,7.04C21.1,6.65 21.1,6 20.71,5.63L18.37,3.29C18,2.9 17.35,2.9 16.96,3.29L15.12,5.12L18.87,8.87M3,17.25V21H6.75L17.81,9.93L14.06,6.18L3,17.25Z" />
              </svg>
            </div>
            <h3>Задания</h3>
            <p>Редактируйте, удаляйте и обновляйте контрольные работы.</p>
          </div>

          <div class="card">
            <div class="card-top-line"></div>
            <div class="icon-container">
              <svg class="icon" viewBox="0 0 24 24">
                <path fill="currentColor" d="M12,4A4,4 0 0,1 16,8A4,4 0 0,1 12,12A4,4 0 0,1 8,8A4,4 0 0,1 12,4M12,14C16.42,14 20,15.79 20,18V20H4V18C4,15.79 7.58,14 12,14Z" />
              </svg>
            </div>
            <h3>Студенты</h3>
            <p>Просматривайте список студентов, редактируйте и удаляйте их профили.</p>
          </div>

          <div class="card">
            <div class="card-top-line"></div>
            <div class="icon-container">
              <svg class="icon" viewBox="0 0 24 24">
                <path fill="currentColor" d="M12,5A3.5,3.5 0 0,1 15.5,8.5A3.5,3.5 0 0,1 12,12A3.5,3.5 0 0,1 8.5,8.5A3.5,3.5 0 0,1 12,5M4,8.5A2.5,2.5 0 0,1 6.5,11A2.5,2.5 0 0,1 4,13.5A2.5,2.5 0 0,1 1.5,11A2.5,2.5 0 0,1 4,8.5M20,8.5A2.5,2.5 0 0,1 22.5,11A2.5,2.5 0 0,1 20,13.5A2.5,2.5 0 0,1 17.5,11A2.5,2.5 0 0,1 20,8.5M12,14C14.67,14 20,15.34 20,18V20H4V18C4,15.34 9.33,14 12,14Z" />
              </svg>
            </div>
            <h3>Группы</h3>
            <p>Просматривайте, создавайте и удаляйте учебные группы.</p>
          </div>

          <div class="card">
            <div class="card-top-line"></div>
            <div class="icon-container">
              <svg class="icon" viewBox="0 0 24 24">
                <path fill="currentColor" d="M22,21H2V3H4V19H6V10H10V19H12V6H16V19H18V14H22V21Z" />
              </svg>
            </div>
            <h3>Результаты</h3>
            <p>Анализируйте успеваемость студентов по различным метрикам.</p>
          </div>
        </div>
      </header>
    </template>
  </div>
</template>

<script>
import { getUserInfo } from "@/js/auth";

export default {
  name: "HomePage",
  data() {
    return {
      user: null,
      isLoading: true,
      error: null,
      showWelcome: false,
    };
  },
  async created() {
    try {
      this.user = await getUserInfo();
    
      const hasSeenWelcome = localStorage.getItem("seenWelcome") === "true";

      if (!hasSeenWelcome && this.user) {
        this.showWelcome = true;
        localStorage.setItem("seenWelcome", "true");
        this.$root.notify(`Добро пожаловать, ${this.user.firstName}!`, 'success');
      }
    } finally {
      this.isLoading = false;
    }
  }
};
</script>

<style scoped>
.home-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 90vh;
  padding: 20px;
}

.header {
  text-align: center;
  background: white;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
  max-width: 900px;
  width: 100%;
  animation: fadeIn 0.4s ease-in-out;
}

h1 {
  font-size: 2.2rem;
  color: #2c3e50;
  margin-bottom: 1rem;
  font-weight: 600;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-top: 2rem;
}

.card {
  background: rgba(255, 255, 255, 0.96);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  transition: all 0.25s ease;
  text-align: center;
  position: relative;
  overflow: hidden;
  border: 1px solid #e0e0e0;
}

.card-top-line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #2f80ed, #56ccf2);
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px rgba(47, 128, 237, 0.1);
}

.icon-container {
  width: 50px;
  height: 50px;
  margin: 1rem auto;
  background: linear-gradient(135deg, #2f80ed, #56ccf2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon {
  width: 24px;
  height: 24px;
}

.card h3 {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #2c3e50;
}

.card p {
  font-size: 0.875rem;
  color: #6b7b8c;
  line-height: 1.4;
  margin: 0;
}

.subtitle {
  font-size: 1.1rem;
  color: #7f8c8d;
  margin-bottom: 2rem;
  line-height: 1.5;
}

.actions {
  display: flex;
  justify-content: center;
  flex-direction: row; 
  gap: 10px; 
  padding: 0 10px; 
  flex-wrap: wrap;
  margin-top: 2rem;
  max-width: 100%;
}

.btn {
  padding: 0.8rem 2rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  box-sizing: border-box; 
  flex: 1; 
  min-width: 120px; 
  text-align: center;
}

.btn.primary {
  background-color: #2f80ed;
  color: white;
}

.btn.primary:hover {
  background-color: #1e63c5;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(41, 128, 185, 0.3);
}

.btn.secondary {
  background-color: #95a5a6;
  color: white;
}

.btn.secondary:hover {
  background-color: #7f8c8d;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(127, 140, 141, 0.3);
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.loader {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
}

@media (max-width: 768px) {
  .cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .card {
    padding: 20px;
  }
}

@media (max-width: 600px) {
  .header {
    padding: 20px 15px; 
  }

  h1 {
    font-size: 1.8rem;
  }

  .btn {
    padding: 0.7rem 1rem; 
    font-size: 0.9rem;
  }
}

@media (max-width: 400px) {
  .actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}
</style>