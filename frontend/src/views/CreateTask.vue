<template>
  <div class="page">
    <h1>Создание задания</h1>

    <div class="card">
      <div class="form-group">
        <label>Название задания <span class="required">*</span></label>
        <input v-model="taskName" type="text" placeholder="Введите название" />
      </div>

      <div class="form-group">
        <label>Описание <span class="required">*</span></label>
        <textarea v-model="taskDescription" placeholder="Введите описание"></textarea>
      </div>

      <div class="testcase-section">
        <h2>Тест-кейсы</h2>

        <transition-group name="fade" tag="div">
          <div
            v-for="(testcase, index) in testCases"
            :key="index"
            class="testcase"
          >
            <div class="form-group">
              <label>Ввод <span class="required">*</span></label>
              <textarea
                v-model="testcase.input"
                placeholder="Ввод программы"
              ></textarea>
            </div>
            <div class="form-group">
              <label>Ожидаемый вывод <span class="required">*</span></label>
              <textarea
                v-model="testcase.output"
                placeholder="Ожидаемый результат"
              ></textarea>
            </div>
            <button
              class="btn-remove"
              @click="removeTestCase(index)"
              title="Удалить тест-кейс 🗑"
            >
              🗑 Удалить
            </button>
          </div>
        </transition-group>

        <button
          @click="addTestCase"
          class="btn-add"
          title="Добавить тест-кейс ➕"
        >
          ➕ Добавить тест-кейс
        </button>
      </div>

      <button
        @click="saveTask"
        class="btn-save"
        title="Сохранить задание 💾"
      >
        💾 Сохранить задание
      </button>
    </div>
  </div>
</template>

<script>
import { getAccessToken, getRefreshToken } from '@/js/auth'

export default {
  data() {
    return {
      taskName: '',
      taskDescription: '',
      testCases: [{ input: '', output: '' }],
    };
  },
  methods: {
    addTestCase() {
      this.testCases.push({ input: '', output: '' });
    },
    removeTestCase(index) {
      this.testCases.splice(index, 1);
    },
    async saveTask() {
      if (!this.taskName.trim() || !this.taskDescription.trim()) {
        alert('Пожалуйста, заполните все обязательные поля.');
        return;
      }

      for (const testCase of this.testCases) {
        if (!testCase.input.trim() || !testCase.output.trim()) {
          alert('Все тест-кейсы должны быть заполнены.');
          return;
        }
      }

      console.log("tokenData из localStorage:", localStorage.getItem("tokenData"));

      const data = {
        name: this.taskName.trim(),
        description: this.taskDescription.trim(),
        testCases: this.testCases.map(tc => ({
          input: tc.input.trim(),
          output: tc.output.trim(),
        })),
      };

      let accessToken = getAccessToken();
      const refreshToken = getRefreshToken();

      console.log("AccessToken перед отправкой:", accessToken);
      console.log("Тело запроса:", data);

      const makeRequest = async (token) => {
        return await fetch('http://localhost:8080/api/v1/tasks', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
          },
          body: JSON.stringify(data),
        });
      };

      try {
        let response = await makeRequest(accessToken);

        if (response.status === 401 && refreshToken) {
          const refreshResponse = await fetch('http://localhost:8080/api/v1/auth/refresh', {
            method: 'POST',
            headers: {
              'Content-Type': 'application/json',
            },
            body: JSON.stringify({ refreshToken }),
          });

          if (!refreshResponse.ok) throw new Error('Не удалось обновить токен');

          const tokens = await refreshResponse.json();
          accessToken = tokens.accessToken;
          localStorage.setItem('tokenData', JSON.stringify(tokens));

          response = await makeRequest(accessToken);
        }

        if (!response.ok) throw new Error('Ошибка при создании задания');

        alert('Задание успешно создано!');
        this.taskName = '';
        this.taskDescription = '';
        this.testCases = [{ input: '', output: '' }];
      } catch (error) {
        console.error(error);
        alert('Произошла ошибка при отправке задания.');
      }
    },
  },
};
</script>

<style scoped>
.page {
  max-width: 800px;
  margin: auto;
  font-family: Arial, sans-serif;
  padding: 1rem;
  box-sizing: border-box;
}

h1 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
}

.card {
  background: #fff;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
  overflow: hidden;
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

.testcase-section {
  margin-top: 2rem;
}

.testcase {
  background: #f5faff;
  padding: 1rem;
  margin-bottom: 1rem;
  border-left: 4px solid #007bff;
  border-radius: 8px;
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
  background-color: #007bff;
  color: white;
}

.btn-remove {
  background-color: #dc3545;
  color: white;
  margin-top: 0.5rem;
}

.btn-save {
  background-color: #2ecc71;
  color: white;
  margin-top: 2rem;
  width: 100%;
}

.btn-add:hover {
  background-color: #0056b3;
}

.btn-save:hover {
  background-color: #218838;
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
</style>
