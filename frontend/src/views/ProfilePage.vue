<template>
  <div class="profile-wrapper">
    <div v-if="user" class="profile-card">
      <h1>Личный кабинет</h1>

      <div class="profile-header">
        <img src="/default-avatar.png" alt="Avatar" class="avatar" />
        <div>
          <h2>{{ user.firstName }} {{ user.lastName }}</h2>
          <p class="user-role">
            {{ user.role === 'teacher' ? 'Преподаватель' : 'Студент' }}
          </p>
        </div>
      </div>

      <div class="profile-info" v-if="!isEditing">
        <div class="info-item">
          <i class="fas fa-envelope"></i>
          <span>Email:</span>
          <span>{{ user.email }}</span>
        </div>
        <div class="info-item">
          <i class="fas fa-users"></i>
          <span>Группа:</span>
          <span>{{ user.groupName }}</span>
        </div>
        <button class="edit-btn" @click="startEditing">Редактировать</button>
      </div>

      <form v-else class="edit-form" @submit.prevent="saveChanges">
        <div class="form-group">
          <label>Имя:</label>
          <input v-model="form.firstName" type="text" :class="{ 'input-error': !form.firstName }" required />
        </div>

        <div class="form-group">
          <label>Фамилия:</label>
          <input v-model="form.lastName" type="text" :class="{ 'input-error': !form.lastName }" required />
        </div>

        <div class="form-group">
          <label>Email:</label>
          <input v-model="form.email" type="email" :class="{ 'input-error': !form.email }" required />
        </div>

        <div class="form-group">
          <label>Старый пароль:</label>
          <input type="password" v-model="form.oldPassword" :class="{ 'input-error': !form.oldPassword && !form.password }" placeholder="Введите старый пароль" />
        </div>

        <div class="form-group">
          <label>Новый пароль:</label>
          <input type="password" v-model="form.password" :class="{ 'input-error': form.password && !isPasswordValid }" placeholder="Введите новый пароль" />
          <div class="password-hints" v-if="form.password">
            <div :class="{ valid: hasMinLength }">• Минимум 8 символов</div>
            <div :class="{ valid: hasUpperCase }">• Заглавная буква</div>
            <div :class="{ valid: hasLowerCase }">• Строчная буква</div>
            <div :class="{ valid: hasDigit }">• Цифра</div>
            <div :class="{ valid: hasSpecialChar }">• Спецсимвол</div>
          </div>
        </div>

        <div class="form-group" v-if="form.password">
          <label>Подтверждение пароля:</label>
          <input type="password" v-model="form.confirmPassword" :class="{ 'input-error': form.confirmPassword && form.password !== form.confirmPassword }" placeholder="Повторите пароль" />
          <small v-if="form.confirmPassword && form.password !== form.confirmPassword" class="error-message">Пароли не совпадают</small>
        </div>

        <div class="form-group">
          <label>Группа:</label>
          <multiselect
            v-model="selectedGroup"
            :options="groups"
            :searchable="true"
            :allow-empty="false"
            :multiple="false"
            :select-label="''"
            :selected-label="''"
            :deselect-label="''"
            placeholder="Выберите группу"
            label="name"
            track-by="id"
            class="custom-multiselect"
          ></multiselect>
        </div>

        <div class="button-group">
          <button type="submit" class="save-btn" :disabled="isSubmitDisabled">Сохранить</button>
          <button type="button" class="cancel-btn" @click="cancelEditing">Отмена</button>
        </div>
      </form>
    </div>

    <div v-else>
      <p>Загрузка профиля...</p>
    </div>
  </div>
</template>

<script>
import { getUserInfo } from "../js/auth";
import { updateUser, fetchGroups } from "../js/manager";
import Multiselect from "vue-multiselect";
import "vue-multiselect/dist/vue-multiselect.min.css";

