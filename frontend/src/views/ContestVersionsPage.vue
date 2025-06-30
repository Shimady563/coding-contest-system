<template>
  <div class="contest-versions-container">
    <h1>Варианты контрольной</h1>

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
  methods: {
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
                contestId: this.$route.params.id
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

          // Регистрация прошла успешно — переходим к задачам
          this.$router.push(`/contest-version/${version.id}/tasks`);
        } else if (tasksResponse.ok) {
          // Есть доступ — просто переходим к задачам
          this.$router.push(`/contest-version/${version.id}/tasks`);
        } else {
          throw new Error("Не удалось получить доступ к задачам варианта");
        }
      } catch (err) {
        console.error("Ошибка при обработке варианта:", err);
        alert("Произошла ошибка. Попробуйте позже.");
      }
    }
  },
  async mounted() {
    const userInfo = await getUserInfo();
    console.log("Информация о пользователе:", userInfo);

    const contestId = this.$route.params.id;

    if (!contestId) {
      console.error("Не удалось получить contestId или токен.");
      this.loading = false;
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/contest-versions?contestId=${contestId}`,
        { credentials: "include" }
      );

      if (response.status === 403) {
        // Пользователь уже выбрал вариант — направляем сразу к задачам
        alert("Вы уже зарегистрированы на вариант.");
        return;
      }

      if (!response.ok) {
        throw new Error("Ошибка загрузки вариантов");
      }

      this.versions = await response.json();
    } catch (error) {
      console.error("Ошибка:", error.message);
    } finally {
      this.loading = false;
    }
  },
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
}

.version-link:hover {
  text-decoration: underline;
}

</style>
