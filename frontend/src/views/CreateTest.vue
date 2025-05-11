<template>
  <div class="page">
    <h1 class="title">Создание контрольной работы</h1>

    <div class="create-cr-container">
      <div class="form-group">
        <label for="name">Название <span class="required">*</span></label>
        <input 
          id="name" 
          v-model="controlWork.name" 
          type="text" 
          placeholder="Введите название" 
          :class="{ 'invalid': !controlWork.name && submitted }"
        />
        <span v-if="!controlWork.name && submitted" class="error-message">Это поле обязательно</span>
      </div>

      <div class="form-group">
        <label for="description">Описание <span class="required">*</span></label>
        <textarea 
          id="description" 
          v-model="controlWork.description" 
          rows="3" 
          placeholder="Краткое описание контрольной"
          :class="{ 'invalid': !controlWork.description && submitted }"
        />
        <span v-if="!controlWork.description && submitted" class="error-message">Это поле обязательно</span>
      </div>

      <div class="form-group">
        <label for="group">Группа <span class="required">*</span></label>
        <select 
          id="group" 
          v-model="controlWork.group"
          :class="{ 'invalid': !controlWork.group && submitted }"
        >
          <option disabled value="">-- Выберите группу --</option>
          <option v-for="group in groups" :key="group.id" :value="group.id">
            {{ group.name }}
          </option>
        </select>
        <span v-if="!controlWork.group && submitted" class="error-message">Выберите группу</span>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="start">Начало <span class="required">*</span></label>
          <input 
            id="start" 
            type="datetime-local" 
            v-model="controlWork.startTime"
            :class="{ 'invalid': !controlWork.startTime && submitted }"
          />
          <span v-if="!controlWork.startTime && submitted" class="error-message">Укажите дату начала</span>
        </div>
        <div class="form-group">
          <label for="end">Окончание <span class="required">*</span></label>
          <input 
            id="end" 
            type="datetime-local" 
            v-model="controlWork.endTime"
            :class="{ 'invalid': !controlWork.endTime && submitted }"
          />
          <span v-if="!controlWork.endTime && submitted" class="error-message">Укажите дату окончания</span>
        </div>
      </div>

      <h2 class="subtitle">Варианты <span class="required">*</span></h2>
      <span v-if="variants.length === 0 && submitted" class="error-message">Добавьте хотя бы один вариант</span>
      
      <div v-for="(variant, index) in variants" :key="index" class="variant-block">
        <VariantForm
          :variant="variant"
          :allTasks="tasks"
          @remove="removeVariant(index)"
          @update="(updated) => updateVariant(index, updated)"
          :submitted="submitted"
        />
      </div>

      <div class="btn-group">
        <button class="btn btn-secondary" @click="addVariant">➕ Добавить вариант</button>
        <button class="btn btn-primary" @click="saveControlWork">💾 Сохранить контрольную</button>
      </div>
    </div>
  </div>
</template>

