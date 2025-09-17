<template>
  <div class="page">
    <div class="card">
      <h1>{{ isEdit ? 'Редактирование задания' : 'Создание задания' }}</h1>

      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <span>Загрузка данных...</span>
      </div>
      <div v-else>
        <div class="form-group">
          <label>Название задания <span class="required">*</span></label>
          <input v-model="task.name" type="text" placeholder="Введите название" :class="{ 'invalid': !task.name && submitted }" />
          <span v-if="!task.name && submitted" class="error-message">Это поле обязательно</span>
        </div>

        <div class="form-group">
          <label>Описание <span class="required">*</span></label>
          <textarea v-model="task.description" placeholder="Введите описание" :class="{ 'invalid': !task.description && submitted }"></textarea>
          <span v-if="!task.description && submitted" class="error-message">Это поле обязательно</span>
        </div>

        <div class="testcase-section">
          <h2>Тест-кейсы <span class="required">*</span></h2>
          <span v-if="task.testCases.length === 0 && submitted" class="error-message">Добавьте хотя бы один тест-кейс</span>

          <transition-group name="fade" tag="div">
            <div v-for="(testCase, index) in task.testCases" :key="index" class="testcase" :class="{ 'invalid': (!testCase.input || !testCase.output) && submitted }">
              <div class="form-group">
                <label>Ввод <span class="required">*</span></label>
                <textarea v-model="testCase.input" placeholder="Ввод программы" :class="{ 'invalid': !testCase.input && submitted }"></textarea>
                <span v-if="!testCase.input && submitted" class="error-message">Заполните поле ввода</span>
              </div>
              <div class="form-group">
                <label>Ожидаемый вывод <span class="required">*</span></label>
                <textarea v-model="testCase.output" placeholder="Ожидаемый результат" :class="{ 'invalid': !testCase.output && submitted }"></textarea>
                <span v-if="!testCase.output && submitted" class="error-message">Заполните поле вывода</span>
              </div>
              <button class="btn btn-danger" @click="removeTestCase(index)" type="button">🗑 Удалить</button>
            </div>
          </transition-group>

          <button @click="addTestCase" class="btn btn-secondary" type="button">➕ Добавить тест-кейс</button>
        </div>

        <button @click="onSubmit" class="btn btn-primary" :disabled="saving">
          <span v-if="saving">⏳ Сохранение...</span>
          <span v-else>{{ isEdit ? '💾 Сохранить изменения' : '💾 Сохранить задание' }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { getTask, createTask, updateTask } from '@/js/api';

export default {
  props: { id: { type: String, required: false } },
  data() {
    return {
      task: { name: '', description: '', testCases: [{ input: '', output: '' }] },
      submitted: false,
      loading: false,
      saving: false,
    };
  },
  computed: {
    isEdit() { return !!this.id; }
  },
  async mounted() {
    if (this.isEdit) {
      this.loading = true;
      try {
        const data = await getTask(this.id);
        this.task = {
          name: data.name || '',
          description: data.description || '',
          testCases: (data.testCases && data.testCases.length ? data.testCases : [{ input: '', output: '' }]).map(tc => ({ input: tc.input || '', output: tc.output || '' }))
        };
      } catch (e) {
        this.$root.notify('Не удалось загрузить данные задания', 'error');
        this.$router.push('/manage-contests');
      } finally {
        this.loading = false;
      }
    }
  },
  methods: {
    addTestCase() { this.task.testCases.push({ input: '', output: '' }); },
    removeTestCase(index) { this.task.testCases.splice(index, 1); },
    validate() {
      this.submitted = true;
      if (!this.task.name.trim()) { this.$root.notify('Введите название задания', 'error'); return false; }
      if (!this.task.description.trim()) { this.$root.notify('Введите описание задания', 'error'); return false; }
      if (this.task.testCases.length === 0) { this.$root.notify('Добавьте хотя бы один тест-кейс', 'error'); return false; }
      for (const [i, tc] of this.task.testCases.entries()) {
        if (!tc.input.trim() || !tc.output.trim()) { this.$root.notify(`Заполните все поля тест-кейса #${i + 1}`, 'error'); return false; }
      }
      return true;
    },
    async onSubmit() {
      if (!this.validate()) return;
      this.saving = true;
      try {
        const payload = {
          name: this.task.name.trim(),
          description: this.task.description.trim(),
          testCases: this.task.testCases.map(tc => ({ input: tc.input.trim(), output: tc.output.trim() })),
        };
        if (this.isEdit) {
          await updateTask(this.id, payload);
          this.$root.notify('Задание успешно обновлено!', 'success');
          this.$router.push('/manage-contests');
        } else {
          await createTask(payload);
          this.$root.notify('Задание успешно создано!', 'success');
          this.task = { name: '', description: '', testCases: [{ input: '', output: '' }] };
          this.submitted = false;
        }
      } catch (e) {
        this.$root.notify('Произошла ошибка при сохранении задания', 'error');
      } finally {
        this.saving = false;
      }
    }
  }
};
</script>

<style scoped>
/* Minimal component-specific overrides as needed */
</style>