export default {
  components: { Multiselect },
  data() {
    return {
      user: null,
      isEditing: false,
      form: {
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        confirmPassword: "",
        oldPassword: "",
      },
      groups: [],
      selectedGroup: null,
    };
  },
  computed: {
    hasMinLength() { return this.form.password.length >= 8; },
    hasUpperCase() { return /[A-Z]/.test(this.form.password); },
    hasLowerCase() { return /[a-z]/.test(this.form.password); },
    hasDigit() { return /\d/.test(this.form.password); },
    hasSpecialChar() { return /[@#$%^&+=!]/.test(this.form.password); },
    isPasswordValid() {
      return !this.form.password || (this.hasMinLength && this.hasUpperCase && this.hasLowerCase && this.hasDigit && this.hasSpecialChar);
    },
    isSubmitDisabled() {
      const passwordsMatch = !this.form.password || this.form.password === this.form.confirmPassword;
      return (
        !this.form.firstName ||
        !this.form.lastName ||
        !this.form.email ||
        !this.form.oldPassword || 
        !passwordsMatch ||        
        !this.isPasswordValid     
      );
    }
  },
  async created() {
    try {
      const userInfo = await getUserInfo();
      this.user = userInfo;
      await this.fetchGroupsList();
    } catch (err) {
      this.$root.notify(err.message, "error");
    }
  },
  methods: {
    async fetchGroupsList() {
      this.groups = await fetchGroups();
      if (this.user && this.user.groupName) {
        this.selectedGroup = this.groups.find(g => g.name === this.user.groupName) || null;
      }
    },
    startEditing() {
      this.form = {
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        email: this.user.email,
        password: "",
        confirmPassword: "",
        oldPassword: "",
      };
      this.isEditing = true;
    },
    cancelEditing() {
      this.isEditing = false;
    },
    async saveChanges() {
      try {
        const payload = {
          firstName: this.form.firstName,
          lastName: this.form.lastName,
          email: this.form.email,
        };

        if (this.selectedGroup) payload.groupId = this.selectedGroup.id;
        payload.password = this.form.password || this.form.oldPassword;

        await updateUser(this.user.id, payload);

        this.user = {
          ...this.user,
          ...payload,
          groupName: this.selectedGroup ? this.selectedGroup.name : this.user.groupName,
        };

        this.isEditing = false;
        this.$root.notify("Данные успешно обновлены!", "success");
      } catch (err) {
        console.error("Ошибка при сохранении:", err);
        this.$root.notify("Ошибка при сохранении изменений", "error");
      }
    },
  },
};
</script>

<style scoped>
.profile-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 90vh;
  padding: 20px;
}

.profile-card {
  background: #fff;
  padding: 2rem;
  border-radius: 20px;
  max-width: 520px;
  width: 100%;
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  animation: fadeIn 0.4s ease-in-out;
}

.profile-card:hover {
  transform: translateY(-4px);
}

h1 {
  text-align: center;
  font-size: 1.8rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.avatar {
  width: 84px;
  height: 84px;
  border-radius: 50%;
  border: 4px solid #2f80ed33;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-role {
  font-size: 0.95rem;
  color: #7f8c8d;
  margin-top: 4px;
}

.profile-info,
.edit-form {
  display: grid;
  gap: 1rem;
  word-break: break-word;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  background: #f9fafc;
  padding: 0.75rem 1rem;
  border-radius: 12px;
  font-size: 0.97rem;
  color: #34495e;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: 500;
  margin-bottom: 4px;
  color: #2c3e50;
}

.form-group input {
  padding: 0.5rem;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: 0.2s;
}

.form-group input:focus {
  border-color: #2f80ed;
  outline: none;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
}

.edit-btn,
.save-btn,
.cancel-btn {
  padding: 0.6rem 1.2rem;
  border-radius: 10px;
  font-weight: 500;
  cursor: pointer;
  transition: 0.3s;
  border: none;
}

.edit-btn {
  background-color: #2f80ed;
  color: #fff;
}

.save-btn {
  background-color: #27ae60;
  color: #fff;
}

.cancel-btn {
  background-color: #e0e0e0;
}

.edit-btn:hover {
  background-color: #256bcc;
}

.save-btn:hover {
  background-color: #219150;
}

.cancel-btn:hover {
  background-color: #ccc;
}

.error-message { 
  color: red; 
  font-size: 0.85rem; 
  margin-top: 4px; 
}

.input-error { 
  border-color: red !important; 
}

.password-hints div { 
  font-size: 0.85rem; 
  margin: 2px 0; 
}

.password-hints .valid { 
  color: green; 
}

.custom-multiselect :deep(.multiselect) {
  min-height: 38px;
  margin-top: 6px;
}

.custom-multiselect :deep(.multiselect__tags) {
  min-height: 38px;
  padding: 8px 30px 8px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: white;
  font-size: 16px;
}

.custom-multiselect :deep(.multiselect__tags:focus-within) {
  border-color: #2f80ed;
  box-shadow: 0 0 0 2px rgba(47, 128, 237, 0.1);
  outline: none;
}

.custom-multiselect :deep(.multiselect__input),
.custom-multiselect :deep(.multiselect__single) {
  font-size: 16px;
  padding: 0;
  margin: 0;
  background: transparent;
  border: none;
}

.custom-multiselect :deep(.multiselect__input:focus) {
  outline: none;
  box-shadow: none;
}

.custom-multiselect :deep(.multiselect__placeholder) {
  color: #999;
  margin: 0;
  padding: 0;
  font-size: 16px;
}

.custom-multiselect :deep(.multiselect__select) {
  height: 36px;
  right: 1px;
  top: 1px;
  width: 30px;
  padding: 0;
  background: transparent;
  border-radius: 0 8px 8px 0;
}

.custom-multiselect :deep(.multiselect__select:before) {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 6px 5px 0 5px;
  border-color: #666 transparent transparent transparent;
  transition: transform 0.2s ease;
}

.custom-multiselect :deep(.multiselect--active .multiselect__select:before) {
  transform: translate(-50%, -50%) rotate(180deg);
}

.custom-multiselect :deep(.multiselect__select:hover) {
  background: rgba(0, 0, 0, 0.05);
}

.custom-multiselect :deep(.multiselect__select:hover:before) {
  border-color: #333 transparent transparent transparent;
}

.custom-multiselect :deep(.multiselect--active .multiselect__select) {
  background: rgba(0, 0, 0, 0.05);
}

.custom-multiselect :deep(.multiselect__content-wrapper) {
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  margin-top: 4px;
  z-index: 10;
}

.custom-multiselect :deep(.multiselect__option) {
  padding: 8px 12px;
  font-size: 16px;
  min-height: 36px;
}

.custom-multiselect :deep(.multiselect__option--selected) {
  background-color: #d0ebff;
  color: #333;
  font-weight: normal;
}

.custom-multiselect :deep(.multiselect__option--highlight) {
  background: #2f80ed;
  color: white;
}

.custom-multiselect :deep(.multiselect__option--selected.multiselect__option--highlight) {
  background: #1366d6;
  color: white;
}
</style>