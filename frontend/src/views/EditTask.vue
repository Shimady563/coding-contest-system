<template>
  <div class="page">
    <div class="card">
      <h1>Редактирование задания</h1>

      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <span>Загрузка данных...</span>
      </div>
      <div v-else>
        <form @submit.prevent="updateTask">
          <div class="form-group">
            <label>Название задания <span class="required">*</span></label>
            <input 
              v-model="task.name" 
              type="text" 
              placeholder="Введите название"
              :class="{ 'invalid': !task.name && submitted }"
            />
            <span v-if="!task.name && submitted" class="error-message">Это поле обязательно</span>
          </div>

          <div class="form-group">
            <label>Описание <span class="required">*</span></label>
            <textarea 
              v-model="task.description" 
              placeholder="Введите описание"
              :class="{ 'invalid': !task.description && submitted }"
            ></textarea>
            <span v-if="!task.description && submitted" class="error-message">Это поле обязательно</span>
          </div>

          <div class="testcase-section">
            <h2>Тест-кейсы <span class="required">*</span></h2>
            <span v-if="task.testCases.length === 0 && submitted" class="error-message">Добавьте хотя бы один тест-кейс</span>

            <transition-group name="fade" tag="div">
              <div
                v-for="(testCase, index) in task.testCases"
                :key="index"
                class="testcase"
                :class="{ 'invalid': (!testCase.input || !testCase.output) && submitted }"
              >
                <div class="form-group">
                  <label>Ввод <span class="required">*</span></label>
                  <textarea
                    v-model="testCase.input"
                    placeholder="Ввод программы"
                    :class="{ 'invalid': !testCase.input && submitted }"
                  ></textarea>
                  <span v-if="!testCase.input && submitted" class="error-message">Заполните поле ввода</span>
                </div>
                <div class="form-group">
                  <label>Ожидаемый вывод <span class="required">*</span></label>
                  <textarea
                    v-model="testCase.output"
                    placeholder="Ожидаемый результат"
                    :class="{ 'invalid': !testCase.output && submitted }"
                  ></textarea>
                  <span v-if="!testCase.output && submitted" class="error-message">Заполните поле вывода</span>
                </div>
                <button
                  class="btn-remove"
                  @click="removeTestCase(index)"
                  title="Удалить тест-кейс 🗑"
                  type="button"
                >
                  🗑 Удалить
                </button>
              </div>
            </transition-group>

            <button
              @click="addTestCase"
              class="btn-add"
              title="Добавить тест-кейс ➕"
              type="button"
            >
              ➕ Добавить тест-кейс
            </button>
          </div>

          <button
            @click="updateTask"
            class="btn-save"
            title="Сохранить задание 💾"
            :disabled="loading"
          >
            <span v-if="loading">⏳ Сохранение...</span>
            <span v-else>💾 Сохранить изменения</span>
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { MANAGER_URL } from "@/js/auth";

export default {
  data() {
    return {
      task: {
        id: null,
        name: '',
        description: '',
        testCases: []
      },
      loading: true,
      submitted: false
    };
  },
  async mounted() {
    await this.fetchTask();
  },
  methods: {
    async fetchTask() {
      const id = this.$route.params.id;
      this.loading = true;

      try {
        const response = await fetch(`${MANAGER_URL}/tasks/${id}`, {
          credentials: "include"
        });

        if (!response.ok) {
          throw new Error('Ошибка при загрузке задания');
        }

        const data = await response.json();
        this.task = {
          id: data.id,
          name: data.name || '',
          description: data.description || '',
          testCases: data.testCases && data.testCases.length > 0 
            ? data.testCases.map(tc => ({
                input: tc.input || '',
                output: tc.output || ''
              }))
            : [{ input: '', output: '' }]
        };
      } catch (error) {
        console.error('Error fetching task:', error);
        this.$root.notify('Не удалось загрузить данные задания', 'error');
        this.$router.push('/manage-contests');
      } finally {
        this.loading = false;
      }
    },
    addTestCase() {
      this.task.testCases.push({ input: '', output: '' });
    },
    removeTestCase(index) {
      this.task.testCases.splice(index, 1);
    },
    validateForm() {
      this.submitted = true;
      
      if (!this.task.name.trim()) {
        this.$root.notify('Введите название задания', 'error');
        return false;
      }
      
      if (!this.task.description.trim()) {
        this.$root.notify('Введите описание задания', 'error');
        return false;
      }
      
      if (this.task.testCases.length === 0) {
        this.$root.notify('Добавьте хотя бы один тест-кейс', 'error');
        return false;
      }
      
      for (const [index, testCase] of this.task.testCases.entries()) {
        if (!testCase.input.trim() || !testCase.output.trim()) {
          this.$root.notify(`Заполните все поля тест-кейса #${index + 1}`, 'error');
          return false;
        }
      }
      
      return true;
    },
    async updateTask() {
      if (!this.validateForm()) return;
      
      this.loading = true;

      try {
        const data = {
          name: this.task.name.trim(),
          description: this.task.description.trim(),
          testCases: this.task.testCases.map(tc => ({
            input: tc.input.trim(),
            output: tc.output.trim(),
          })),
        };

        const response = await fetch(`${MANAGER_URL}/tasks/${this.task.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include',
          body: JSON.stringify(data),
        });

        if (!response.ok) {
          const errorData = await response.json().catch(() => ({}));
          throw new Error(errorData.message || 'Ошибка при обновлении задания');
        }

        this.$root.notify('Задание успешно обновлено!', 'success');
        this.$router.push('/manage-contests');
      } catch (error) {
        console.error('Error updating task:', error);
        this.$root.notify('Произошла ошибка при сохранении задания', 'error');
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.page {
  max-width: 800px;
  margin: auto;
  padding: 1rem;
  box-sizing: border-box;
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
}

.card {
  max-width: 900px;
  background: #fff;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
  overflow: hidden;
  animation: fadeIn 0.4s ease-in-out;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.form-group {
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
  width: 100%;
  box-sizing: border-box;
}

label {
  font-weight: bold;
  margin-bottom: 0.3rem;
}

.required {
  color: red;
  font-size: 1rem;
}

input,
textarea {
  padding: 10px;
  font-size: 14px;
  border: 1px solid #ccc;
  border-radius: 6px;
  resize: vertical;
  width: 100%;
  box-sizing: border-box;
}

.invalid {
  border-color: #dc3545;
}

.error-message {
  color: #dc3545;
  font-size: 14px;
  margin-top: 5px;
}

.testcase-section {
  margin-top: 2rem;
}

.testcase {
  background: #f5faff;
  padding: 1rem;
  margin-bottom: 1rem;
  border-left: 4px solid #60a5fa;
  border-radius: 8px;
}

.testcase.invalid {
  border-left-color: #dc3545;
}

.btn-add,
.btn-save,
.btn-remove {
  margin-top: 1rem;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.3s ease, transform 0.2s ease;
  box-sizing: border-box;
}

.btn-add {
  background-color: #60a5fa;
  color: white;
}

.btn-remove {
  background-color: #dc3545;
  color: white;
  margin-top: 0.5rem;
}

.btn-save {
  background-color: #34d399;
  color: white;
  margin-top: 2rem;
  width: 100%;
}

.btn-save:disabled {
  background-color: #a7f3d0;
  cursor: not-allowed;
}

.btn-add:hover {
  background-color: #3b82f6;
}

.btn-save:hover:not(:disabled) {
  background-color: #10b981;
}

.btn-remove:hover {
  background-color: #c82333;
}

.btn-add:active,
.btn-save:active,
.btn-remove:active {
  transform: scale(0.97);
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.4s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>

