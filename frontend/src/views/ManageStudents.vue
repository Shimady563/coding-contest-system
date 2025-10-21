<template>
  <div class="page-container">
    <div class="page-header">
      <h1><i class="fas fa-user-graduate"></i> Управление студентами</h1>
    </div>

    <form class="filters" @submit.prevent="onSearch">
      <div class="filter-group floating-label">
        <input 
          type="text" 
          v-model="searchParams.firstName" 
          id="firstName"
          class="text-input" 
          placeholder=""
        />
        <label for="firstName">Имя</label>
      </div>

      <div class="filter-group floating-label">
        <input 
          type="text" 
          v-model="searchParams.lastName" 
          id="lastName"
          class="text-input" 
          placeholder=""
        />
        <label for="lastName">Фамилия</label>
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
        <button type="submit" class="apply-btn">
          <i class="fas fa-filter"></i> Применить
        </button>
        <button type="button" @click="resetSearch" class="reset-btn">
          <i class="fas fa-broom"></i> Сбросить
        </button>
      </div>
    </form>

    <div class="stats-container" v-if="students.length">
      <div class="stats">
        Показано {{ students.length }} из {{ totalElements }} студентов
      </div>
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
            <td class="name-col">
              {{ student.lastName }} {{ student.firstName }}
            </td>
            <td class="email-col">
              <a :href="`mailto:${student.email}`">{{ student.email }}</a>
            </td>
            <td class="group-col">
              <span :class="{'no-group': !student.groupName}">
                {{ student.groupName || 'Не указана' }}
              </span>
            </td>
            <td class="actions-col">
              <div class="action-buttons">
                <button @click="openEditModal(student)" class="btn-icon edit-btn" title="Редактировать">
                  <i class="fas fa-pencil-alt"></i>
                </button>
                <button @click="confirmDeleteStudent(student)" class="btn-icon delete-btn" title="Удалить">
                  <i class="fas fa-trash-alt"></i>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-container" v-if="totalPages > 1">
      <div class="pagination-info">
        Страница {{ currentPage + 1 }} из {{ totalPages }}
      </div>
      <div class="pagination-controls">
        <button 
          @click="prevPage" 
          :disabled="currentPage === 0" 
          class="pagination-btn"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        <div class="page-indicator">
          Страница {{ currentPage + 1 }} из {{ totalPages }}
        </div>
        <button 
          @click="nextPage" 
          :disabled="currentPage >= totalPages - 1" 
          class="pagination-btn"
        >
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
          <div class="floating-label">
            <input 
              v-model="editingStudent.firstName" 
              id="editFirstName"
              class="form-input"
              placeholder=""
            >
            <label for="editFirstName">Имя</label>
          </div>
          <div class="floating-label">
            <input 
              v-model="editingStudent.lastName" 
              id="editLastName"
              class="form-input"
              placeholder=""
            >
            <label for="editLastName">Фамилия</label>
          </div>
          <div class="floating-label">
            <input 
              v-model="editingStudent.email" 
              type="email" 
              id="editEmail"
              class="form-input"
              placeholder=""
            >
            <label for="editEmail">Email</label>
          </div>
          <div 
            class="floating-label multiselect-floating" 
            :class="{ active: editingStudent?.selectedGroup || $refs.editGroupSelect?.isOpen }"
          >
            <div class="custom-multiselect full-width">
              <multiselect
                ref="editGroupSelect"
                v-model="editingStudent.selectedGroup"
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
import { listUsers, updateUser, deleteUser, fetchGroups } from "@/js/manager";
import ConfirmDialog from "@/components/ConfirmDialog.vue";
import Multiselect from "vue-multiselect";
import "vue-multiselect/dist/vue-multiselect.min.css";

