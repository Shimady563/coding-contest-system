<template>

<h1 class="title">Создание контрольной работы</h1>

<div class="create-cr-container">

    <div class="form-group">
      <label for="name">Название</label>
      <input id="name" v-model="controlWork.name" type="text" placeholder="Введите название" />
    </div>

    <div class="form-group">
      <label for="description">Описание</label>
      <textarea id="description" v-model="controlWork.description" rows="3" placeholder="Краткое описание контрольной" />
    </div>

    <div class="form-group">
      <label for="group">Группа</label>
      <select id="group" v-model="controlWork.group">
        <option disabled value="">-- Выберите группу --</option>
        <option v-for="group in groups" :key="group.id" :value="group.id">
          {{ group.name }}
        </option>
      </select>
    </div>

    <div class="form-row">
      <div class="form-group">
        <label for="start">Начало</label>
        <input id="start" type="datetime-local" v-model="controlWork.startTime" />
      </div>
      <div class="form-group">
        <label for="end">Окончание</label>
        <input id="end" type="datetime-local" v-model="controlWork.endTime" />
      </div>
    </div>

    <h2 class="subtitle">Варианты</h2>
    <div v-for="(variant, index) in variants" :key="index" class="variant-block">
      <VariantForm
        :variant="variant"
        :allTasks="tasks"
        @remove="removeVariant(index)"
        @update="(updated) => updateVariant(index, updated)"
      />
    </div>

    <div class="btn-group">
      <button class="btn btn-secondary" @click="addVariant">+ Добавить вариант</button>
      <button class="btn btn-primary" @click="saveControlWork">💾 Сохранить контрольную</button>
    </div>
  </div>
</template>

<script>
import VariantForm from '../components/VariantForm.vue';
import { fetchGroups } from '../js/auth.js'; // Импортируем функцию

export default {
  components: { VariantForm },
  data() {
    return {
      controlWork: {
        name: '',
        description: '',
        group: '',
        startTime: '',
        endTime: '',
      },
      groups: [],
      tasks: [],
      variants: [],
    };
  },
  mounted() {
    this.loadGroups();
    this.fetchTasks();
  },
  methods: {
    async loadGroups() {
      this.groups = await fetchGroups(); // Используем импортированную функцию
    },

    async fetchTasks() {
      try {
        const tokenData = JSON.parse(localStorage.getItem("tokenData"));
        if (!tokenData || !tokenData.accessToken) {
          throw new Error("Пользователь не авторизован");
        }

        const response = await fetch('http://localhost:8080/api/v1/tasks', {
          headers: {
            Authorization: `Bearer ${tokenData.accessToken}`,
          },
        });

        if (!response.ok) {
          const error = await response.json();
          throw new Error(`Не удалось загрузить задания: ${error.message}`);
        }

        const data = await response.json();
        this.tasks = data.content || [];
        console.log("Задания получены:", this.tasks);  // Добавлено логирование для отладки
      } catch (error) {
        console.error("Ошибка при получении заданий:", error.message);
      }
    },

    addVariant() {
      this.variants.push({
        name: `Вариант ${this.variants.length + 1}`,
        tasks: [],
      });
    },
    updateVariant(index, updatedVariant) {
      this.variants.splice(index, 1, updatedVariant);
    },
    removeVariant(index) {
      this.variants.splice(index, 1);
    },
    async saveControlWork() {
      try {
        const tokenData = JSON.parse(localStorage.getItem("tokenData"));
        if (!tokenData || !tokenData.accessToken) {
          throw new Error("Пользователь не авторизован");
        }

        const payload = {
          ...this.controlWork,
          variants: this.variants
        };

        const response = await fetch('http://localhost:8080/api/v1/contests', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${tokenData.accessToken}`
          },
          body: JSON.stringify(payload)
        });

        if (!response.ok) {
          const error = await response.json();
          throw new Error(`Ошибка при сохранении контрольной: ${error.message}`);
        }

        const result = await response.json();
        console.log("Контрольная успешно сохранена:", result);
        alert("Контрольная успешно создана!");
        this.$router.push('/manage-contests'); // если есть роут на список КР
      } catch (error) {
        console.error("Ошибка при сохранении контрольной:", error.message);
        alert(`Ошибка: ${error.message}`);
      }
    }
  },
};
</script>

<style scoped>
.create-cr-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
}

.title {
  font-size: 30px;
  margin-bottom: 28px;
  font-weight: 700;
  text-align: center;
}

.subtitle {
  font-size: 22px;
  margin: 36px 0 20px;
  font-weight: 600;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 6px;
  font-weight: 500;
  color: #444;
}

input,
select,
textarea {
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px solid #d1d5db;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

input:focus,
select:focus,
textarea:focus {
  border-color: #2563eb;
  outline: none;
}

.form-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.variant-block {
  background: #f0f4f8;
  border-radius: 16px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.variant-block:hover {
  background-color: #e7f0fb;
}

.btn-group {
  display: flex;
  gap: 16px;
  margin-top: 30px;
  justify-content: center;
}

.btn {
  padding: 12px 20px;
  font-size: 16px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-primary {
  background-color: #34d399;
  color: white;
}

.btn-primary:hover {
  background-color: #10b981;
}

.btn-secondary {
  background-color: #60a5fa;
  color: white;
}

.btn-secondary:hover {
  background-color: #3b82f6;
}
</style>
