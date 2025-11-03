<template>
  <div class="page-container">
    <div class="page-header">
      <h1><i class="fas fa-user-graduate"></i> Управление студентами</h1>
    </div>

    <form class="filters" @submit.prevent="onSearch">
      <div class="filter-group">
        <FloatingInput
          v-model="searchParams.firstName"
          id="firstName"
          label="Имя"
          type="text"
          placeholder=""
        />
      </div>

      <div class="filter-group">
        <FloatingInput
          v-model="searchParams.lastName"
          id="lastName"
          label="Фамилия"
          type="text"
          placeholder=""
        />
      </div>

      <div
        class="filter-group floating-label multiselect-floating"
        :class="{ active: searchParams.selectedGroup || $refs.groupSelect?.isOpen }"
      >
        <div class="custom-multiselect full-width">
          <multiselect
            ref="groupSelect"
            id="groupSelect"
            v-model="searchParams.selectedGroup"
            :options="groups"
            :multiple="false"
            :searchable="true"
            :close-on-select="true"
            :show-labels="false"
            placeholder=""
            label="name"
            track-by="id"
            :append-to-body="true"
            open-direction="below"
            :allow-empty="true"
            @select="forceCloseSelect"
          />
        </div>
        <label for="groupSelect">Группа</label>
      </div>

      <div class="filter-actions">
        <button type="submit" class="apply-btn"><i class="fas fa-filter"></i> Применить</button>
        <button type="button" @click="resetSearch" class="reset-btn">
          <i class="fas fa-broom"></i> Сбросить
        </button>
      </div>
    </form>

    <div class="stats-container" v-if="students.length">
      <div class="stats">Показано {{ students.length }} из {{ totalElements }} студентов</div>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <span>Загрузка данных...</span>
    </div>

    <div v-else-if="!students.length" class="empty-state">
      <i class="fas fa-user-slash"></i>
      <h3>Студенты не найдены</h3>
      <p>Попробуйте изменить параметры поиска</p>
    </div>

    <div v-else class="table-container">
      <table class="students-table">
        <thead>
          <tr>
            <th class="id-col">№</th>
            <th class="name-col">ФИО</th>
            <th class="email-col">Email</th>
            <th class="group-col">Группа</th>
            <th class="actions-col">Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(student, index) in students" :key="student.id">
            <td class="id-col">{{ currentPage * pageSize + index + 1 }}</td>
            <td class="name-col">{{ student.lastName }} {{ student.firstName }}</td>
            <td class="email-col">
              <a :href="`mailto:${student.email}`">{{ student.email }}</a>
            </td>
            <td class="group-col">
              <span :class="{ 'no-group': !student.groupName }">
                {{ student.groupName || 'Не указана' }}
              </span>
            </td>
            <td class="actions-col">
              <div class="action-buttons">
                <button
                  @click="openEditModal(student)"
                  class="btn-icon edit-btn"
                  title="Редактировать"
                >
                  <i class="fas fa-pencil-alt"></i>
                </button>
                <button
                  @click="confirmDeleteStudent(student)"
                  class="btn-icon delete-btn"
                  title="Удалить"
                >
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-container" v-if="totalPages > 1">
      <div class="pagination-info">Страница {{ currentPage + 1 }} из {{ totalPages }}</div>
      <div class="pagination-controls">
        <button @click="prevPage" :disabled="currentPage === 0" class="pagination-btn">
          <i class="fas fa-chevron-left"></i>
        </button>
        <div class="page-indicator">Страница {{ currentPage + 1 }} из {{ totalPages }}</div>
        <button @click="nextPage" :disabled="currentPage >= totalPages - 1" class="pagination-btn">
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>

    <div v-if="editingStudent" class="modal-backdrop">
      <div class="modal-dialog">
        <div class="modal-header">
          <h3><i class="fas fa-user-edit"></i> Редактирование студента</h3>
        </div>
        <div class="modal-body">
          <FloatingInput
            v-model="editingStudent.firstName"
            id="editFirstName"
            name="editFirstName"
            label="Имя"
            type="text"
            required
            placeholder=""
            class="form-input"
          />
          <FloatingInput
            v-model="editingStudent.lastName"
            id="editLastName"
            name="editLastName"
            label="Фамилия"
            type="text"
            required
            placeholder=""
            class="form-input"
          />
          <FloatingInput
            v-model="editingStudent.email"
            id="editEmail"
            name="editEmail"
            label="Email"
            type="email"
            required
            placeholder=""
            class="form-input"
          />
          <FloatingInput
            v-model="editingStudent.password"
            id="editPassword"
            name="editPassword"
            label="Новый пароль"
            type="password"
            placeholder=""
            class="form-input"
            autocomplete="new-password"
          >
            <PasswordHints :password="editingStudent.password" />
          </FloatingInput>

          <div
            class="floating-label multiselect-floating"
            :class="{ active: editingStudent?.selectedGroup || $refs.editGroupSelect?.isOpen }"
          >
            <div class="custom-multiselect full-width">
              <multiselect
                ref="editGroupSelect"
                id="editGroup"
                name="editGroup"
                v-model="editingStudent.selectedGroup"
                :options="groups"
                :multiple="false"
                :searchable="true"
                :close-on-select="true"
                :show-labels="false"
                placeholder=""
                label="name"
                track-by="id"
                :append-to-body="false"
                open-direction="below"
              />
            </div>
            <label for="editGroup">Группа</label>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeModal" class="btn-cancel">Отмена</button>
          <button @click="saveStudent" class="btn-save">Сохранить изменения</button>
        </div>
      </div>
    </div>

    <ConfirmDialog
      v-if="showConfirmDialog"
      :title="confirmDialog.title"
      :message="confirmDialog.message"
      @confirm="executeDelete"
      @cancel="cancelDelete"
    />
  </div>
