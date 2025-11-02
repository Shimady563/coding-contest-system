<template>
  <div class="page">
    <div class="card">
      <h1>{{ isEdit ? 'Редактирование задания' : 'Создание задания' }}</h1>

      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <span>Загрузка данных...</span>
      </div>
      <div v-else>
        <FloatingInput
          id="name"
          name="name"
          label="Название"
          v-model="task.name"
          placeholder=""
          type="text"
          :error="!task.name && submitted">
          <span v-if="!task.name && submitted" class="error-message">Это поле обязательно</span>
        </FloatingInput>

        <FloatingInput
          id="description"
          name="description"
          label="Описание"
          v-model="task.description"
          placeholder=""
          type="textarea"
          :error="!task.description && submitted">
          <span v-if="!task.description && submitted" class="error-message">Это поле обязательно</span>
        </FloatingInput>

        <div class="testcase-section">
          <h2>Тест-кейсы</h2>
          <span v-if="task.testCases.length === 0 && submitted" class="error-message">Добавьте хотя бы один тест-кейс</span>

          <transition-group name="fade" tag="div">
            <div v-for="(testCase, index) in task.testCases" :key="index" class="testcase" :class="{ 'invalid': (!testCase.input || !testCase.output) && submitted }">
              <FloatingInput
                :id="`input-${index}`"
                :name="`input-${index}`"
                label="Ввод"
                v-model="testCase.input"
                type="textarea"
                placeholder=""
                class="input-testcase"
                :error="!testCase.input && submitted"
              >
                <span v-if="!testCase.input && submitted" class="error-message">Заполните поле ввода</span>
              </FloatingInput>
              <FloatingInput
                :id="`output-${index}`"
                :name="`output-${index}`"
                label="Ожидаемый вывод"
                v-model="testCase.output"
                type="textarea"
                placeholder=""
                :error="!testCase.output && submitted"
              >
                <span v-if="!testCase.output && submitted" class="error-message">Заполните поле вывода</span>
              </FloatingInput>
              <button class="btn btn-danger" @click="removeTestCase(index)" type="button">
                <i class="fas fa-trash"></i> 
                Удалить
              </button>
            </div>
          </transition-group>
        </div>

        <div class="form-actions">
          <button @click="addTestCase" class="btn btn-secondary" type="button">
            <i class="fas fa-plus"></i> 
            Добавить тест-кейс
          </button>
          <button @click="onSubmit" class="btn btn-primary" :disabled="saving">
            <span v-if="saving">
              <i class="fas fa-spinner fa-spin"></i>  
              Сохранение...
            </span>
            <span v-else>
              <i class="fas fa-save"></i>
              {{ isEdit ? ' Сохранить изменения' : ' Сохранить задание' }}
            </span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import FloatingInput from '@/components/FloatingInput.vue';
import { getTask, createTask, updateTask } from '@/js/manager';

export default {
  components:{
    FloatingInput,
  },
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
.card {
  overflow: visible !important;
  position: relative;
  background: #ffffff;
  padding: 1.5rem;
  border-radius: 16px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
}

.input-testcase{
  margin-top: 25px;
}

.error-message {
  margin-top: -25px;
  margin-bottom: 1.5rem;
}

.testcase-section {
  margin-top: 2rem;
}

.testcase {
  background: #ffffff; 
  border: 1px solid #e5e9f0;
  border-radius: 12px;
  padding: 1rem 1.25rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.04);
}

.testcase.invalid {
  border-color: #e74c3c;
  box-shadow: 0 0 0 2px rgba(231, 76, 60, 0.1);
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 2rem;
  gap: 1rem;
}

.btn {
  padding: 12px 18px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.btn-secondary {
  background: #2f80ed;
  color: #fff;
}

.btn-secondary:hover {
  background: #256bcc;
}

.btn-danger {
  margin-top: 10px ;
  background: #e74c3c;
  color: #fff;
}

.btn-danger:hover {
  background: #cf3b2c;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  padding: 2rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #2f80ed;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@media (max-width: 768px) {
  .card {
    padding: 1rem;
  }

  .floating-label label {
    font-size: 14px;
  }

  .form-actions {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}
</style>