<template>
  <div class="create-cr-container">
    <h1 class="title">Создание контрольной</h1>

    <div class="form-group">
      <label for="name">Название:</label>
      <input id="name" v-model="controlWork.name" type="text" />
    </div>

    <div class="form-group">
      <label for="description">Описание:</label>
      <textarea id="description" v-model="controlWork.description" rows="3" />
    </div>

    <div class="form-group">
      <label for="group">Группа:</label>
      <select id="group" v-model="controlWork.group">
        <option disabled value="">-- Выберите группу --</option>
        <option v-for="group in groups" :key="group" :value="group">
          {{ group }}
        </option>
      </select>
    </div>

    <div class="form-row">
      <div class="form-group">
        <label for="start">Начало:</label>
        <input id="start" type="datetime-local" v-model="controlWork.startTime" />
      </div>
      <div class="form-group">
        <label for="end">Окончание:</label>
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
      groups: ['101', '102', '103'],
      tasks: [
        { id: '1', name: 'Сортировка', description: 'Реализуйте алгоритм сортировки пузырьком' },
        { id: '2', name: 'Фибоначчи', description: 'Вычислите n-е число Фибоначчи' },
        { id: '3', name: 'Поиск максимума', description: 'Найдите максимум в массиве' },
      ],
      variants: [],
    };
  },
  methods: {
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
    saveControlWork() {
      const payload = {
        ...this.controlWork,
        variants: this.variants,
      };
      console.log('Контрольная к отправке:', payload);
      alert('Контрольная сохранена!');
    },
  },
};
</script>

<style scoped>
.create-cr-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 28px;
  margin-bottom: 24px;
  font-weight: bold;
}

.subtitle {
  font-size: 22px;
  margin: 32px 0 16px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 6px;
  font-weight: 500;
}

input,
select,
textarea {
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  font-size: 16px;
}

.form-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.variant-block {
  padding: 20px;
  background: #f9fafb;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  margin-bottom: 20px;
}

.btn-group {
  display: flex;
  gap: 16px;
  margin-top: 24px;
}

.btn {
  padding: 10px 18px;
  font-size: 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.btn-primary {
  background-color: #28a745;
  color: white;
}

.btn-secondary {
  background-color: #007bff;
  color: white;
}
</style>