export default {
  components: {
    Multiselect,
    ConfirmDialog
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
      confirmDialog: { title: "", message: "" },
      studentToDelete: null,
      searchParams: {
        firstName: "",
        lastName: "",
        role: "ROLE_STUDENT",
        selectedGroup: null
      }
    };
  },
  async created() {
    await this.fetchGroups();
    await this.fetchStudents();
  },
  watch: {
    groups(newGroups) {
      if (this.searchParams.selectedGroup) {
        const ref = newGroups.find(g => g.id === this.searchParams.selectedGroup.id);
        if (ref) this.searchParams.selectedGroup = ref;
      }
      if (this.editingStudent && this.editingStudent.selectedGroup) {
        const ref = newGroups.find(g => g.id === this.editingStudent.selectedGroup.id);
        if (ref) this.editingStudent.selectedGroup = ref;
      }
    }
  },
  computed: {
    groupOptions() {
      return this.groups.map(group => ({ name: group.name }));
    }
  },
  methods: {
    async fetchStudents() {
      this.loading = true;
      try {
        const params = {
          pageNumber: this.currentPage,
          pageSize: this.pageSize,
          role: this.searchParams.role
        };
        if (this.searchParams.firstName) params.firstName = this.searchParams.firstName;
        if (this.searchParams.lastName) params.lastName = this.searchParams.lastName;
        if (this.searchParams.selectedGroup) {
          params.groupName = this.searchParams.selectedGroup.name;
        }

        const data = await listUsers(params);
        this.students = data.content || [];
        this.totalPages = data.page?.totalPages || 1;
        this.totalElements = data.page?.totalElements || 0;
      } catch (err) {
        this.$toast?.error("Ошибка при загрузке студентов");
      } finally {
        this.loading = false;
      }
    },
    async fetchGroups() {
      try {
        const groups = await fetchGroups();
        this.groups = Array.isArray(groups) ? groups : (groups.content || []);
      } catch (err) {
        this.$toast?.error("Ошибка при загрузке групп");
        this.groups = [];
      }
    },
    forceCloseSelect() {
          setTimeout(() => {
        this.$refs.groupSelect?.deactivate();
      }, 0);
    },
    openEditModal(student) {
      const groupRef = this.groups.find(g => g.id === student.groupId || g.name === student.groupName) || null;
      this.editingStudent = { ...student, selectedGroup: groupRef };
    },
    closeModal() {
      this.editingStudent = null;
    },
    async saveStudent() {
      try {
        const { id, firstName, lastName, email, selectedGroup } = this.editingStudent;
        const groupId = selectedGroup ? selectedGroup.id : null;
        
        await updateUser(id, { firstName, lastName, email, groupId });

        this.$toast?.success("Данные обновлены");
        this.closeModal();
        this.fetchStudents();
      } catch{
        this.$toast?.error("Ошибка при обновлении");
      }
    },
    onSearch() {
      this.currentPage = 0;
      this.fetchStudents();
    },
    resetSearch() {
      this.searchParams = {
        firstName: '',
        lastName: '',
        selectedGroup: null,
        role: 'ROLE_STUDENT'
      };
      this.currentPage = 0;
      this.fetchStudents();
    },
    confirmDeleteStudent(student) {
      this.studentToDelete = student;
      this.confirmDialog = {
        title: 'Удаление студента',
        message: `Вы уверены, что хотите удалить студента "${student.lastName} ${student.firstName}"? Это действие нельзя отменить.`
      };
      this.showConfirmDialog = true;
    },
    async deleteStudent(id) {
      try {
        await deleteUser(id);

        this.$toast?.success("Студент удален");
        this.fetchStudents();
      } catch {
        this.$toast?.error("Ошибка при удалении");
      }
    },
    async executeDelete() {
      if (!this.studentToDelete) return;
      await this.deleteStudent(this.studentToDelete.id);
      this.showConfirmDialog = false;
      this.studentToDelete = null;
    },
    cancelDelete() {
      this.showConfirmDialog = false;
      this.studentToDelete = null;
    },
    nextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++;
        this.fetchStudents();
      }
    },
    prevPage() {
      if (this.currentPage > 0) {
        this.currentPage--;
        this.fetchStudents();
      }
    },
  }
};
</script>

