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
          <i class="fas fa-envelope"></i>
          <span>Email:</span>
          <span>{{ user.email }}</span>
        </div>
        <div class="info-item">
          <i class="fas fa-users"></i>
          <span>Группа:</span>
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
  padding: 20px;
}

.profile-card {
  background: #fff;
  padding: 2rem;
  border-radius: 20px;
  max-width: 520px;
  width: 100%;
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  animation: fadeIn 0.4s ease-in-out;
}

.profile-card:hover {
  transform: translateY(-4px);
}

h1 {
  text-align: center;
  font-size: 1.8rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.avatar {
  width: 84px;
  height: 84px;
  border-radius: 50%;
  border: 4px solid #2f80ed33;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.profile-header h2 {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 600;
  color: #2f80ed;
}

.user-role {
  font-size: 0.95rem;
  color: #7f8c8d;
  margin-top: 4px;
}

.profile-info {
  display: grid;
  gap: 1rem;
  margin-top: 1.5rem;
  word-break: break-word;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  background: #f9fafc;
  padding: 0.75rem 1rem;
  border-radius: 12px;
  font-size: 0.97rem;
  color: #34495e;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.6);
  flex-wrap: wrap;
}

.info-item::before {
  content: attr(data-icon);
  font-size: 1.2rem;
  color: #2f80ed;
}

.loading {
  text-align: center;
  font-size: 18px;
  color: #555;
}
</style>
