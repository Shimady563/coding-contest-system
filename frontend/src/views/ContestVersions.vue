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
    <Modal v-if="showConfirm" @close="showConfirm = false">
      <template #header>
        <h2>Подтверждение</h2>
      </template>
      <template #body>
        <p>Вы уверены, что хотите выбрать вариант "{{ selectedVersion?.name }}"? После выбора изменить его будет нельзя.</p>
      </template>
      <template #footer>
        <button class="btn" @click="showConfirm = false">Отмена</button>
        <button class="btn btn-primary" @click="proceedStart">Подтвердить</button>
      </template>
    </Modal>
  </div>
</template>

<script>
import { getUserInfo } from "../js/auth";
import { getContest, getContestVersionsByContest, getTasksByContestVersion, startContestForUser } from "../js/manager";
import Modal from "@/components/Modal.vue";

export default {
  name: "ContestVersionsPage",
  components: { Modal },
  data() {
    return {
      versions: [],
      loading: true,
      contest: null,
      showConfirm: false,
      selectedVersion: null,
    };
  },
  async mounted() {
    this.loadVersions();
  },
  methods: {
    async loadVersions() {
      const contestId = this.$route.params.contestId;

      try {
        this.contest = await getContest(contestId);
      } catch {
        return;
      }

      const start = new Date(this?.contest.startTime);
      const end =  new Date(this?.contest.endTime);
      const now = new Date();

      if (now < start || now > end) {
        this.$router.replace('/access-denied-time');
        return;
      }

      try {
        this.versions = await getContestVersionsByContest(contestId);
      } finally {
        this.loading = false;
      }
    },
    async confirmStart(version) {
      try {
        const tasks = await getTasksByContestVersion(version.id);
        if (tasks && tasks.length > 0) {
          this.$router.push(`/contests/${this.$route.params.contestId}/contest-version/${version.id}`);
          return;
        }
      } catch (err) {}

      this.selectedVersion = version;
      this.showConfirm = true;
    },
    async proceedStart() {
      const version = this.selectedVersion;
      this.showConfirm = false;
      const userInfo = await getUserInfo();
      try {
        await startContestForUser(userInfo.id, { 
          contestVersionId: version.id, 
          contestId: this.$route.params.contestId 
        });
        this.$router.push(`/contests/${this.$route.params.contestId}/contest-version/${version.id}`);
      } catch {
        this.$root.notify('Вы уже выбрали вариант и не можете сменить его.', 'error');
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
</style>