<style scoped>
.page-container {
  padding: 20px;
  max-width: 1152px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  color: #2c3e50;
  font-size: 28px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
}

.filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  background: #f8f9fa;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 20px;
  border: 1px solid #e9ecef;
}

.filters .floating-label,
.modal-body .floating-label {
  position: relative;
  margin-bottom: 20px;
  background-color: #f8f9fa;
}

.filters .floating-label input,
.modal-body .floating-label input {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  outline: none;
  font-size: 14px;
  color: #333;
  box-sizing: border-box;
  background-color: #f8f9fa;
  transition: all 0.25s ease;
}

.filters .floating-label label,
.modal-body .floating-label label {
  position: absolute;
  left: 16px;
  top: 14px; 
  font-size: 14px;
  color: rgba(0,0,0,0.5);
  pointer-events: none;
  padding: 0 4px;
  transition: all 0.25s ease;
  background-color: #f8f9fa;
  z-index: 2;
}

.filters .floating-label input:focus + label,
.filters .floating-label input:not(:placeholder-shown) + label,
.modal-body .floating-label input:focus + label,
.modal-body .floating-label input:not(:placeholder-shown) + label {
  top: -8px; 
  left: 12px;
  font-size: 12px;
  color: #2f80ed;
  background-color: #f8f9fa;
  padding: 0 4px;
  z-index: 3;
}

.filters .floating-label input:focus,
.modal-body .floating-label input:focus {
  border-color: #2f80ed;
  box-shadow: 0 0 0 2px rgba(47, 128, 237, 0.1);
}

.filters .multiselect-floating,
.modal-body .multiselect-floating {
  position: relative;
}

.filters .multiselect-floating label,
.modal-body .multiselect-floating label {
  position: absolute;
  left: 16px;
  top: 14px;
  font-size: 14px;
  color: rgba(0,0,0,0.5);
  pointer-events: none;
  padding: 0 4px;
  transition: all 0.25s ease;
  background-color: #f8f9fa;
  z-index: 2;
}

.filters .multiselect-floating.active label,
.modal-body .multiselect-floating.active label {
  top: -8px;
  left: 12px;
  font-size: 12px;
  color: #2f80ed;
  background-color: #f8f9fa;
  z-index: 2;
}

.multiselect-floating :deep(.multiselect),
.multiselect-floating :deep(.multiselect__tags),
.multiselect-floating :deep(.multiselect__content-wrapper) {
  z-index: auto !important;    
}

