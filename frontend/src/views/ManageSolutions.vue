<template>
  <div class="page-container">
    <div class="page-header">
      <h1><i class="fas fa-check-circle"></i> Решения студентов</h1>
    </div>

    <form class="filters" @submit.prevent="fetchSolutions">
      <div
        class="filter-group floating-label multiselect-floating"
        :class="{ active: selectedStatus || $refs.statusSelect?.isOpen }"
      >
        <div class="custom-multiselect full-width">
          <multiselect
            id="statusSelect"
            name="statusSelect"
            ref="statusSelect"
            v-model="selectedStatus"
            :options="statuses"
            :searchable="true"
            :show-labels="false"
            placeholder=""
            label="name"
            track-by="name"
            :append-to-body="true"
            open-direction="below"
            @select="forceCloseSelect('statusSelect')"
          />
        </div>
        <label for="statusSelect">Статус</label>
      </div>

      <div
        class="filter-group floating-label multiselect-floating"
        :class="{ active: selectedTask || $refs.taskSelect?.isOpen }"
      >
        <div class="custom-multiselect full-width">
          <multiselect
            id="taskSelect"
            name="taskSelect"
            ref="taskSelect"
            v-model="selectedTask"
            :options="tasks"
            track-by="id"
            label="name"
            placeholder=""
            :searchable="true"
            :allow-empty="true"
            :multiple="false"
            :show-labels="false"
            :append-to-body="true"
            open-direction="below"
            @select="forceCloseSelect('taskSelect')"
          />
        </div>
        <label for="taskSelect">Задача</label>
      </div>

      <div
        class="filter-group floating-label multiselect-floating"
        :class="{ active: selectedUser || $refs.userSelect?.isOpen }"
      >
        <div class="custom-multiselect full-width">
          <multiselect
            id="userSelect"
            name="userSelect"
            ref="userSelect"
            v-model="selectedUser"
            :options="users"
            :custom-label="userLabel"
            track-by="id"
            placeholder=""
            :searchable="true"
            :allow-empty="true"
            :multiple="false"
            :show-labels="false"
            :append-to-body="true"
            open-direction="below"
            @select="forceCloseSelect('userSelect')"
          />
        </div>
        <label for="userSelect">Студент</label>
      </div>

      <div
        class="filter-group floating-label multiselect-floating"
        v-if="groups.length"
        :class="{ active: selectedGroup || $refs.groupSelect?.isOpen }"
      >
        <div class="custom-multiselect full-width">
          <multiselect
            id="groupSelect"
            name="groupSelect"
            ref="groupSelect"
            v-model="selectedGroup"
            :options="groups"
            label="name"
            track-by="id"
            placeholder=""
            :searchable="true"
            :allow-empty="true"
            :multiple="false"
            :show-labels="false"
            :append-to-body="true"
            open-direction="below"
            @select="forceCloseSelect('groupSelect')"
          />
        </div>
        <label for="groupSelect">Группа</label>
      </div>

      <div class="filter-group">
        <FloatingInput
          v-model="filters.startTime"
          id="startTime"
          name="startTime"
          label="С начала"
          type="datetime-local"
          class="text-input"
          placeholder=""
        />
      </div>

      <div class="filter-group">
        <FloatingInput
          v-model="filters.endTime"
          id="endTime"
          name="endTime"
          label="До"
          type="datetime-local"
          class="text-input"
          placeholder=""
        />
      </div>

      <div class="filter-actions">
        <button type="submit" class="apply-btn"><i class="fas fa-filter"></i> Применить</button>
        <button type="button" @click="resetFilters" class="reset-btn">
          <i class="fas fa-broom"></i> Сбросить
        </button>
      </div>
    </form>

    <div class="stats-container" v-if="solutions.content && solutions.content.length">
      <div class="stats">
        Показано {{ solutions.content.length }} из {{ solutions.page.totalElements }} решений
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="spinner"></div>
      <span>Загрузка данных...</span>
    </div>

    <div v-else-if="!solutions.content || !solutions.content.length" class="empty-state">
      <i class="fas fa-file-code"></i>
      <h3>Решения не найдены</h3>
      <p>Попробуйте изменить параметры поиска</p>
    </div>

    <div v-else class="table-container">
      <table class="solutions-table">
        <thead>
          <tr>
            <th class="task-col">Имя задачи</th>
            <th class="status-col">Статус</th>
            <th class="user-col">Пользователь</th>
            <th class="group-col">Группа</th>
            <th class="date-col">Отправлено</th>
            <th class="code-col">Код</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="solution in solutions.content" :key="solution.id">
            <td class="task-col">{{ solution.taskName }}</td>
            <td class="status-col">
              <span :class="getStatusClass(solution.status)">{{ solution.status }}</span>
            </td>
            <td class="user-col">
              {{ solution.user ? solution.user.firstName + ' ' + solution.user.lastName : '-' }}
            </td>
            <td class="group-col">{{ solution.user?.groupName || '-' }}</td>
            <td class="date-col">{{ formatDate(solution.submittedAt) }}</td>
            <td class="code-col">
              <button
                @click="showCodeModal(solution.code)"
                class="btn-icon code-btn"
                title="Показать код"
              >
                <i class="fas fa-code"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination-container" v-if="solutions.page && solutions.page.totalPages > 1">
      <div class="pagination-info">
        Страница {{ filters.pageNumber + 1 }} из {{ solutions.page.totalPages }}
      </div>
      <div class="pagination-controls">
        <button @click="prevPage" :disabled="filters.pageNumber === 0" class="pagination-btn">
          <i class="fas fa-chevron-left"></i>
        </button>
        <div class="page-indicator">
          Страница {{ filters.pageNumber + 1 }} из {{ solutions.page.totalPages }}
        </div>
        <button
          @click="nextPage"
          :disabled="filters.pageNumber >= solutions.page.totalPages - 1"
          class="pagination-btn"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>

    <div v-if="modalCode" class="modal-backdrop" @click.self="closeModal">
      <div class="modal-dialog">
        <div class="modal-header">
          <h3><i class="fas fa-code"></i> Код решения</h3>
          <div class="modal-actions">
            <button @click="copyCode(modalCode)" class="btn-icon copy-btn" title="Скопировать">
              <i class="fas fa-copy"></i>
            </button>
            <button @click="closeModal" class="btn-icon close-btn" title="Закрыть">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
        <div class="modal-body">
          <ReadOnlyCodeMirror :code="modalCode" language="text/x-java" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ReadOnlyCodeMirror from '@/components/ReadOnlyCodeMirror.vue'
