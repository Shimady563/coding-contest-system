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
  background-color: #f4f4f4;
  padding: 20px;
  border-radius: 5px;
  margin-bottom: 20px;
}

.task-description h2 {
  margin-bottom: 10px;
  font-size: 20px;
}

.task-description p {
  font-size: 16px;
  color: #333;
}
</style>
