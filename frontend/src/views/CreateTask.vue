<template>
  <div class="page">
    <h1>Создание задания</h1>

    <div class="card">
      <div class="form-group">
        <label>Название задания</label>
        <input v-model="taskName" type="text" placeholder="Введите название" />
      </div>

      <div class="form-group">
        <label>Описание</label>
        <textarea v-model="taskDescription" placeholder="Введите описание"></textarea>
      </div>

      <div class="testcase-section">
        <h2>Тест-кейсы</h2>

        <div
          v-for="(testcase, index) in testCases"
          :key="index"
          class="testcase"
        >
          <div class="form-group">
            <label>Ввод</label>
            <textarea v-model="testcase.input" placeholder="Ввод программы" />
          </div>
          <div class="form-group">
            <label>Ожидаемый вывод</label>
            <textarea
              v-model="testcase.output"
              placeholder="Ожидаемый результат"
            />
          </div>
          <button class="btn-remove" @click="removeTestCase(index)">Удалить</button>
        </div>

        <button @click="addTestCase" class="btn-add">Добавить тест-кейс</button>
      </div>

      <button @click="saveTask" class="btn-save">Сохранить задание</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      taskName: '',
      taskDescription: '',
      testCases: [],
    };
  },
  methods: {
    addTestCase() {
      this.testCases.push({ input: '', output: '' });
    },
    removeTestCase(index) {
      this.testCases.splice(index, 1);
    },
    saveTask() {
      const data = {
        name: this.taskName,
        description: this.taskDescription,
        testCases: this.testCases,
      };
      console.log('Сохранение задания:', data);
      alert('Задание сохранено!');
      // TODO: запрос на сервер
    },
  },
};
</script>

<style scoped>
.page {
  max-width: 800px;
  margin: auto;
  padding: 2rem;
}

.card {
  background: #fff;
  padding: 1.5rem;
  border-radius: 10px;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.05);
}

.form-group {
  margin-bottom: 1rem;
  display: flex;
  flex-direction: column;
}

input,
textarea {
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 6px;
}

.testcase-section {
  margin-top: 2rem;
}

.testcase {
  background: #f9f9f9;
  padding: 1rem;
  margin-bottom: 1rem;
  border-left: 3px solid #007bff;
  border-radius: 6px;
}

.btn-add,
.btn-save,
.btn-remove {
  margin-top: 1rem;
  padding: 10px 15px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
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
  background-color: #28a745;
  color: white;
  margin-top: 1.5rem;
}
</style>
