<template>
  <div class="student-contests-container">
    <h1>Мои контрольные работы</h1>

    <div v-if="loading" class="loading">Загрузка...</div>

    <div v-else-if="contests.length === 0" class="empty-state">
      Нет доступных контрольных для вашей группы.
    </div>

    <ul v-else class="contest-list">
      <li v-for="contest in contests" :key="contest.id" class="contest-item">
        <div class="contest-info">
          <router-link :to="`/contest/${contest.id}`" class="contest-link">
            {{ contest.name }}
          </router-link>
          <p class="description">{{ contest.description }}</p>
          <p class="time">
            🕓 {{ formatDate(contest.startTime) }} – {{ formatDate(contest.endTime) }}
          </p>
        </div>
        <span class="status">
          Статус: {{ getContestStatus(contest.startTime, contest.endTime) }}
        </span>
      </li>
    </ul>
  </div>
</template>


<script>
import { getGroupIdForCurrentUser } from "@/js/auth";

export default {
  name: "StudentContestsPage",
  data() {
    return {
      contests: [],
      loading: true,
    };
  },
  async mounted() {
    try {
      const groupId = await getGroupIdForCurrentUser();

      if (!groupId) {
        throw new Error("groupId is null");
      }

      const response = await fetch(`http://localhost:8080/api/v1/contests/group?groupId=${groupId}`, {
        credentials: 'include',
      });

      if (!response.ok) {
        throw new Error("Не удалось загрузить контрольные");
      }

      const data = await response.json();
      this.contests = data || [];
      console.log(data);
    } catch (error) {
      console.error("Ошибка при загрузке:", error.message);
    } finally {
      this.loading = false;
    }
  },
  methods: {
    getContestStatus(startTime, endTime) {
      const now = new Date();
      const start = new Date(startTime);
      const end = new Date(endTime);

      if (now < start) return "Ожидается";
      if (now > end) return "Завершена";
      return "Активна";
    },
    formatDate(dateStr) {
      const date = new Date(dateStr);
      return date.toLocaleString("ru-RU", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      });
    },
  },
};
</script>

<style scoped>
.student-contests-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

h1 {
  text-align: center;
  font-size: 28px;
  color: #2f2f2f;
  margin-bottom: 30px;
}

.loading {
  text-align: center;
  font-size: 18px;
  color: #888;
}

.empty-state {
  text-align: center;
  font-size: 18px;
  color: #777;
}

.contest-list {
  list-style: none;
  padding: 0;
}

.contest-item {
  padding: 15px;
  margin-bottom: 15px;
  background: #f5f7fa;
  border-radius: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.contest-link {
  font-size: 18px;
  color: #2f80ed;
  text-decoration: none;
  font-weight: 600;
}

.contest-link:hover {
  text-decoration: underline;
}

.status {
  font-size: 14px;
  color: #555;
}

.contest-info {
  flex: 1;
}

.description {
  margin: 8px 0;
  color: #666;
}

.time {
  font-size: 14px;
  color: #999;
}
</style>
