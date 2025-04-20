<template>
  <div class="manage-container">
    <h1>Управление контрольными и заданиями</h1>

    <!-- Меню для переключения между контрольными и заданиями -->
    <div class="tabs">
      <button
        :class="{ active: isTestsActive }"
        @click="isTestsActive = true"
      >Контрольные</button>
      <button
        :class="{ active: !isTestsActive }"
        @click="isTestsActive = false"
      >Задания</button>
    </div>

    <!-- Управление контрольными -->
    <div v-if="isTestsActive" class="test-management">
      <button @click="goToCreateTest">Создать контрольную</button>
      <ul class="test-list">
        <li v-for="test in tests" :key="test.id" class="test-item">
          <router-link :to="`/edit-test/${test.id}`" class="test-link">
            {{ test.name }}
          </router-link>
        </li>
      </ul>
    </div>

    <!-- Управление заданиями -->
    <div v-else class="task-management">
      <button @click="goToCreateTask">Создать задание</button>
      <div v-for="(task, index) in tasks" :key="task.id" class="task-item">
        <h3>{{ task.name }}</h3>
        <p>{{ task.description }}</p>
        <button @click="goToEditTask(task.id)">Редактировать</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isTestsActive: true, // Флаг для переключения между вкладками
      tests: [
        { id: 1, name: "Контрольная по алгоритмам" },
        { id: 2, name: "ООП в C++" },
        { id: 3, name: "Базы данных — часть 1" }
      ],
      tasks: [
        { id: 1, name: "Задание 1", description: "Описание задания 1" },
        { id: 2, name: "Задание 2", description: "Описание задания 2" },
        { id: 3, name: "Задание 3", description: "Описание задания 3" }
      ]
    };
  },
  methods: {
    goToCreateTest() {
      this.$router.push("/create-test");
    },
    goToCreateTask() {
      this.$router.push("/create-task");
    },
    goToEditTask(taskId) {
      this.$router.push(`/edit-task/${taskId}`);
    }
  }
};
</script>

<style scoped>
.manage-container {
  max-width: 900px;
  margin: 40px auto;
  padding: 20px;
  text-align: center;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  box-sizing: border-box;
}

h1 {
  font-size: 28px;
  margin-bottom: 20px;
  color: #333;
}

.tabs {
  margin-bottom: 20px;
}

.tabs button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  border: 1px solid #ddd;
  background-color: #f0f0f0;
  border-radius: 6px;
  transition: background-color 0.3s;
  margin: 0 10px;
}

.tabs button.active {
  background-color: #2f80ed;
  color: white;
  border-color: #2f80ed;
}

button {
  background: #2f80ed;
  color: #fff;
  border: none;
  padding: 12px 20px;
  font-size: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-bottom: 30px;
}

button:hover {
  background-color: #1366d6;
}

.test-list, .task-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.test-item, .task-item {
  margin-bottom: 15px;
}

.test-link {
  display: block;
  padding: 12px 16px;
  background-color: #f5f7fa;
  color: #2d2d2d;
  text-decoration: none;
  font-weight: 500;
  border-radius: 8px;
  transition: background-color 0.2s ease;
  text-align: left;
}

.test-link:hover {
  background-color: #e2e6ed;
  color: #1a73e8;
}

.task-management {
  margin-top: 30px;
}

.task-item {
  background: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
