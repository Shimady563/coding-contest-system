<template>
  <div class="page">
    <div class="card">
      <h1>Редактирование контрольной работы</h1>

      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <span>Загрузка данных...</span>
      </div>
      <div v-else>
        <form @submit.prevent="updateContest">
          <div class="form-group">
            <label for="name">Название <span class="required">*</span></label>
            <input 
              v-model="contest.name" 
              id="name" 
              type="text" 
              placeholder="Введите название"
              :class="{ 'invalid': !contest.name && submitted }"
              required 
            />
            <span v-if="!contest.name && submitted" class="error-message">Это поле обязательно</span>
          </div>

          <div class="form-group">
            <label for="description">Описание <span class="required">*</span></label>
            <textarea 
              v-model="contest.description" 
              id="description"
              placeholder="Краткое описание контрольной"
              :class="{ 'invalid': !contest.description && submitted }"
            ></textarea>
            <span v-if="!contest.description && submitted" class="error-message">Это поле обязательно</span>
          </div>

          <div class="form-group">
            <label for="group">Группа <span class="required">*</span></label>
            <select 
              id="group" 
              v-model="contest.groupId"
              :class="{ 'invalid': !contest.groupId && submitted }"
            >
              <option disabled value="">-- Выберите группу --</option>
              <option v-for="group in groups" :key="group.id" :value="group.id">
                {{ group.name }}
              </option>
            </select>
            <span v-if="!contest.groupId && submitted" class="error-message">Выберите группу</span>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="start">Начало <span class="required">*</span></label>
              <input 
                v-model="contest.startTime" 
                id="start" 
                type="datetime-local"
                :class="{ 'invalid': !contest.startTime && submitted }"
              />
              <span v-if="!contest.startTime && submitted" class="error-message">Выберите время начала</span>
            </div>

            <div class="form-group">
              <label for="end">Окончание <span class="required">*</span></label>
              <input 
                v-model="contest.endTime" 
                id="end" 
                type="datetime-local"
                :class="{ 'invalid': !contest.endTime && submitted }"
              />
              <span v-if="!contest.endTime && submitted" class="error-message">Выберите время окончания</span>
            </div>
          </div>

          <button 
            type="submit" 
            class="btn-save"
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
import { MANAGER_URL, fetchGroups } from "@/js/auth";

export default {
  data() {
    return {
      contest: {
        id: null,
        name: '',
        description: '',
        startTime: '',
        endTime: '',
        groupId: null
      },
      groups: [],
      loading: true,
      submitted: false
    };
  },
  async mounted() {
    await this.loadGroups();
    await this.fetchContest();
  },
  methods: {
    async loadGroups() {
      try {
        this.groups = await fetchGroups();
      } catch (error) {
        console.error('Error loading groups:', error);
        this.$root.notify('Ошибка при загрузке групп', 'error');
      }
    },
    async fetchContest() {
      const id = this.$route.params.id;
      this.loading = true;

      try {
        const response = await fetch(`${MANAGER_URL}/contests/${id}`, {
          credentials: "include"
        });

        if (!response.ok) {
          throw new Error('Ошибка при загрузке контрольной');
        }

        const data = await response.json();
        this.contest = {
          id: data.id,
          name: data.name || '',
          description: data.description || '',
          startTime: data.startTime ? this.formatDateTimeForInput(data.startTime) : '',
          endTime: data.endTime ? this.formatDateTimeForInput(data.endTime) : '',
          groupId: data.groupId || null
        };
      } catch (error) {
        console.error('Error fetching contest:', error);
        this.$root.notify('Не удалось загрузить данные контрольной', 'error');
        this.$router.push('/manage-contests');
      } finally {
        this.loading = false;
      }
    },
    formatDateTimeForInput(dateTimeString) {
      // Convert ISO string to datetime-local input format
      const date = new Date(dateTimeString);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      return `${year}-${month}-${day}T${hours}:${minutes}`;
    },
    validateForm() {
      this.submitted = true;
      
      if (!this.contest.name.trim()) {
        this.$root.notify('Введите название контрольной', 'error');
        return false;
      }
      
      if (!this.contest.description.trim()) {
        this.$root.notify('Введите описание контрольной', 'error');
        return false;
      }
      
      if (!this.contest.groupId) {
        this.$root.notify('Выберите группу', 'error');
        return false;
      }
      
      if (!this.contest.startTime) {
        this.$root.notify('Выберите время начала', 'error');
        return false;
      }
      
      if (!this.contest.endTime) {
        this.$root.notify('Выберите время окончания', 'error');
        return false;
      }

      const startTime = new Date(this.contest.startTime);
      const endTime = new Date(this.contest.endTime);
      
      if (startTime >= endTime) {
        this.$root.notify('Время окончания должно быть позже времени начала', 'error');
        return false;
      }
      
      return true;
    },
    async updateContest() {
      if (!this.validateForm()) return;
      
      this.loading = true;

      try {
        const contestPayload = {
          name: this.contest.name.trim(),
          description: this.contest.description.trim(),
          groupId: Number(this.contest.groupId),
          startTime: this.contest.startTime,
          endTime: this.contest.endTime,
        };

        const response = await fetch(`${MANAGER_URL}/contests/${this.contest.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          credentials: 'include',
          body: JSON.stringify(contestPayload)
        });

        if (!response.ok) {
          const errorResponse = await response.json().catch(() => ({}));
          throw new Error(errorResponse.message || 
                        errorResponse.error || 
                        `HTTP error ${response.status}`);
        }

        this.$root.notify('Контрольная успешно обновлена!', 'success');
        this.$router.push('/manage-contests');
      } catch (error) {
        console.error('Error updating contest:', error);
        this.$root.notify('Не удалось сохранить изменения', 'error');
      } finally {
        this.loading = false;
      }
    },
  },
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
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
textarea,
select {
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

.btn-save {
  margin-top: 2rem;
  padding: 12px 24px;
  background-color: #34d399;
  color: white;
  font-size: 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  width: 100%;
}

.btn-save:disabled {
  background-color: #a7f3d0;
  cursor: not-allowed;
}

.btn-save:hover:not(:disabled) {
  background-color: #10b981;
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

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .card {
    padding: 1rem;
  }
}
</style>
