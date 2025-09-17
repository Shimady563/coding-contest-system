<template>
  <div class="info-form">
    <div class="form-group">
      <label>Название:</label>
      <input v-model="form.name" type="text" placeholder="Название контрольной" />
    </div>

    <div class="form-group">
      <label>Описание:</label>
      <textarea v-model="form.description" placeholder="Описание"></textarea>
    </div>

    <div class="form-group">
      <label>Группа:</label>
      <select v-model="form.group">
        <option disabled value="">Выберите группу</option>
        <option v-for="group in groups" :key="group.id" :value="group.id">{{ group.name }}</option>
      </select>
    </div>

    <div class="form-group">
      <label>Начало:</label>
      <input v-model="form.startTime" type="datetime-local" />
    </div>

    <div class="form-group">
      <label>Конец:</label>
      <input v-model="form.endTime" type="datetime-local" />
    </div>
  </div>
</template>

<script>
import { MANAGER_URL } from "@/js/api";

export default {
  props: ['modelValue'],
  data() {
    return {
      groups: [],
    };
  },
  computed: {
    form: {
      get() {
        return this.modelValue;
      },
      set(val) {
        this.$emit('update:modelValue', val);
      },
    },
  },
  mounted() {
    this.fetchGroups();
  },
  methods: {
    async fetchGroups() {
      try {
        const response = await fetch(`${MANAGER_URL}/groups`);
        if (!response.ok) {
          throw new Error('Не удалось загрузить список групп');
        }
        const data = await response.json();
        this.groups = data;  
      } catch (error) {
        console.error(error);
      }
    },
  },
};
</script>

<style scoped>
.info-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 30px;
}
.form-group {
  display: flex;
  flex-direction: column;
}
input, textarea, select {
  padding: 8px;
  border-radius: 6px;
  border: 1px solid #ccc;
}
</style>
