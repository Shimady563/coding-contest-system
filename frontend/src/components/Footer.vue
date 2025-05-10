<template>
  <footer class="footer">
    <div class="container">
      <div class="copyright">
        &copy; {{ currentYear }} coding-contest-system
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  data() {
    return {
      currentYear: new Date().getFullYear(),
    };
  },
};
</script>

<style scoped>
.footer {
  padding: 20px 0;
  text-align: center;
  background: #f5f5f5;
  border-top: 1px solid #e0e0e0;
}

.copyright {
  color: #777;
  font-size: 0.9em;
}
</style>
