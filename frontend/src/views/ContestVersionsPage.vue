<template>
  <div class="contest-versions-container">
    <h1>Варианты контрольной {{ contest?.name }}</h1>

    <div v-if="loading" class="loading">Загрузка...</div>

    <div v-else-if="versions.length === 0" class="empty-state">
      Варианты для этой контрольной пока не добавлены.
    </div>

    <ul v-else class="version-list">
      <li v-for="version in versions" :key="version.id" class="version-item">
        <div class="version-info">
          <a href="#" class="version-link" @click.prevent="confirmStart(version)">
            {{ version.name }}
          </a>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
import { getUserInfo } from "../js/auth";

export default {
  name: "ContestVersionsPage",
  data() {
    return {
      versions: [],
      loading: true,
    };
  },
  async mounted() {
    this.loadVersions();
  },
  methods: {
    async loadVersions() {
      const contestId = this.$route.params.contestId;

      try {
        const contestResponse = await fetch(`http://localhost:8080/api/v1/contests/${contestId}`, {
          credentials: "include"
        });
        if (!contestResponse.ok) throw new Error("Не удалось загрузить данные контеста");
        this.contest = await contestResponse.json();
      } catch {
        return;
      }

      const start = new Date(this?.contest.startTime);
      const end =  new Date(this?.contest.endTime);
      const now = new Date();

      if (now < start || now > end) {
        this.$router.replace('/access-denied-contest');
        return;
      }

      try {
        const response = await fetch(
          `http://localhost:8080/api/v1/contest-versions?contestId=${contestId}`,
          { credentials: "include" }
        );

        if (response.status === 403) {
          alert("Вы уже зарегистрированы на вариант.");
          return;
        }

        if (!response.ok) {
          throw new Error("Ошибка загрузки вариантов");
        }

        this.versions = await response.json();
      } catch {
      } finally {
        this.loading = false;
      }
    },
    async confirmStart(version) {
      const userInfo = await getUserInfo();
      try {
        // Сначала проверяем доступ к задачам варианта
        const tasksResponse = await fetch(
          `http://localhost:8080/api/v1/tasks/contest-version?contestVersionId=${version.id}`,
          {
            credentials: "include"
          }
        );

        if (tasksResponse.status === 403) {
          // Нет доступа — надо регистрироваться
          const confirmed = confirm(
            `Вы точно уверены, что хотите начать с вариантом "${version.name}"?\nПосле выбора смена будет невозможна.`
          );

          if (!confirmed) return;

          // Отправляем PATCH-запрос на регистрацию
          const patchResponse = await fetch(
            `http://localhost:8080/api/v1/users/start/${userInfo.id}`,
            {
              method: "PATCH",
              headers: {
                "Content-Type": "application/json"
              },
              credentials: "include",
              body: JSON.stringify({
                contestVersionId: version.id,
                contestId: this.$route.params.contestId
              })
            }
          );

          if (patchResponse.status === 403) {
            alert("Вы уже выбрали вариант и не можете сменить его.");
            return;
          }

          if (!patchResponse.ok) {
            throw new Error("Ошибка регистрации на вариант");
          }
          this.$router.push(`/contest/${this.$route.params.contestId}/contest-version/${version.id}`);
        } else if (tasksResponse.ok) {
          // Есть доступ — просто переходим к задачам
          this.$router.push(`/contest/${this.$route.params.contestId}/contest-version/${version.id}`);
        } else {
          throw new Error("Не удалось получить доступ к задачам варианта");
        }
      } catch (err) {
        alert("Произошла ошибка. Попробуйте позже.");
      }
    }
  }
};
</script>

<style scoped>
.contest-versions-container {
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

.version-list {
  list-style: none;
  padding: 0;
}

.version-item {
  padding: 15px;
  margin-bottom: 15px;
  background: #f5f7fa;
  border-radius: 10px;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.version-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
  background-color: #eef2f7;
}

.version-info h3 {
  margin: 0;
  color: #2f80ed;
}

.version-info p {
  margin: 5px 0 0;
  color: #555;
}

.version-link {
  font-size: 18px;
  color: #2f80ed;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.25s ease;
}

.version-link:hover {
  color: #1a73e8;
  text-decoration: underline;
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
