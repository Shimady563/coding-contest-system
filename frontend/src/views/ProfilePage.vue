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
        <div class="floating-label">
          <input 
            v-model="form.firstName" 
            type="text" 
            id="firstName"
            required 
            placeholder=""
            :class="{ 'input-error': !form.firstName }"
          />
          <label for="firstName">Имя</label>
        </div>

        <div class="floating-label">
          <input 
            v-model="form.lastName" 
            type="text" 
            id="lastName"
            required 
            placeholder=""
            :class="{ 'input-error': !form.lastName }"
          />
          <label for="lastName">Фамилия</label>
        </div>

        <div class="floating-label">
          <input 
            v-model="form.email" 
            type="email" 
            id="email"
            required 
            placeholder=""
            :class="{ 'input-error': !form.email }"
          />
          <label for="email">Email</label>
        </div>

        <div class="floating-label">
          <input 
            type="password" 
            v-model="form.oldPassword" 
            id="oldPassword"
            placeholder=""
            :class="{ 'input-error': !form.oldPassword && form.password }" 
          />
          <label for="oldPassword">Старый пароль</label>
          <small v-if="form.password && !form.oldPassword" class="error-message">
            Для смены пароля требуется старый пароль
          </small>
        </div>

        <div class="floating-label">
          <input 
            type="password" 
            v-model="form.password" 
            id="password"
            placeholder=""
            :class="{ 'input-error': form.password && !isPasswordValid }" 
          />
          <label for="password">Новый пароль</label>
          <div class="password-hints" v-if="form.password">
            <div :class="{ valid: hasMinLength }">
              <span class="hint-icon">✓</span>
              <span class="hint-text">Минимум 8 символов</span>
            </div>
            <div :class="{ valid: hasUpperCase }">
              <span class="hint-icon">✓</span>
              <span class="hint-text">Заглавная буква</span>
            </div>
            <div :class="{ valid: hasLowerCase }">
              <span class="hint-icon">✓</span>
              <span class="hint-text">Строчная буква</span>
            </div>
            <div :class="{ valid: hasDigit }">
              <span class="hint-icon">✓</span>
              <span class="hint-text">Цифра</span>
            </div>
            <div :class="{ valid: hasSpecialChar }">
              <span class="hint-icon">✓</span>
              <span class="hint-text">Спецсимвол</span>
            </div>
          </div>
        </div>

        <div class="floating-label" v-if="form.password">
          <input 
            type="password" 
            v-model="form.confirmPassword" 
            id="confirmPassword"
            placeholder=""
            :class="{ 'input-error': form.confirmPassword && form.password !== form.confirmPassword }" 
          />
          <label for="confirmPassword">Подтверждение пароля</label>
          <small v-if="form.confirmPassword && form.password !== form.confirmPassword" class="error-message">
            Пароли не совпадают
          </small>
        </div>

        <div class="floating-label multiselect-floating" :class="{ active: selectedGroup }">
          <div class="custom-multiselect">
            <multiselect
              v-model="selectedGroup"
              :options="groups"
              :searchable="true"
              :allow-empty="false"
              :multiple="false"
              :select-label="''"
              :selected-label="''"
              :deselect-label="''"
              :append-to-body="true"
              open-direction="below"
              placeholder=""
              label="name"
              track-by="id"
            />
          </div>
          <label for="group">Группа</label>
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
      const passwordFieldsValid = !this.form.password || (this.form.oldPassword && this.isPasswordValid && passwordsMatch);
      
      return (
        !this.form.firstName ||
        !this.form.lastName ||
        !this.form.email ||
        !passwordFieldsValid
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
        
        // Если указан новый пароль, используем его, иначе оставляем старый
        if (this.form.password) {
          payload.password = this.form.password;
        } else if (this.form.oldPassword) {
          payload.password = this.form.oldPassword;
        }

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

.profile-info {
  display: grid;
  gap: 1rem;
  word-break: break-word;
}

.edit-form {
  display: grid;
  gap: 1.8rem;
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

.floating-label {
  position: relative;
  margin-bottom: 0;
}

.floating-label input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #ccc;
  border-radius: 8px;
  outline: none;
  transition: all 0.25s ease;
  background: #fff;
  font-size: 15px;
  color: #333;
  box-sizing: border-box;
}

.floating-label label {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(0, 0, 0, 0.5);
  pointer-events: none;
  padding: 0 4px;
  font-size: 15px;
  transition: all 0.25s ease;
  background: transparent;
}

.floating-label input:focus + label,
.floating-label input:not(:placeholder-shown) + label,
.floating-label.multiselect-floating.active label {
  top: -8px;
  left: 10px;
  font-size: 12px;
  color: #2f80ed;
  background: #fff;
  transform: none;
}

.floating-label input:focus {
  border-color: #2f80ed;
  box-shadow: 0 0 0 2px rgba(47, 128, 237, 0.12);
}

.floating-label input:invalid:not(:focus):not(:placeholder-shown) {
  border-color: #f44336;
}

.floating-label input:invalid:not(:focus):not(:placeholder-shown) + label {
  color: #f44336;
}

.password-hints {
  margin-top: 8px;
  font-size: 13.5px;
  line-height: 1.4;
}

.password-hints div {
  display: flex;
  align-items: center;
  margin: 4px 0;
  color: #888;
  transition: color 0.2s ease;
}

.hint-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  margin-right: 8px;
  font-size: 12px;
  border: 1.5px solid #ddd;
  border-radius: 50%;
  color: transparent;
  transition: all 0.2s ease;
}

