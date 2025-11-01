<template>
  <div>
    <div v-if="loading" class="spinner-overlay">
      <div class="loader"></div>
    </div>

    <div v-else>
      <Navbar />
      <div class="page-content">
        <router-view />
        <notification ref="notification" />
      </div>
      <Footer />
    </div>
  </div>
</template>

<script>
import Navbar from "./components/Navbar.vue";
import Footer from "./components/Footer.vue";
import Notification from "@/components/Notification.vue";
import { refreshAuth } from "./js/auth";

export default {
  components: { Navbar, Footer, Notification },
  data() {
    return {
      loading: true,
    };
  },
  async created() {
    try {
      await refreshAuth();
    } catch {
    } finally {
      this.loading = false;
    }
  },
  methods: {
    notify(message, type = "info") {
      this.$refs.notification.show(message, type);
    },
  },
};
</script>

<style scoped>
.page-content {
  min-height: 100vh;
  background: linear-gradient(to right, #dfe9f3, #ffffff);
  padding: 2rem;
}

.spinner-overlay {
  position: fixed;
  inset: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background: white;
  z-index: 9999;
}

.loader {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>