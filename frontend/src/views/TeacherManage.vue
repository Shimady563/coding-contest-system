<template>
  <div class="manage-container">
    <h1>Управление контрольными и заданиями</h1>

    <!-- Меню для переключения -->
    <div class="tabs">
      <button :class="{ active: isTestsActive }" @click="isTestsActive = true">Контрольные</button>
      <button :class="{ active: !isTestsActive }" @click="isTestsActive = false">Задания</button>
    </div>

    <!-- Контрольные -->
    <div v-if="isTestsActive" class="management-section">
      <button class="create-btn" @click="goToCreateTest">Создать контрольную</button>
      <ul class="items-list">
        <li v-for="test in tests" :key="test.id" class="item">
          <div class="item-link">
            <div class="item-title">{{ test.name }}</div>
            <div class="item-description">{{ test.description }}</div>
          </div>
        </li>
      </ul>
    </div>

    <!-- Задания -->
    <div v-else class="management-section">
      <button class="create-btn " @click="goToCreateTask">Создать задание</button>
      <ul class="items-list">
        <li v-for="task in tasks" :key="task.id" class="item">
          <router-link :to="`/edit-task/${task.id}`" class="item-link">
            <div class="item-title">{{ task.name }}</div>
            <div class="item-description">{{ task.description }}</div>
          </router-link>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isTestsActive: true,
      tests: [],
      tasks: [],
    };
  },
  mounted() {
    this.fetchContests();
    this.fetchTasks();
  },
  methods: {
    async fetchContests() {
      try {
        const response = await fetch("http://localhost:8080/api/v1/contests?name=", {
          credentials: "include"
        });

        if (!response.ok) {
          throw new Error('Не удалось загрузить контрольные работы');
        }

        const data = await response.json();
        console.log("Контрольные пришли:", data);
        this.tests = data.content || [];
      } catch (error) {
        console.error("Ошибка при получении контрольных:", error.message);
      }
    },

    async fetchTasks() {
      try {
        const response = await fetch("http://localhost:8080/api/v1/tasks", {
          credentials: "include"
        });

        if (!response.ok) {
          throw new Error('Не удалось загрузить задания');
        }

        const data = await response.json();
        this.tasks = data.content || [];
      } catch (error) {
        console.error("Ошибка при получении заданий:", error.message);
      }
    },
    saveTestToStorage(test) {
      localStorage.setItem('selectedTest', JSON.stringify(test));
    },
    goToCreateTest() {
      this.$router.push("/create-contest");
    },
    goToCreateTask() {
      this.$router.push("/create-task");
    },
    goToEditTask(taskId) {
      this.$router.push(`/edit-task/${taskId}`);
    },
  },
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
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.tabs button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  border: 1px solid #ddd;
  background-color: #f0f0f0;
  border-radius: 6px;
  transition: all 0.3s;
  margin: 0;
}

.tabs button.active {
  background-color: #2f80ed;
  color: white;
  border-color: #2f80ed;
}

.management-section {
  margin-top: 20px;
}

.create-btn {
  display: block;
  width: 100%;
  max-width: 300px;
  margin: 0 auto 30px auto;
  padding: 12px 20px;
  font-size: 16px;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  color: white;
}

.create-btn {
  background-color: #34d399; 
}

.create-btn:hover {
  opacity: 0.9;
  background-color: #10b981; 
}

.items-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.item {
  margin-bottom: 15px;
}

.item-link {
  display: block;
  padding: 16px;
  background-color: #f5f7fa;
  color: #2d2d2d;
  text-decoration: none;
  font-weight: 500;
  border-radius: 8px;
  transition: all 0.2s ease;
  text-align: left;
}

.item-link:hover {
  background-color: #e2e6ed;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.item-title {
  font-size: 18px;
  font-weight: 600;
  color: #2d2d2d;
  margin-bottom: 6px;
}

.item-description {
  font-size: 14px;
  color: #888;
}
</style>