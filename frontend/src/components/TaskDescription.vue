<template>
  <div class="task-description">
    <h2>Task Description</h2>
    <p>{{ description }}</p>
  </div>
</template>

<script>
export default {
  props: {
    description: String,
  },
};
</script>

<style scoped>
.task-description {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.task-description h2 {
  font-size: 22px;
  margin-bottom: 12px;
  color: #333;
}

.task-description p {
  font-size: 16px;
  color: #555;
}
</style>
