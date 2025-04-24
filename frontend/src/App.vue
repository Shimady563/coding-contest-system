<template>
  <div>
    <Navbar />
    <div class="page-content">
      <router-view />
    </div>
    <Footer />
  </div>
</template>

<script>
import Navbar from "@/components/Navbar.vue";
import Footer from "./components/Footer.vue";

export default {
  components: { Navbar,
    Footer },
};
</script>

<style scoped>

.page-content {
  min-height: 100vh;
  background: linear-gradient(to right, #dfe9f3, #ffffff);
  padding: 2rem;
}

</style>
