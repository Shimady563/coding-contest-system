<template>
  <div class="student-contests-container">
    <h1>Мои контрольные работы</h1>

    <div v-if="loading" class="loading">Загрузка...</div>

    <div v-else-if="contests.length === 0" class="empty-state">
      Нет доступных контрольных для вашей группы.
    </div>

    <ul v-else class="contest-list">
      <li v-for="contest in contests" :key="contest.id">
        <router-link
          :to="`/contest/${contest.id}`"
          class="contest-item"
          @click="selectContest(contest)"
        >
          <div class="contest-info">
            <div class="contest-title">{{ contest.name }}</div>
            <p class="description">{{ contest.description }}</p>
            <p class="time">
              🕓 {{ formatDate(contest.startTime) }} – {{ formatDate(contest.endTime) }}
            </p>
          </div>
          <span
            class="status"
            :class="getStatusClass(getContestStatus(contest.startTime, contest.endTime))"
          >
            Статус: {{ getContestStatus(contest.startTime, contest.endTime) }}
          </span>
        </router-link>
      </li>
    </ul>
  </div>
</template>

<script>
import { getGroupIdForCurrentUser } from "@/js/auth";
import { MANAGER_URL } from "@/js/api";

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

      const response = await fetch(`${MANAGER_URL}/contests/group?groupId=${groupId}`, {
        credentials: 'include',
      });

      if (!response.ok) {
        throw new Error("Не удалось загрузить контрольные");
      }

      const data = await response.json();
      this.contests = data || [];
    } catch {
    }
    finally {
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
    getStatusClass(status) {
      switch (status) {
        case "Ожидается":
          return "status-upcoming";
        case "Активна":
          return "status-active";
        case "Завершена":
          return "status-finished";
        default:
          return "";
      }
    }
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
  animation: fadeIn 0.4s ease-in-out;
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

.contest-list li {
  margin-bottom: 15px;
}

.contest-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f5f7fa;
  border-radius: 10px;
  padding: 15px;
  text-decoration: none;
  transition: all 0.2s ease;
  color: inherit;
}

.contest-item:hover {
  background-color: #e2e6ed;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
}

.contest-title {
  font-size: 18px;
  font-weight: 600;
  color: #2f80ed;
  margin-bottom: 6px;
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

.status {
  font-size: 14px;
  font-weight: 500;
}

.status-upcoming {
  color: #f39c12;
}

.status-active {
  color: #27ae60;
}

.status-finished {
  color: #c0392b;
}
</style>
