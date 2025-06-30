<template>
  <div class="edit-container">
    <h1>Редактирование контрольной работы</h1>

    <div v-if="loading">Загрузка...</div>
    <div v-else>
      <form @submit.prevent="updateTest">
        <label for="name">Название</label>
        <input v-model="test.name" id="name" type="text" required />

        <label for="description">Описание</label>
        <textarea v-model="test.description" id="description"></textarea>

        <label for="start">Начало</label>
        <input v-model="test.startTime" id="start" type="datetime-local" />

        <label for="end">Окончание</label>
        <input v-model="test.endTime" id="end" type="datetime-local" />

        <label for="group">ID группы</label>
        <input v-model="test.groupId" id="group" type="number" />

        <button type="submit">Сохранить</button>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      test: {
        id: null,
        name: '',
        description: '',
        startTime: '',
        endTime: '',
        groupId: null
      },
      loading: true
    };
  },
  mounted() {
    const stored = localStorage.getItem('selectedTest');
    if (stored) {
      this.test = JSON.parse(stored);
      this.loading = false;
    } else {
      this.fetchTest(); 
    }
  },
  methods: {
    async fetchTest() {
      const id = this.$route.params.id;
      const tokenData = JSON.parse(localStorage.getItem("tokenData"));

      try {
        const response = await fetch(`http://localhost:8080/api/v1/contests?id=${id}`, {
          headers: {
            Authorization: `Bearer ${tokenData?.accessToken}`,
          },
        });

        if (!response.ok) throw new Error('Ошибка при загрузке контрольной');

        const data = await response.json();
        this.test = data;
        this.loading = false;
      } catch (error) {
        console.error(error.message);
        alert("Не удалось загрузить данные контрольной");
      }
    },
    async updateTest() {
      const tokenData = JSON.parse(localStorage.getItem("tokenData"));

      try {
        const response = await fetch(`http://localhost:8080/api/v1/contests?id=${this.test.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${tokenData?.accessToken}`,
          },
          body: JSON.stringify(this.test),
        });

        if (!response.ok) throw new Error('Ошибка при сохранении');

        alert('Контрольная успешно обновлена!');
        this.$router.push('/manage-tests');
      } catch (error) {
        console.error(error.message);
        alert('Не удалось сохранить изменения');
      }
    },
  },
};
</script>

<style scoped>
.edit-container {
  max-width: 600px;
  margin: 40px auto;
  background: #fff;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

h1 {
  margin-bottom: 20px;
  font-size: 24px;
  color: #333;
}

label {
  display: block;
  margin-top: 20px;
  margin-bottom: 8px;
  font-weight: 500;
}

input,
textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 16px;
  box-sizing: border-box;
}

button {
  margin-top: 30px;
  padding: 12px 24px;
  background-color: #2f80ed;
  color: white;
  font-size: 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

button:hover {
  background-color: #1366d6;
}
</style>
