<template>
  <div class="profile-wrapper">
    <div v-if="user" class="profile-card">
      <h1>Личный кабинет</h1>
      <div class="profile-header">
        <img src="/default-avatar.png" alt="Avatar" class="avatar" />
        <div>
          <h2>{{ user.firstName }} {{ user.lastName }}</h2>
          <p class="user-role">{{ user.role === 'teacher' ? 'Преподаватель' : 'Студент' }}</p>
        </div>
      </div>

      <div class="profile-info">
        <div class="info-item">
          <span>📧 Email:</span>
          <span>{{ user.email }}</span>
        </div>
        <div class="info-item">
          <span>👥 Группа:</span>
          <span>{{ user.groupName }}</span>
        </div>
      </div>
    </div>

    <div v-else>
      <p>Загрузка профиля...</p>
    </div>
  </div>
</template>

<script>
import { getUserInfo } from "../js/auth";

export default {
  data() {
    return {
      user: null,
    };
  },
  async created() {
    try {
      const userInfo = await getUserInfo();
      this.user = userInfo;
    } catch (err) {
      if (err.message === "UNAUTHORIZED") {
        this.$router.push("/login");
      } else {
      }
    }
  },
};
</script>

<style scoped>
.profile-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 90vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.profile-card {
  background: #fff;
  padding: 2rem;
  border-radius: 20px;
  max-width: 500px;
  width: 100%;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  animation: fadeIn 0.4s ease-in-out;
}

.profile-card:hover {
  transform: translateY(-5px);
}

.profile-header {
  display: flex;
  align-items: center;
  margin-bottom: 1.5rem;
}

.avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-right: 1rem;
  border: 3px solid #ccc;
}

h1{
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.user-role {
  font-size: 14px;
  color: #777;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  background: #f7f9fc;
  padding: 0.75rem 1rem;
  border-radius: 10px;
  color: #333;
}

.loading {
  text-align: center;
  font-size: 18px;
  color: #555;
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