.password-hints .valid {
  color: #27ae60;
}

.password-hints .valid .hint-icon {
  background-color: #27ae60;
  border-color: #27ae60;
  color: white;
}

.hint-text {
  flex: 1;
}

.error-message {
  color: #e74c3c;
  font-size: 12px;
  margin-top: 6px;
  display: block;
}

.input-error {
  border-color: #e74c3c !important;
}

.input-error:focus {
  border-color: #e74c3c !important;
  box-shadow: 0 0 0 2px rgba(231, 76, 60, 0.12) !important;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
  gap: 1rem;
}

.edit-btn,
.save-btn,
.cancel-btn {
  padding: 12px 1.5rem;
  border-radius: 10px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  font-size: 15px;
  flex: 1;
}

.edit-btn {
  background-color: #2f80ed;
  color: #fff;
  margin-top: 1rem;
}

.save-btn {
  background-color: #27ae60;
  color: #fff;
}

.cancel-btn {
  background-color: #e0e0e0;
  color: #333;
}

.edit-btn:hover {
  background-color: #256bcc;
  transform: translateY(-1px);
}

.save-btn:hover:not(:disabled) {
  background-color: #219150;
  transform: translateY(-1px);
}

.cancel-btn:hover {
  background-color: #ccc;
  transform: translateY(-1px);
}

.save-btn:disabled {
  background-color: #cfcfcf;
  cursor: not-allowed;
  transform: none;
}

.multiselect-floating {
  position: relative;
}

.custom-multiselect :deep(.multiselect) {
  min-height: 44px;
  margin-top: 0;
  border-radius: 8px;
}

.custom-multiselect :deep(.multiselect__tags) {
  min-height: 44px;
  padding: 8px 36px 8px 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: #fff;
  font-size: 15px;
  transition: all 0.25s ease;
}

.custom-multiselect :deep(.multiselect__tags:focus-within) {
  border-color: #2f80ed;
  box-shadow: 0 0 0 2px rgba(47, 128, 237, 0.12);
  outline: none;
}

.custom-multiselect :deep(.multiselect__input),
.custom-multiselect :deep(.multiselect__single) {
  font-size: 15px;
  padding: 3px;
  margin: 0;
  background: transparent;
  border: none;
}

.custom-multiselect :deep(.multiselect__placeholder) {
  color: rgba(0, 0, 0, 0.5);
  font-size: 15px;
}

.custom-multiselect :deep(.multiselect__select) {
  height: 42px;
  right: 6px;
  top: 1px;
  width: 30px;
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
  font-size: 15px;
  min-height: 36px;
}

.custom-multiselect :deep(.multiselect__option--selected) {
  background-color: #d0ebff;
  color: #333;
}

.custom-multiselect :deep(.multiselect__option--highlight) {
  background: #2f80ed;
  color: white;
}

.multiselect-floating label {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(0, 0, 0, 0.5);
  pointer-events: none;
  padding: 0 4px;
  font-size: 15px;
  transition: all 0.25s ease;
  background: transparent;
  z-index: 2;
}

.multiselect-floating.active label {
  top: -8px;
  left: 10px;
  font-size: 12px;
  color: #2f80ed;
  background: #fff;
  transform: none;
}

.multiselect-floating.active :deep(.multiselect__placeholder) {
  display: none;
}
</style>