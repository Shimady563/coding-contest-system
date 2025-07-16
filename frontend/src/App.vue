<template>
  <div>
    <Navbar />
    <div class="page-content">
      <router-view />
      <notification ref="notification" />
    </div>
    <Footer />
  </div>
</template>

<script>
import Navbar from "./components/Navbar.vue";
import Footer from "./components/Footer.vue";
import Notification from '@/components/Notification.vue'

export default {
  components: { Navbar,
    Footer, Notification},
  methods: {
    notify(message, type = 'info') {
      this.$refs.notification.show(message, type)
    }
  },
};
</script>

<style scoped>
.page-content {
  min-height: 100vh;
  background: linear-gradient(to right, #dfe9f3, #ffffff);
  padding: 2rem;
}
</style>