</template>

<script>
import { listUsers, updateUser, deleteUser, fetchGroups } from '@/js/manager'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import FloatingInput from '@/components/FloatingInput.vue'
import PasswordHints from '@/components/PasswordHints.vue'
import Multiselect from 'vue-multiselect'
import 'vue-multiselect/dist/vue-multiselect.min.css'

export default {
  components: {
    Multiselect,
    ConfirmDialog,
    FloatingInput,
    PasswordHints,
  },
  data() {
    return {
      students: [],
      groups: [],
      currentPage: 0,
      pageSize: 10,
      totalPages: 1,
      totalElements: 0,
      loading: false,
      editingStudent: null,
      showConfirmDialog: false,
      confirmDialog: { title: '', message: '' },
      studentToDelete: null,
      searchParams: {
        firstName: '',
        lastName: '',
        role: 'ROLE_STUDENT',
        selectedGroup: null,
      },
      passwordRules: {
        minLength: 8,
        upper: /[A-Z]/,
        lower: /[a-z]/,
        digit: /\d/,
        special: /[@#$%^&+=!?*]/,
      },
    }
  },
  async created() {
    await this.fetchGroups()
    await this.fetchStudents()
  },
  watch: {
    groups(newGroups) {
      if (this.searchParams.selectedGroup) {
        const ref = newGroups.find((g) => g.id === this.searchParams.selectedGroup.id)
        if (ref) this.searchParams.selectedGroup = ref
      }
      if (this.editingStudent && this.editingStudent.selectedGroup) {
        const ref = newGroups.find((g) => g.id === this.editingStudent.selectedGroup.id)
        if (ref) this.editingStudent.selectedGroup = ref
      }
    },
  },
  computed: {
    groupOptions() {
      return this.groups.map((group) => ({ name: group.name }))
    },
    hasMinLength() {
      return this.editingStudent?.password?.length >= this.passwordRules.minLength
    },
    hasUpperCase() {
      return this.passwordRules.upper.test(this.editingStudent?.password || '')
    },
    hasLowerCase() {
      return this.passwordRules.lower.test(this.editingStudent?.password || '')
    },
    hasDigit() {
      return this.passwordRules.digit.test(this.editingStudent?.password || '')
    },
    hasSpecialChar() {
      return this.passwordRules.special.test(this.editingStudent?.password || '')
    },
    isPasswordValid() {
      const p = this.editingStudent?.password || ''
      return (
        !p ||
        (this.hasMinLength &&
          this.hasUpperCase &&
          this.hasLowerCase &&
          this.hasDigit &&
          this.hasSpecialChar)
      )
    },
  },
  methods: {
    async fetchStudents() {
      this.loading = true
      try {
        const params = {
          pageNumber: this.currentPage,
          pageSize: this.pageSize,
          role: this.searchParams.role,
        }
        if (this.searchParams.firstName) params.firstName = this.searchParams.firstName
        if (this.searchParams.lastName) params.lastName = this.searchParams.lastName
        if (this.searchParams.selectedGroup) {
          params.groupName = this.searchParams.selectedGroup.name
        }

        const data = await listUsers(params)
        this.students = data.content || []
        this.totalPages = data.page?.totalPages || 1
        this.totalElements = data.page?.totalElements || 0
      } catch (err) {
        this.$root.notify('Ошибка при загрузке студентов', 'error')
      } finally {
        this.loading = false
      }
    },
    async fetchGroups() {
      try {
        const groups = await fetchGroups()
        this.groups = Array.isArray(groups) ? groups : groups.content || []
      } catch (err) {
        this.$root.notify('Ошибка при загрузке групп', 'error')
        this.groups = []
      }
    },
    forceCloseSelect() {
      setTimeout(() => {
        this.$refs.groupSelect?.deactivate()
      }, 0)
    },
    openEditModal(student) {
      const groupRef =
        this.groups.find((g) => g.id === student.groupId || g.name === student.groupName) || null
      this.editingStudent = { ...student, selectedGroup: groupRef }
    },
    closeModal() {
      this.editingStudent = null
    },
    async saveStudent() {
      try {
        const { id, firstName, lastName, email, selectedGroup, password } = this.editingStudent
        const groupId = selectedGroup ? selectedGroup.id : null

        if (password && !this.isPasswordValid) {
          this.$root.notify('Пароль не соответствует требованиям', 'error')
          return
        }

        const payload = { firstName, lastName, email, groupId }
        if (password) payload.password = password

        await updateUser(id, payload)

        this.$root.notify('Данные обновлены', 'success')
        this.closeModal()
        this.fetchStudents()
      } catch {
        this.$root.notify('Ошибка при обновлении', 'error')
      }
    },
    onSearch() {
      this.currentPage = 0
      this.fetchStudents()
    },
    resetSearch() {
      this.searchParams = {
        firstName: '',
        lastName: '',
        selectedGroup: null,
        role: 'ROLE_STUDENT',
      }
      this.currentPage = 0
      this.fetchStudents()
    },
    confirmDeleteStudent(student) {
      this.studentToDelete = student
      this.confirmDialog = {
        title: 'Удаление студента',
        message: `Вы уверены, что хотите удалить студента "${student.lastName} ${student.firstName}"? Это действие нельзя отменить.`,
      }
      this.showConfirmDialog = true
    },
    async deleteStudent(id) {
      try {
        await deleteUser(id)

        this.$root.notify('Студент удален', 'success')
        this.fetchStudents()
      } catch {
        this.$root.notify('Ошибка при удалении', 'error')
      }
    },
    async executeDelete() {
      if (!this.studentToDelete) return
      await this.deleteStudent(this.studentToDelete.id)
      this.showConfirmDialog = false
      this.studentToDelete = null
    },
    cancelDelete() {
      this.showConfirmDialog = false
      this.studentToDelete = null
    },
    nextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++
        this.fetchStudents()
      }
    },
    prevPage() {
      if (this.currentPage > 0) {
        this.currentPage--
        this.fetchStudents()
      }
    },
  },
}
</script>

