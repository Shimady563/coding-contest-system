<template>
    <div class="create-test-container">
      <h1>Создание контрольной</h1>
  
      <!-- Название контрольной -->
      <div class="form-group">
        <label>Название контрольной:</label>
        <input v-model="testName" type="text" placeholder="Введите название" />
      </div>
  
      <!-- Текст контрольной -->
      <div class="form-group">
        <label>Текст контрольной:</label>
        <textarea v-model="testDescription" placeholder="Введите текст"></textarea>
      </div>
  
      <!-- Блок добавления тест-кейсов -->
      <h2>Тест-кейсы</h2>
      <div class="test-case-form">
        <input v-model="newTestCase.input" type="text" placeholder="Входные данные" />
        <input v-model="newTestCase.expectedOutput" type="text" placeholder="Ожидаемый результат" />
        <button @click="addTestCase">Добавить тест-кейс</button>
      </div>
  
      <!-- Список тест-кейсов -->
      <ul class="test-cases">
        <li v-for="(testCase, index) in testCases" :key="index">
          <span><strong>Вход:</strong> {{ testCase.input }} | <strong>Выход:</strong> {{ testCase.expectedOutput }}</span>
          <button @click="removeTestCase(index)">🗑️</button>
        </li>
      </ul>
  
      <!-- Кнопка сохранения -->
      <button class="save-btn" @click="saveTest">Сохранить контрольную</button>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        testName: "", // Название контрольной
        testDescription: "", // Текст контрольной
        newTestCase: { input: "", expectedOutput: "" }, // Новый тест-кейс
        testCases: [], // Список тест-кейсов
      };
    },
    methods: {
      addTestCase() {
        if (this.newTestCase.input.trim() && this.newTestCase.expectedOutput.trim()) {
          this.testCases.push({ ...this.newTestCase }); // Добавляем копию объекта
          this.newTestCase.input = "";
          this.newTestCase.expectedOutput = "";
        }
      },
      removeTestCase(index) {
        this.testCases.splice(index, 1); // Удаляем тест-кейс
      },
      saveTest() {
        if (!this.testName.trim() || !this.testDescription.trim() || this.testCases.length === 0) {
          alert("Заполните все поля перед сохранением!");
          return;
        }
        
        const newTest = {
          name: this.testName,
          description: this.testDescription,
          cases: this.testCases,
        };
  
        console.log("Сохранение контрольной:", newTest);
        alert("Контрольная успешно сохранена!");
        
        // Очистка формы после сохранения
        this.testName = "";
        this.testDescription = "";
        this.testCases = [];
      },
    },
  };
  </script>
  
  <style scoped>
  .create-test-container {
    max-width: 600px;
    margin: 20px auto;
    text-align: center;
  }
  
  .form-group {
    display: flex;
    flex-direction: column;
    margin-bottom: 15px;
  }
  
  input, textarea {
    padding: 8px;
    border: 1px solid #ccc;
    border-radius: 5px;
    font-size: 16px;
  }
  
  textarea {
    min-height: 80px;
  }
  
  .test-case-form {
    display: flex;
    gap: 10px;
    margin-bottom: 15px;
  }
  
  .test-case-form input {
    flex: 1;
  }
  
  button {
    background: #28a745;
    color: white;
    border: none;
    padding: 8px 12px;
    cursor: pointer;
    border-radius: 5px;
  }
  
  button:hover {
    background: #218838;
  }
  
  .test-cases {
    list-style: none;
    padding: 0;
  }
  
  .test-cases li {
    display: flex;
    justify-content: space-between;
    background: #f8f9fa;
    padding: 8px;
    margin: 5px 0;
    border-radius: 5px;
  }
  
  .save-btn {
    background: #007bff;
    margin-top: 20px;
  }
  </style>
  