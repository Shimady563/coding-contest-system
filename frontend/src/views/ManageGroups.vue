<template>
  <div class="page-container">
    <div class="page-header">
      <h1><i class="fas fa-users"></i> Управление группами</h1>
    </div>

    <form class="filters" @submit.prevent="onSearch">
      <div class="filter-group floating-label">
        <input 
          type="text" 
          v-model="searchParams.name" 
          id="groupName"
          class="text-input" 
          placeholder=""
        />
        <label for="groupName">Название</label>
      </div>

      <div class="filter-actions">
        <button type="submit" class="apply-btn">
          <i class="fas fa-filter"></i> Применить
        </button>
        <button type="button" @click="resetSearch" class="reset-btn">
          <i class="fas fa-broom"></i> Сбросить
        </button>
        <button type="button" @click="openCreateModal" class="create-btn">
          <i class="fas fa-plus"></i> Создать группу
        </button>
      </div>
    </form>

    <div class="stats-container" v-if="groups.length">
      <div class="stats">
        Показано {{ groups.length }} из {{ totalElements }} групп
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <span>Загрузка данных...</span>
    </div>

    <div v-else-if="!groups.length" class="empty-state">
      <i class="fas fa-users-slash"></i>
      <h3>Группы не найдены</h3>
      <p>Попробуйте изменить параметры поиска</p>
    </div>

    <div v-else class="table-container">
      <table class="groups-table">
        <thead>
          <tr>
            <th class="id-col">№</th>
            <th class="name-col">Название</th>
            <th class="actions-col">Действия</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(group, index) in groups" :key="group.id">
            <td class="id-col">{{ currentPage * pageSize + index + 1 }}</td>
            <td class="name-col">{{ group.name }}</td>
            <td class="actions-col">
              <div class="action-buttons">
                <button @click="openEditModal(group)" class="btn-icon edit-btn" title="Редактировать">
                  <i class="fas fa-pencil-alt"></i>
                </button>
                <button @click="confirmDeleteGroup(group)" class="btn-icon delete-btn" title="Удалить">
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

    <div v-if="showCreateModal" class="modal-backdrop">
      <div class="modal-dialog">
        <div class="modal-header">
          <h3><i class="fas fa-plus"></i> Создать группу</h3>
        </div>
        <div class="modal-body">
          <div class="floating-label">
            <input 
              v-model="newGroupName" 
              id="newGroupName"
              class="form-input"
              placeholder=""
            >
            <label for="newGroupName">Название группы</label>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeCreateModal" class="btn-cancel">Отмена</button>
          <button @click="createGroup" class="btn-save" :disabled="creating">
            <span v-if="creating">Сохранение...</span>
            <span v-else>Создать</span>
          </button>
        </div>
      </div>
    </div>

    <div v-if="editingGroup" class="modal-backdrop">
      <div class="modal-dialog">
        <div class="modal-header">
          <h3><i class="fas fa-pencil-alt"></i> Редактирование группы</h3>
        </div>
        <div class="modal-body">
          <div class="floating-label">
            <input 
              v-model="editingGroup.name" 
              id="editGroupName"
              class="form-input"
              placeholder=""
            >
            <label for="editGroupName">Название группы</label>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closeEditModal" class="btn-cancel">Отмена</button>
          <button @click="updateGroup" class="btn-save" :disabled="updating">
            <span v-if="updating">Сохранение...</span>
            <span v-else>Сохранить</span>
          </button>
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
import { getGroupsPage, createGroup, deleteGroup, updateGroupById } from "@/js/manager";
import ConfirmDialog from "@/components/ConfirmDialog.vue";