<style scoped>
.students-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.students-table th {
  background: #f8f9fa;
  color: #555;
  font-weight: 600;
  text-align: left;
  padding: 16px;
  border-bottom: 2px solid #e9ecef;
}

.students-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #e9ecef;
  vertical-align: middle;
}

.students-table tr:hover td {
  background-color: #f8f9fa;
}

.id-col {
  min-width: 80px;
  color: #7f8c8d;
}
.name-col {
  min-width: 200px;
}
.email-col a {
  color: #2f80ed;
  text-decoration: none;
}
.email-col a:hover {
  text-decoration: underline;
}
.group-col .no-group {
  color: #95a5a6;
  font-style: italic;
}
.actions-col {
  min-width: 120px;
}
.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
  background: transparent;
}

.modal-dialog {
  position: relative;
  background: white;
  border-radius: 12px;
  width: 500px;
  max-width: calc(100% - 40px);
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  animation: modalFadeIn 0.3s ease;
}

.password-hints {
  margin-top: 8px;
  font-size: 13px;
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

.filters .multiselect-floating label {
  top: 21px;
}

.modal-body :deep(.multiselect),
.multiselect-floating label {
  background-color: white !important;
}

.modal-body :deep(.multiselect__content-wrapper) {
  z-index: 10000 !important;
  position: fixed;
  width: 452px !important;
  min-width: auto !important;
  left: auto !important;
  right: auto !important;
}

.modal-footer,
.modal-header {
  z-index: 1;
  background-color: white;
}

.btn-cancel {
  background-color: #f8f9fa;
  color: #333;
  border: 1px solid #ddd;
}
.btn-cancel:hover {
  background-color: #e9ecef;
}
.btn-save {
  background-color: #2ecc71;
  color: white;
}
.btn-save:hover {
  background-color: #27ae60;
}

@media (max-width: 600px) {
  .modal-body :deep(.multiselect__content-wrapper) {
    position: absolute !important;
    width: 100% !important;
  }
}
</style>