.filter-actions {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.apply-btn, .reset-btn {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  height: fit-content;
}

.apply-btn {
  background: #2f80ed;
  color: white;
}

.apply-btn:hover {
  background: #256bcc;
  transform: translateY(-1px);
}

.reset-btn {
  background: #6c757d;
  color: white;
}

.reset-btn:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

.stats-container {
  margin: 16px 0;
  padding: 0 8px;
}

.stats {
  font-size: 14px;
  color: #7f8c8d;
  font-weight: 500;
  background: #f8f9fa;
  padding: 8px 16px;
  border-radius: 6px;
  display: inline-block;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #7f8c8d;
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

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #7f8c8d;
}

.empty-state i {
  font-size: 3rem;
  margin-bottom: 16px;
  color: #bdc3c7;
}

.empty-state h3 {
  font-size: 18px;
  margin-bottom: 8px;
  color: #2c3e50;
}

.empty-state p {
  font-size: 14px;
}

.table-container {
  overflow-x: auto;
  border-radius: 12px;
  border: 1px solid #e9ecef;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

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

.id-col { min-width: 80px; color: #7f8c8d; }
.name-col { min-width: 200px; }
.email-col a { color: #2f80ed; text-decoration: none; }
.email-col a:hover { text-decoration: underline; }
.group-col .no-group { color: #95a5a6; font-style: italic; }
.actions-col { min-width: 120px; }
.action-buttons { display: flex; gap: 8px; }

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

.edit-btn { color: #2f80ed; }
.edit-btn:hover { background-color: rgba(47, 128, 237, 0.1); }
.delete-btn { color: #e74c3c; }
.delete-btn:hover { background-color: rgba(231, 76, 60, 0.1); }

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding: 20px 0;
  border-top: 1px solid #e9ecef;
}

.pagination-info { font-size: 14px; color: #7f8c8d; }
.pagination-controls { display: flex; align-items: center; gap: 12px; }
.pagination-btn {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #ddd;
  background: white;
  color: #333;
}
.pagination-btn:disabled { opacity: 0.5; cursor: not-allowed; }
.pagination-btn:hover:not(:disabled) {
  background-color: #f8f9fa;
  border-color: #3498db;
  color: #3498db;
}
.page-indicator { font-size: 14px; color: #555; font-weight: 500; }

.modal-backdrop {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000; backdrop-filter: blur(3px);
}

.modal-dialog {
  position: relative;
  background: white;
  border-radius: 12px;
  width: 500px; 
  max-width: calc(100% - 40px); 
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0,0,0,0.2);
  animation: modalFadeIn 0.3s ease;
}

.modal-body :deep(.multiselect__content-wrapper) {
  z-index: 10000 !important; 
  position: fixed;
  width: 452px !important;
  min-width: auto !important;
  left: auto !important;
  right: auto !important;
}

.modal-footer {
  z-index: 1; 
}

.modal-body .floating-label {
  position: relative;
  margin-bottom: 20px;
  background-color: white;
}

.modal-body .floating-label input {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  outline: none;
  font-size: 14px;
  color: #333;
  box-sizing: border-box;
  background-color: white; 
  transition: all 0.25s ease;
}

.modal-body .floating-label label {
  position: absolute;
  left: 16px;
  top: 14px; 
  font-size: 14px;
  color: rgba(0,0,0,0.5);
  pointer-events: none;
  padding: 0 4px;
  transition: all 0.25s ease;
  background-color: white; 
  z-index: 2;
}

.modal-body .floating-label input:focus + label,
.modal-body .floating-label input:not(:placeholder-shown) + label {
  top: -8px; 
  left: 12px;
  font-size: 12px;
  color: #2f80ed;
  background-color: white;
  padding: 0 4px;
  z-index: 3;
}

.modal-body .multiselect-floating label {
  position: absolute;
  left: 16px;
  top: 14px;
  font-size: 14px;
  color: rgba(0,0,0,0.5);
  pointer-events: none;
  padding: 0 4px;
  transition: all 0.25s ease;
  background-color: white; 
  z-index: 2;
}

.modal-body .multiselect-floating.active label {
  top: -8px;
  left: 12px;
  font-size: 12px;
  color: #2f80ed;
  background-color: white; 
  z-index: 2;
}

.modal-body .custom-multiselect :deep(.multiselect__tags) {
  min-height: 48px; 
  padding: 12px 40px 0 16px;
  border: 1px solid #ddd; 
  border-radius: 8px; 
  background: white; 
  font-size: 14px;
}

@keyframes modalFadeIn {
  from { opacity: 0; transform: scale(0.9) translateY(-20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background: white;
  z-index: 10;
}

.modal-header h3 {
  color: #2c3e50;
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.modal-body { 
  padding: 24px; 
  box-sizing: border-box; 
  overflow: visible !important;
}

.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid #e9ecef;
  display: flex; justify-content: flex-end; gap: 12px;
  position: sticky; 
  bottom: 0; 
  background: white;
}

.btn-cancel, .btn-save {
  padding: 12px 24px; border-radius: 8px;
  font-size: 14px; font-weight: 500;
  cursor: pointer; transition: all 0.2s ease; border: none;
}

.btn-cancel { background-color: #f8f9fa; color: #333; border: 1px solid #ddd; }
.btn-cancel:hover { background-color: #e9ecef; }
.btn-save { background-color: #2ecc71; color: white; }
.btn-save:hover { background-color: #27ae60; }

.custom-multiselect :deep(.multiselect) { min-height: 48px; margin-top: 0; }
.custom-multiselect :deep(.multiselect__tags) {
  min-height: 48px; padding: 12px 40px 0 16px;
  border: 1px solid #ddd; border-radius: 8px; background: inherit; font-size: 14px;
}
.custom-multiselect :deep(.multiselect__tags:focus-within) {
  border-color: #2f80ed; box-shadow: 0 0 0 2px rgba(47,128,237,0.1); outline: none;
}
.custom-multiselect :deep(.multiselect__input),
.custom-multiselect :deep(.multiselect__single) {
  font-size: 14px; padding: 4px 0; margin: 0; background: transparent; border: none;
}
.custom-multiselect :deep(.multiselect__placeholder) {
  color: rgba(0,0,0,0.5); font-size: 14px; margin-top: 2px;
}
.custom-multiselect :deep(.multiselect__select) {
  height: 46px; right: 6px; top: 1px; width: 30px; background: transparent;
  border-radius: 0 8px 8px 0;
}
.custom-multiselect :deep(.multiselect__select:before) {
  content: ''; position: absolute; top: 50%; left: 50%;
  transform: translate(-50%,-50%); width: 0; height: 0;
  border-style: solid; border-width: 6px 5px 0 5px;
  border-color: #666 transparent transparent transparent; transition: transform 0.2s ease;
}
.custom-multiselect :deep(.multiselect--active .multiselect__select:before) {
  transform: translate(-50%,-50%) rotate(180deg);
}
.custom-multiselect :deep(.multiselect__select:hover) { background: rgba(0,0,0,0.05); }
.custom-multiselect :deep(.multiselect__select:hover:before) { border-color: #333 transparent transparent transparent; }
.custom-multiselect :deep(.multiselect--active .multiselect__select) { 
  background: rgba(47, 128, 237, 0.05); 
}
.custom-multiselect :deep(.multiselect__content-wrapper) {
  border: 1px solid #ddd; border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1); margin-top: 4px; z-index: 10;
}
.custom-multiselect :deep(.multiselect__option) { padding: 10px 12px; font-size: 14px; min-height: 40px; }
.custom-multiselect :deep(.multiselect__option--selected) { 
  background-color: rgba(47, 128, 237, 0.1); 
  color: #2f80ed; 
  font-weight: 500;
}
.custom-multiselect :deep(.multiselect__option--highlight) { 
  background: #2f80ed;
  color: white; 
}
.custom-multiselect :deep(.multiselect__option--selected.multiselect__option--highlight) { 
  background: #256bcc; 
  color: white; 
}
.multiselect-floating.active :deep(.multiselect__placeholder) { display: none; }

@media (max-width: 768px) {
  .page-container {
    padding: 15px;
  }

  .filters {
    grid-template-columns: 1fr;
    padding: 20px;
    gap: 16px;
  }

  .filter-actions {
    flex-direction: column;
    gap: 10px;
  }

  .apply-btn, .reset-btn {
    width: 100%;
    justify-content: center;
  }

  .pagination-container {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }

  .modal-dialog {
    width: 95%;
    margin: 20px;
  }

  .modal-body :deep(.multiselect__content-wrapper) {
    width: calc(100vw - 80px) !important;
  }

  .modal-footer {
    flex-direction: column;
  }

  .btn-cancel, .btn-save {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .page-header h1 {
    font-size: 24px;
  }

  .filters {
    padding: 16px;
  }

  .students-table {
    font-size: 13px;
  }

  .students-table th,
  .students-table td {
    padding: 12px 8px;
  }

  .action-buttons {
    gap: 4px;
  }

  .btn-icon {
    width: 32px;
    height: 32px;
  }
}
</style>