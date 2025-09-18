<template>
  <div class="manage-groups-container">
    <div class="header">
      <h1><i class="fas fa-users"></i> Управление группами</h1>
      <div class="stats" v-if="groups.length">
        Показано {{ groups.length }} из {{ totalElements }} групп
      </div>
    </div>

    <form class="filters" @submit.prevent="onSearch">
      <div class="filter-group">
        <label>
          <span>Название:</span>
          <input type="text" v-model="searchParams.name" class="text-input" placeholder="Поиск по названию">
        </label>
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
          <div class="form-group">
            <label>Название группы</label>
            <input v-model="newGroupName" class="form-input" placeholder="Введите название группы">
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
import { getGroupsPage, createGroup, deleteGroup } from "@/js/manager";
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
      groupToDelete: null
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
    }
  }
};
</script>

<style scoped>
.manage-groups-container {
  max-width: 1152px;
  margin: 20px auto;
  padding: 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.stats {
  font-size: 14px;
  color: #7f8c8d;
}

.filters {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e0e0e0;
}

.filter-group {
  display: flex;
  flex-direction: column;
}

.filter-group label span {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  margin-bottom: 6px;
  display: block;
}

.text-input {
  padding: 10px 10px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
  background: white;
  width: 100%;
  box-sizing: border-box;
}

.text-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
}

.filter-actions {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}

.apply-btn,
.reset-btn,
.create-btn {
  padding: 10px 16px;
  border-radius: 8px;
  font-weight: 500;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  align-self: flex-end;
  height: 40px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.apply-btn {
  background-color: #3498db;
  color: white;
  border: none;
}

.apply-btn:hover {
  background-color: #2980b9;
}

.reset-btn {
  background-color: transparent;
  color: #7f8c8d;
  border: 1px solid #ddd;
}

.reset-btn:hover {
  background-color: #f1f1f1;
}

.create-btn {
  background-color: #34d399;
  color: white;
  border: none;
}

.create-btn:hover {
  background-color: #10b981;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
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
  border: 1px solid #eee;
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
  padding: 14px 16px;
  border-bottom: 2px solid #eee;
}

.groups-table td {
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
  vertical-align: middle;
}

.groups-table tr:hover td {
  background-color: #f8f9fa;
}

.id-col {
  min-width: 80px;
  color: #7f8c8d;
}

.name-col {
  min-width: 200px;
}

.actions-col {
  min-width: 120px;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
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

.delete-btn {
  color: #e74c3c;
}

.delete-btn:hover {
  background-color: rgba(231, 76, 60, 0.1);
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.pagination-info {
  font-size: 14px;
  color: #7f8c8d;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.pagination-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #ddd;
  background: white;
  color: #333;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #f8f9fa;
}

.page-indicator {
  font-size: 0.9rem;
  color: #555;
}

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(3px);
}

.modal-dialog {
  background: white;
  border-radius: 12px;
  width: 420px;
  max-width: calc(100% - 40px);
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  animation: modalFadeIn 0.3s ease;
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background: white;
  z-index: 10;
}

.modal-body {
  padding: 1.5rem;
  box-sizing: border-box;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  color: #555;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  box-sizing: border-box;
  max-width: 100%;
}

.form-input:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
  outline: none;
}

.modal-footer {
  padding: 1.5rem;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  position: sticky;
  bottom: 0;
  background: white;
}

.btn-cancel, .btn-save {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
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
  border: none;
}

.btn-save:hover {
  background-color: #27ae60;
}

@media (max-width: 768px) {
  .manage-groups-container {
    padding: 15px;
  }

  .filters {
    grid-template-columns: 1fr;
    padding: 15px;
    gap: 12px;
  }

  .filter-actions {
    flex-direction: column;
    gap: 8px;
  }

  .apply-btn,
  .reset-btn,
  .create-btn {
    width: 100%;
  }
}
</style>
