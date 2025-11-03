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
        <FloatingInput
          v-model="form.firstName"
          id="firstName"
          name="firstName"
          label="Имя"
          type="text"
          required
          placeholder=""
          autocomplete="given-name"
          :error="!form.firstName"
        />

        <FloatingInput
          v-model="form.lastName"
          id="lastName"
          name="lastName"
          label="Фамилия"
          type="text"
          required
          placeholder=""
          autocomplete="family-name"
          :error="!form.lastName"
        />

        <FloatingInput
          v-model="form.email"
          id="email"
          name="email"
          label="Email"
          type="email"
          required
          placeholder=""
          autocomplete="email"
          :error="!form.email"
        />

        <FloatingInput
          v-model="form.password"
          id="password"
          name="password"
          label="Новый пароль"
          type="password"
          placeholder=""
          autocomplete="new-password"
          :error="form.password !== '' && !passwordValid"
        >
          <PasswordHints :password="form.password" />
        </FloatingInput>

        <FloatingInput
          v-if="form.password"
          v-model="form.confirmPassword"
          id="confirmPassword"
          name="confirmPassword"
          label="Подтверждение пароля"
          type="password"
          autocomplete="new-password"
          :error="form.confirmPassword && form.password !== form.confirmPassword"
        >
          <small
            v-if="form.confirmPassword && form.password !== form.confirmPassword"
            class="error-message"
          >
            Пароли не совпадают
          </small>
        </FloatingInput>

        <div
          class="floating-label multiselect-floating"
          v-if="user.role !== 'teacher'"
          :class="{ active: selectedGroup || $refs.groupSelect?.isOpen }"
        >
          <div class="custom-multiselect">
            <multiselect
              id="group"
              name="group"
              ref="groupSelect"
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
              @open="$forceUpdate()"
              @close="$forceUpdate()"
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
import { getUserInfo } from '../js/auth'
import { updateUser, fetchGroups } from '../js/manager'
import { validatePassword } from '@/js/password'
import FloatingInput from '@/components/FloatingInput.vue'
import PasswordHints from '@/components/PasswordHints.vue'
import Multiselect from 'vue-multiselect'
import 'vue-multiselect/dist/vue-multiselect.min.css'

export default {
  components: {
    Multiselect,
    FloatingInput,
    PasswordHints,
  },
  data() {
    return {
      user: null,
      isEditing: false,
      form: {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
      },
      groups: [],
      selectedGroup: null,
    }
  },
  computed: {
    passwordValid() {
      if (!this.form.password) return true
      return validatePassword(this.form.password).isValid
    },
    passwordsMatch() {
      return !this.form.password || this.form.password === this.form.confirmPassword
    },
    isSubmitDisabled() {
      return (
        !this.form.firstName ||
        !this.form.lastName ||
        !this.form.email ||
        !this.passwordValid ||
        !this.passwordsMatch
      )
    },
  },
  async created() {
    try {
      const userInfo = await getUserInfo()
      this.user = userInfo
      await this.fetchGroupsList()
    } catch (err) {
      this.$root.notify(err.message, 'error')
    }
  },
  methods: {
    validatePassword,
    async fetchGroupsList() {
      this.groups = await fetchGroups()
      if (this.user && this.user.groupName) {
        this.selectedGroup = this.groups.find((g) => g.name === this.user.groupName) || null
      }
    },
    startEditing() {
      this.form = {
        firstName: this.user.firstName,
        lastName: this.user.lastName,
        email: this.user.email,
        password: '',
        confirmPassword: '',
      }
      this.isEditing = true
    },
    cancelEditing() {
      this.isEditing = false
    },
    async saveChanges() {
      try {
        const payload = {
          firstName: this.form.firstName,
          lastName: this.form.lastName,
          email: this.form.email,
          password: this.form.password || '',
        }

        if (this.user.role !== 'teacher' && this.selectedGroup) {
          payload.groupId = this.selectedGroup.id
        }

        await updateUser(this.user.id, payload)

        this.user = {
          ...this.user,
          ...payload,
          groupName:
            this.user.role !== 'teacher' && this.selectedGroup
              ? this.selectedGroup.name
              : this.user.groupName,
        }

        this.isEditing = false
        this.$root.notify('Данные успешно обновлены!', 'success')
      } catch (err) {
        console.error('Ошибка при сохранении:', err)
        this.$root.notify('Ошибка при сохранении изменений', 'error')
      }
    },
  },
}
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
  background-color: #34d399;
  color: #fff;
}

.btn-cancel {
  background-color: #f8f9fa;
  color: #333;
  border: 1px solid #ddd;
}

.edit-btn:hover {
  background-color: #256bcc;
  transform: translateY(-1px);
}

.save-btn:hover:not(:disabled) {
  background-color: #10b981;
  transform: translateY(-1px);
}

.cancel-btn:hover {
  background-color: #e9ecef;
  transform: translateY(-1px);
}

.save-btn:disabled {
  background-color: #cfcfcf;
  cursor: not-allowed;
  transform: none;
}

.error-message {
  color: #e74c3c;
  font-size: 14px;
  margin-top: -20px;
  text-align: center;
  line-height: 1.4;
}

.multiselect-floating.active label {
  top: -7px;
}
</style>
