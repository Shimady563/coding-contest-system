<template>
  <div>
    <Navbar />
    <div class="page-content">
      <router-view />
    </div>
  </div>
</template>

<script>
import Navbar from "@/components/Navbar.vue";

export default {
  components: { Navbar },
};
</script>

<style>
.page-content {
  padding-top: 60px; 
}
</style>