<script>
import VariantForm from '../components/VariantForm.vue';
import { fetchGroups } from '../js/auth.js';

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
      groups: [],
      tasks: [],
      variants: [],
      submitted: false,
      loading: false
    };
  },
  mounted() {
    this.loadGroups();
    this.fetchTasks();
  },
  methods: {
    async loadGroups() {
      try {
        this.groups = await fetchGroups();
      } catch (error) {
        console.error("Ошибка при загрузке групп:", error);
         }
    },

    async fetchTasks() {
      try {
        const tokenData = JSON.parse(localStorage.getItem("tokenData"));
        if (!tokenData || !tokenData.accessToken) {
          throw new Error("Пользователь не авторизован");
        }

        const response = await fetch('http://localhost:8080/api/v1/tasks', {
          headers: {
            Authorization: `Bearer ${tokenData.accessToken}`,
          },
        });

        if (!response.ok) {
          const error = await response.json();
          throw new Error(`Не удалось загрузить задания: ${error.message}`);
        }

        const data = await response.json();
        this.tasks = data.content || [];
        } catch (error) {
        console.error("Ошибка при получении заданий:", error.message);
      }
    },

    validateForm() {
      this.submitted = true;
      
      const requiredFields = [
        { value: this.controlWork.name, message: 'Введите название контрольной работы' },
        { value: this.controlWork.description, message: 'Введите описание контрольной работы' },
        { value: this.controlWork.group, message: 'Выберите группу' },
        { value: this.controlWork.startTime, message: 'Укажите дату начала' },
        { value: this.controlWork.endTime, message: 'Укажите дату окончания' },
      ];

      for (const field of requiredFields) {
        if (!field.value) {
          this.$root.notify(field.message, 'error');
          return false;
        }
      }

      if (this.variants.length === 0) {
        this.$root.notify('Добавьте хотя бы один вариант', 'error');
        return false;
      }

      if (new Date(this.controlWork.startTime) >= new Date(this.controlWork.endTime)) {
        this.$root.notify('Дата окончания должна быть позже даты начала', 'error');
        return false;
      }

      for (const [index, variant] of this.variants.entries()) {
        if (!variant.name) {
          this.$root.notify(`Укажите название для варианта ${index + 1}`, 'error');
          return false;
        }
        if (variant.tasks.length === 0) {
          this.$root.notify(`Добавьте задания для варианта ${index + 1}`, 'error');
          return false;
        }
      }

      return true;
    },

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

    async saveControlWork() {
      if (!this.validateForm()) return;
      
      if (this.loading) return;
      this.loading = true;

      try {
        const tokenData = JSON.parse(localStorage.getItem("tokenData"));
        if (!tokenData || !tokenData.accessToken) {
          throw new Error("Пользователь не авторизован");
        }

        const payload = {
          name: this.controlWork.name,
          description: this.controlWork.description,
          groupId: Number(this.controlWork.group), 
          startTime: new Date(this.controlWork.startTime).toISOString(),
          endTime: new Date(this.controlWork.endTime).toISOString(),
          variants: this.variants.map(variant => ({
            name: variant.name,
            taskIds: variant.tasks.map(task => Number(task.id)) 
          }))
        };

        const response = await fetch('http://localhost:8080/api/v1/contests', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${tokenData.accessToken}`
          },
          body: JSON.stringify(payload)
        });

        if (!response.ok) {
          const errorResponse = await response.json().catch(() => ({}));
          console.error("Полный ответ сервера:", errorResponse);
          throw new Error(errorResponse.message || 
                        errorResponse.error || 
                        `HTTP error ${response.status}`);
        }

        this.$root.notify('Контрольная работа успешно создана!', 'success');
        this.$router.push('/manage-contests');
      } catch (error) {
        console.error("Ошибка при сохранении контрольной:", error.message);
        this.$root.notify(`Ошибка: ${error.message}`, 'error');
      } finally {
        this.loading = false;
      }
    }
  },
};
</script>

<style scoped>
.page {
  padding: 20px;
}

.create-cr-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
}

.title {
  font-size: 30px;
  margin-bottom: 28px;
  font-weight: 700;
  text-align: center;
}

.subtitle {
  font-size: 22px;
  margin: 36px 0 20px;
  font-weight: 600;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 6px;
  font-weight: 500;
  color: #444;
}

.required {
  color: #dc3545;
}

input,
select,
textarea {
  padding: 10px 14px;
  border-radius: 10px;
  border: 1px solid #d1d5db;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

input:focus,
select:focus,
textarea:focus {
  border-color: #2563eb;
  outline: none;
}

.invalid {
  border-color: #dc3545;
}

.error-message {
  color: #dc3545;
  font-size: 14px;
  margin-top: 5px;
}

.form-row {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.variant-block {
  background: #f0f4f8;
  border-radius: 16px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
}

.variant-block:hover {
  background-color: #e7f0fb;
}

.btn-group {
  display: flex;
  gap: 16px;
  margin-top: 30px;
  justify-content: center;
}

.btn {
  padding: 12px 20px;
  font-size: 16px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.btn-primary {
  background-color: #34d399;
  color: white;
}

.btn-primary:hover {
  background-color: #10b981;
}

.btn-primary:disabled {
  background-color: #a7f3d0;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #60a5fa;
  color: white;
}

.btn-secondary:hover {
  background-color: #3b82f6;
}
</style>