export default {
  components: { ConfirmDialog },
  data() {
    return {
      groups: [],
      currentPage: 0,
      pageSize: 10,
      totalPages: 1,
      totalElements: 0,
      loading: false,
      creating: false,
      showCreateModal: false,
      newGroupName: '',
      searchParams: { name: '' },
      showConfirmDialog: false,
      confirmDialog: { title: '', message: '' },
      groupToDelete: null,
      editingGroup: null,
      updating: false,
    };
  },
  async created() {
    await this.fetchGroups();
  },
  methods: {
    async fetchGroups() {
      this.loading = true;
      try {
        const params = {
          pageNumber: this.currentPage,
          pageSize: this.pageSize,
          ...(this.searchParams.name ? { name: this.searchParams.name } : {})
        };

        const data = await getGroupsPage(params);

        this.groups = data.content || [];
        this.totalPages = data.totalPages ?? (data.page?.totalPages ?? 1);
        this.totalElements = data.totalElements ?? (data.page?.totalElements ?? this.groups.length);
      } catch {
        this.$toast?.error("Ошибка при загрузке групп");
      } finally {
        this.loading = false;
      }
    },
    onSearch() {
      this.currentPage = 0;
      this.fetchGroups();
    },
    resetSearch() {
      this.searchParams.name = '';
      this.currentPage = 0;
      this.fetchGroups();
    },
    nextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++;
        this.fetchGroups();
      }
    },
    prevPage() {
      if (this.currentPage > 0) {
        this.currentPage--;
        this.fetchGroups();
      }
    },
    openCreateModal() {
      this.newGroupName = '';
      this.showCreateModal = true;
    },
    closeCreateModal() {
      this.showCreateModal = false;
    },
    async createGroup() {
      if (!this.newGroupName.trim()) {
        this.$toast?.error('Введите название группы');
        return;
      }
      this.creating = true;
      try {
        await createGroup({ name: this.newGroupName.trim() });
        this.$toast?.success('Группа создана');
        this.closeCreateModal();
        this.fetchGroups();
      } catch {
        this.$toast?.error('Ошибка при создании группы');
      } finally {
        this.creating = false;
      }
    },
    async deleteGroup(id) {
      try {
        await deleteGroup(id);
        this.$toast?.success('Группа удалена');
        this.fetchGroups();
      } catch {
        this.$toast?.error('Ошибка при удалении группы');
      }
    },
    confirmDeleteGroup(group) {
      this.groupToDelete = group;
      this.confirmDialog = {
        title: 'Удаление группы',
        message: `Вы уверены, что хотите удалить группу "${group.name}"? Это действие нельзя отменить.`
      };
      this.showConfirmDialog = true;
    },
    async executeDelete() {
      if (!this.groupToDelete) return;
      await this.deleteGroup(this.groupToDelete.id);
      this.showConfirmDialog = false;
      this.groupToDelete = null;
    },
    cancelDelete() {
      this.showConfirmDialog = false;
      this.groupToDelete = null;
    },
    openEditModal(group) {
      this.editingGroup = { ...group };
    },
    closeEditModal() {
      this.editingGroup = null;
    },
    async updateGroup() {
      if (!this.editingGroup.name.trim()) {
        this.$toast?.error('Введите название группы');
        return;
      }
      this.updating = true;
      try {
        await updateGroupById(this.editingGroup.id, { name: this.editingGroup.name.trim() });
        this.$toast?.success('Группа обновлена');
        this.closeEditModal();
        this.fetchGroups();
      } catch (err) {
        if (err.response?.status === 409) {
          this.$toast?.error('Группа с таким названием уже существует');
        } else {
          this.$toast?.error('Ошибка при обновлении группы');
        }
      } finally {
        this.updating = false;
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

.filter-actions {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.apply-btn, .reset-btn, .create-btn {
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

.create-btn {
  background: #34d399;
  color: white;
}

.create-btn:hover {
  background: #10b981;
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

.groups-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.groups-table th {
  background: #f8f9fa;
  color: #555;
  font-weight: 600;
  text-align: left;
  padding: 16px;
  border-bottom: 2px solid #e9ecef;
}

.groups-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #e9ecef;
  vertical-align: middle;
}

.groups-table tr:hover td {
  background-color: #f8f9fa;
}

.id-col { min-width: 80px; color: #7f8c8d; }
.name-col { min-width: 200px; }
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
  background:  #f8f9fa;
  border-radius: 12px;
  width: 500px; 
  max-width: calc(100% - 40px); 
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0,0,0,0.2);
  animation: modalFadeIn 0.3s ease;
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
  background: #f8f9fa;
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
  background: #f8f9fa;;
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

  .apply-btn, .reset-btn, .create-btn {
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

  .groups-table {
    font-size: 13px;
  }

  .groups-table th,
  .groups-table td {
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