import FloatingInput from '@/components/FloatingInput.vue'
import Multiselect from 'vue-multiselect'
import 'vue-multiselect/dist/vue-multiselect.min.css'
import { listSolutions, listTasks, listUsers, fetchGroups } from '@/js/manager'

export default {
  name: 'StudentSolutionsPage',
  components: {
    ReadOnlyCodeMirror,
    Multiselect,
    FloatingInput,
  },
  data() {
    return {
      solutions: {
        content: [],
        page: {},
      },
      filters: {
        status: '',
        userId: '',
        taskId: '',
        groupId: '',
        startTime: '',
        endTime: '',
        pageNumber: 0,
        pageSize: 10,
      },
      statuses: [
        { name: 'TIMED_OUT' },
        { name: 'COMPILE_ERROR' },
        { name: 'RUNTIME_ERROR' },
        { name: 'WRONG_ANSWER' },
        { name: 'ACCEPTED' },
        { name: 'INTERNAL_ERROR' },
      ],
      tasks: [],
      users: [],
      groups: [],
      selectedStatus: null,
      loading: false,
      visibleCode: null,
      modalCode: null,
      selectedUser: null,
      selectedTask: null,
      selectedGroup: null,
    }
  },
  methods: {
    async fetchSolutions() {
      this.loading = true
      try {
        const params = {
          pageNumber: this.filters.pageNumber,
          pageSize: this.filters.pageSize,
        }
        if (this.selectedStatus?.name) params.status = this.selectedStatus.name
        if (this.selectedUser) params.userId = this.selectedUser.id
        if (this.selectedTask) params.taskId = this.selectedTask.id
        if (this.selectedGroup) params.groupId = this.selectedGroup.id
        if (this.filters.startTime)
          params.startTime = new Date(this.filters.startTime).toISOString()
        if (this.filters.endTime) params.endTime = new Date(this.filters.endTime).toISOString()

        this.solutions = await listSolutions(params)
      } catch {
        this.$root.notify('Не удалось загрузить данные. Пожалуйста, попробуйте позже.', 'error')
      } finally {
        this.loading = false
      }
    },
    async fetchTasks() {
      try {
        const params = {
          name: '',
          pageSize: 1000000,
        }

        const data = await listTasks(params)
        this.tasks = data.content || []
      } catch {
        this.$root.notify('Не удалось загрузить список задач', 'error')
      }
    },
    async fetchUsers() {
      try {
        const data = await listUsers({
          role: 'ROLE_STUDENT',
          pageSize: 1000,
          pageNumber: 0,
        })
        this.users = data.content || []
      } catch {
        this.$root.notify('Не удалось загрузить список пользователей', 'error')
      }
    },
    async fetchGroups() {
      try {
        this.groups = (await fetchGroups()) || []
      } catch {
        this.$root.notify('Не удалось загрузить список групп', 'error')
      }
    },
    userLabel(user) {
      return `${user.firstName} ${user.lastName}`
    },
    resetFilters() {
      this.filters = {
        status: '',
        userId: '',
        taskId: '',
        groupId: '',
        startTime: '',
        endTime: '',
        pageNumber: 0,
        pageSize: 10,
      }
      this.selectedUser = null
      this.selectedTask = null
      this.selectedStatus = null
      this.selectedGroup = null
      this.fetchSolutions()
    },
    nextPage() {
      this.filters.pageNumber++
      this.fetchSolutions()
    },
    prevPage() {
      if (this.filters.pageNumber > 0) {
        this.filters.pageNumber--
        this.fetchSolutions()
      }
    },
    formatDate(date) {
      return new Date(date).toLocaleString()
    },
    copyCode(code) {
      navigator.clipboard
        .writeText(code)
        .then(() => {
          this.$root.notify('Код скопирован в буфер обмена', 'success')
        })
        .catch((err) => {
          console.error('Ошибка копирования:', err)
          this.$root.notify('Не удалось скопировать код', 'error')
        })
    },
    getStatusClass(status) {
      return {
        'status-badge': true,
        'status-accepted': status === 'ACCEPTED',
        'status-error': ['COMPILE_ERROR', 'RUNTIME_ERROR', 'INTERNAL_ERROR'].includes(status),
        'status-warning': ['TIMED_OUT', 'WRONG_ANSWER'].includes(status),
      }
    },
    showCodeModal(code) {
      this.modalCode = code
    },
    closeModal() {
      this.modalCode = null
    },
    forceCloseSelect(selectName) {
      setTimeout(() => {
        this.$refs[selectName]?.deactivate()
      }, 0)
    },
  },
  mounted() {
    this.fetchTasks()
    this.fetchUsers()
    this.fetchGroups()
    this.fetchSolutions()
  },
}
</script>

<style scoped>
.multiselect-floating :deep(.multiselect__content-wrapper) {
  z-index: 1000 !important;
}

.filters .multiselect-floating label {
  top: 21px;
}

.solutions-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.solutions-table th {
  background: #f8f9fa;
  color: #555;
  font-weight: 600;
  text-align: left;
  padding: 16px;
  border-bottom: 2px solid #e9ecef;
}

.solutions-table td {
  padding: 14px 16px;
  border-bottom: 1px solid #e9ecef;
  vertical-align: middle;
}

.solutions-table tr:hover td {
  background-color: #f8f9fa;
}

.task-col {
  min-width: 200px;
}
.user-col {
  min-width: 150px;
}
.group-col {
  min-width: 100px;
  white-space: nowrap;
}
.status-col {
  min-width: 120px;
}
.date-col {
  min-width: 180px;
  white-space: nowrap;
}
.code-col {
  min-width: 80px;
}

.status-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-accepted {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-error {
  background-color: #ffebee;
  color: #c62828;
}

.status-warning {
  background-color: #fff8e1;
  color: #f57f17;
}
</style>
