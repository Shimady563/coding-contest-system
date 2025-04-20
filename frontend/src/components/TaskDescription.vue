<template>
  <div class="task-description">
    <h2>Task Description</h2>
    <p>{{ taskText }}</p>
  </div>
</template>

<script>
export default {
  name: "TaskDescription",
  data() {
    return {
      taskText: `Here is a task description. Write a program that solves a specific problem.
      You can use C++ or another language to complete the task. The goal is to implement the correct algorithm
      and handle inputs/outputs properly.`
    };
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
