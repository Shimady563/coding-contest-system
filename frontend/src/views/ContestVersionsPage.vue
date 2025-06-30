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
          <router-link :to="`/contest-version/${version.id}/tasks`" class="version-link">
            {{ version.name }}
          </router-link>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  name: "ContestVersionsPage",
  data() {
    return {
      versions: [],
      loading: true,
    };
  },
  async mounted() {
    try {
      const response = await fetch(`http://localhost:8080/api/v1/contest-versions?contestId=${contestId}`, {
       credentials: "include"
      });

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
