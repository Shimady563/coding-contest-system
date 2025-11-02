<template>
  <div class="page-container">
    <div class="page-header">
      <h1><i class="fas fa-users"></i> Управление группами</h1>
    </div>

    <form class="filters" @submit.prevent="onSearch">
      <div class="filter-group">
        <FloatingInput
            v-model="searchParams.name"
            id="groupName"
            name="groupName"
            label="Название"
            type="text"
            class="text-input" 
            placeholder=""
          />
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
            <FloatingInput
              v-model="newGroupName"
              id="newGroupName"
              name="newGroupName"
              label="Название"
              type="text"
              class="form-input" 
              placeholder=""
            />
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
          <FloatingInput
              v-model="editingGroup.name"
              id="editGroupName"
              name="editGroupName"
              label="Название"
              type="text"
              class="form-input" 
              placeholder=""
            />
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
import FloatingInput from "@/components/FloatingInput.vue";

export default {
  components: { 
    ConfirmDialog,
    FloatingInput,
  },
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
        this.$root.notify("Ошибка при загрузке групп", 'error');
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
        this.$root.notify('Введите название группы', 'error');
        return;
      }
      this.creating = true;
      try {
        await createGroup({ name: this.newGroupName.trim() });
        this.$root.notify('Группа создана', 'success');
        this.closeCreateModal();
        this.fetchGroups();
      } catch {
        this.$root.notify('Ошибка при создании группы', 'error');
      } finally {
        this.creating = false;
      }
    },
    async deleteGroup(id) {
      try {
        await deleteGroup(id);
        this.$root.notify('Группа удалена', 'success');
        this.fetchGroups();
      } catch {
        this.$root.notify('Ошибка при удалении группы', 'error');
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
        this.$root.notify('Введите название группы', 'error');
        return;
      }
      this.updating = true;
      try {
        await updateGroupById(this.editingGroup.id, { name: this.editingGroup.name.trim() });
        this.$root.notify('Группа обновлена', 'success');
        this.closeEditModal();
        this.fetchGroups();
      } catch (err) {
        if (err.response?.status === 409) {
          this.$root.notify('Группа с таким названием уже существует', 'error');
        } else {
          this.$root.notify('Ошибка при обновлении группы', 'error');
        }
      } finally {
        this.updating = false;
      }
    },
  }
};
</script>

<style scoped>
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

.id-col { 
  min-width: 80px; 
  color: #7f8c8d; 
}
.name-col { min-width: 200px; }
.actions-col { min-width: 120px; }
.action-buttons { 
  display: flex; 
  gap: 8px; 
}

.modal-dialog, .modal-header,
.modal-body, .modal-footer {
  background: white !important;
}
</style>