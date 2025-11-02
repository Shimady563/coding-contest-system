<template>
  <div class="page">
    <div class="card">
      <h1>{{ isEdit ? 'Редактирование контрольной работы' : 'Создание контрольной работы' }}</h1>

      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <span>Загрузка данных...</span>
      </div>

      <div v-else>
        <FloatingInput
          id="name"
          name="name"
          label="Название"
          v-model="contest.name"
          placeholder=""
          type="text"
          :error="!contest.name && submitted">
          <span v-if="!contest.name && submitted" class="error-message">Это поле обязательно</span>
        </FloatingInput>
        <FloatingInput
          id="description"
          name="description"
          label="Описание"
          v-model="contest.description"
          placeholder=""
          type="textarea"
          :error="!contest.description && submitted">
          <span v-if="!contest.description && submitted" class="error-message">Это поле обязательно</span>
        </FloatingInput>
        
        <div
          class="floating-label multiselect-floating"
          :class="{ active: selectedGroup || $refs.groupSelect?.isOpen || (!selectedGroup && submitted) }"
        >
          <div 
          class="custom-multiselect" 
          :class="{ invalid: !selectedGroup && submitted }"
          >
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
          <span v-if="!selectedGroup && submitted" class="error-message">Выберите группу</span>
        </div>

        <div class="form-row">
          <FloatingInput
            v-model="contest.startTime"
            id="start"
            name="start"
            label="Начало"
            type="datetime-local"
            :error="!contest.startTime && submitted"
            placeholder="">
            <span v-if="!contest.startTime && submitted" class="error-message">Выберите время начала</span>
          </FloatingInput>
          <FloatingInput
            v-model="contest.endTime"
            id="end"
            name="end"
            label="Окончание"
            type="datetime-local"
            :error="!contest.startTime && submitted"
            placeholder="">
            <span v-if="!contest.endTime && submitted" class="error-message">Выберите время начала</span>
          </FloatingInput>
        </div>

        <div class="variants-section">
          <h2>Варианты</h2>
          <span v-if="variants.length === 0 && submitted" class="error-message">Добавьте хотя бы один вариант</span>

          <div v-for="(variant, index) in variants" :key="variant.id || index" class="variant-block">
            <FloatingInput
              id="name"
              name="name"
              label="Название варианта"
              v-model="variant.name"
              type="text"
              placeholder=""
              class="variant-input"
              :error="!variant.name && submitted">
              <span v-if="!variant.name && submitted" class="error-message">Название обязательно</span>
            </FloatingInput>

            <div class="tasks-section">
              <TaskSelector :allTasks="tasks" @add-task="task => addTaskToVariant(index, task)" />
              <div class="selected-tasks">
                <span v-for="t in variant.tasks" :key="t.id" class="task-chip">
                  {{ t.name }}
                  <button
                    v-if="!isEdit"
                    type="button"
                    class="remove-task"
                    @click="removeTaskFromVariant(index, t.id)"
                  >
                    ×
                  </button>
                </span>
              </div>
              <span v-if="variant.tasks.length === 0 && submitted" class="error-message">Добавьте хотя бы одно задание</span>
            </div>

            <button class="btn btn-danger" type="button" @click="confirmRemoveVariant(index)">
              <i class="fas fa-trash"></i> Удалить вариант
            </button>
          </div>
        </div>

        <div class="form-actions">
          <button class="btn btn-secondary" type="button" @click="addVariant">
            <i class="fas fa-plus"></i> Добавить вариант
          </button>
          <button class="btn btn-primary" :disabled="saving" @click="onSubmit">
            <span v-if="saving"><i class="fas fa-spinner fa-spin"></i> Сохранение...</span>
            <span v-else><i class="fas fa-save"></i> {{ isEdit ? 'Сохранить изменения' : 'Сохранить контрольную' }}</span>
          </button>
        </div>
      </div>

      <ConfirmDialog
        v-if="showConfirmDialog"
        :title="confirmDialog.title"
        :message="confirmDialog.message"
        @confirm="removeDelete"
        @cancel="cancelDelete"
      />
    </div>
  </div>
</template>

<script>
import TaskSelector from '@/components/TaskSelector.vue';
import ConfirmDialog from "@/components/ConfirmDialog.vue"
import FloatingInput from '@/components/FloatingInput.vue';
import { fetchGroups } from '@/js/manager';
import { getContest, updateContest, createContest, listTasks, createContestVersion, getContestVersionsByContest, 
  deleteContestVersion } from '@/js/manager';
import Multiselect from "vue-multiselect";
import "vue-multiselect/dist/vue-multiselect.min.css";

export default {
  components: { 
    TaskSelector,
    Multiselect,
    ConfirmDialog,
    FloatingInput,
  },
  props: { id: { type: String, required: false } },
  data() {
    return {
      contest: { name: '', description: '', groupId: '', startTime: '', endTime: '' },
      groups: [],
      selectedGroup: null,
      tasks: [],
      variants: [],
      submitted: false,
      loading: false,
      saving: false,
      nextVariantNumber: 1,
      variantsToDelete: [],
      variantToDeleteIndex: null, 
      showConfirmDialog: false, 
      confirmDialog: { title: '', message: '' }   
    };
  },
  computed: { 
    isEdit() { return !!this.id; } 
  },
  watch: {
    selectedGroup(newGroup) {
      if (newGroup) {
        this.contest.groupId = newGroup.id;
      } else {
        this.contest.groupId = '';
      }
    }
  },
  async mounted() {
    await this.loadGroups();
    await this.loadTasks();
    
    if (this.isEdit) {
      await this.loadContest();
      await this.loadContestVersions();
    }
  },
  methods: {
    async loadGroups() {
      try { 
        this.groups = await fetchGroups(); 
      } catch (_) { 
        this.$root.notify('Ошибка при загрузке групп', 'error'); 
      }
    },
    async loadTasks() {
      try { 
        const params = {
          name: "",
          pageSize: 1000000,
        };
        
        const data = await listTasks(params);
        this.tasks = data.content || []; 
      } catch (_) {}
    },
    async loadContest() {
      this.loading = true;
      try {
        const data = await getContest(this.id);
        const format = (d) => { 
          const date = new Date(d); 
          const y = date.getFullYear(); 
          const m = String(date.getMonth() + 1).padStart(2, '0'); 
          const day = String(date.getDate()).padStart(2, '0'); 
          const h = String(date.getHours()).padStart(2, '0'); 
          const min = String(date.getMinutes()).padStart(2, '0'); 
          return `${y}-${m}-${day}T${h}:${min}`; 
        };
        
        this.contest = { 
          id: data.id, 
          name: data.name || '', 
          description: data.description || '', 
          groupId: data.groupId || '', 
          startTime: data.startTime ? format(data.startTime) : '', 
          endTime: data.endTime ? format(data.endTime) : '' 
        };

        if (this.contest.groupId) {
          this.selectedGroup = this.groups.find(group => group.id === this.contest.groupId) || null;
        }
      } catch (_) {
        this.$root.notify('Не удалось загрузить данные контрольной', 'error');
        this.$router.push('/manage-contests');
      } finally { 
        this.loading = false; 
      }
    },
    async loadContestVersions() {
      try {
        const versions = await getContestVersionsByContest(this.id);

        this.variants = versions.map(v => {
          const tasks = (v.taskIds || [])
            .map(id => this.tasks.find(t => t.id === id))
            .filter(Boolean);

          return {
            id: v.id,
            name: v.name,
            tasks
          };
        });

        if (this.variants.length > 0) {
          this.nextVariantNumber = Math.max(...this.variants.map(v => {
            const match = v.name.match(/Вариант (\d+)/);
            return match ? parseInt(match[1]) + 1 : this.variants.length + 1;
          }));
        }
      } catch (error) {
        console.error("Ошибка при загрузке вариантов:", error);
        this.$root.notify("Не удалось загрузить варианты контрольной", "error");
      }
    },
    addVariant() { 
      this.variants.push({ 
        id: null,
        name: `Вариант ${this.nextVariantNumber++}`, 
        tasks: [] 
      }); 
    },
    confirmRemoveVariant(index) {
      this.variantToDeleteIndex = index;
      this.showConfirmDialog = true;
      this.confirmDialog = {
        title: "Подтверждение удаления",
        message: "Вы уверены, что хотите удалить этот вариант?"
      };
    },
    async removeDelete() {
      const index = this.variantToDeleteIndex;
      const variant = this.variants[index];

      if (variant.id) {
        this.variantsToDelete.push(variant.id);
      }
      this.variants.splice(index, 1);

      this.closeConfirmDialog();
    },
    cancelDelete() {
      this.closeConfirmDialog();
    },
    closeConfirmDialog() {
      this.variantToDeleteIndex = null;
      this.showConfirmDialog = false;
      this.confirmDialog = { title: "", message: "" };
    },
    addTaskToVariant(index, task) { 
      const v = this.variants[index]; 
      if (!v.tasks.find(t => t.id === task.id)) {
        v.tasks.push({...task});
      }
    },
    removeTaskFromVariant(variantIndex, taskId) {
      const variant = this.variants[variantIndex];
      variant.tasks = variant.tasks.filter(t => t.id !== taskId);
    },
    validate() {
      this.submitted = true;
      if (!this.contest.name.trim()) { this.$root.notify('Введите название контрольной', 'error'); return false; }
      if (!this.contest.description.trim()) { this.$root.notify('Введите описание контрольной', 'error'); return false; }
      if (!this.selectedGroup) { this.$root.notify('Выберите группу', 'error'); return false; }
      if (!this.contest.startTime) { this.$root.notify('Выберите время начала', 'error'); return false; }
      if (!this.contest.endTime) { this.$root.notify('Выберите время окончания', 'error'); return false; }
      if (new Date(this.contest.startTime) >= new Date(this.contest.endTime)) { this.$root.notify('Время окончания должно быть позже времени начала', 'error'); return false; }
      
      if (this.variants.length === 0) { this.$root.notify('Добавьте хотя бы один вариант', 'error'); return false; }
      for (const [i, v] of this.variants.entries()) {
        if (!v.name) { this.$root.notify(`Укажите название для варианта ${i + 1}`, 'error'); return false; }
        if (v.tasks.length === 0) { this.$root.notify(`Добавьте задания для варианта ${i + 1}`, 'error'); return false; }
      }
      
      return true;
    },
    async onSubmit() {
      if (!this.validate()) return;
      this.saving = true;
      try {
        const payload = { 
          name: this.contest.name.trim(), 
          description: this.contest.description.trim(), 
          groupId: Number(this.contest.groupId), 
          startTime: this.contest.startTime, 
          endTime: this.contest.endTime 
        };
        
        if (this.isEdit) {
          await updateContest(this.id, payload);
          
          for (const variantId of this.variantsToDelete) {
            try {
              await deleteContestVersion(variantId);
            } catch (error) {
              console.error('Ошибка при удалении варианта:', error);
              this.$root.notify('Не удалось удалить один из вариантов', 'error');
            }
          }
          
          for (const variant of this.variants) {
            const taskIds = variant.tasks.map(t => Number(t.id));
            
            if (!variant.id) {
              await createContestVersion({
                name: variant.name,
                contestId: Number(this.id),
                taskIds
              });
            }
          }
          
          this.$root.notify('Контрольная успешно обновлена!', 'success');
          this.$router.push('/manage-contests');
        } else {
          const contest = await createContest(payload);
          const contestId = contest.id;
          
          for (const v of this.variants) {
            await createContestVersion({
              name: v.name,
              contestId,
              taskIds: v.tasks.map(t => Number(t.id))
            });
          }
          
          this.$root.notify('Контрольная работа успешно создана!', 'success');
          this.$router.push('/manage-contests');
        }
      } catch (error) {
        console.error('Ошибка сохранения:', error);
        this.$root.notify('Не удалось сохранить изменения', 'error');
      } finally { 
        this.saving = false; 
      }
    }
  }
};
</script>

<style scoped>
.card {
  overflow: visible !important;
  position: relative;
  background: #ffffff;
  padding: 1.5rem;
  border-radius: 16px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
}

.card > :deep(.floating-label),
.card > .multiselect-floating,
.card > .form-row {
  margin-bottom: 1.25rem;
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
}

.form-row {
  gap: 1.5rem;
  margin-top: 1rem;
}

.floating-label,
.multiselect-floating {
  margin-top: 0 !important;
}

.multiselect-floating :deep(.multiselect__content-wrapper) {
  z-index: 1000 !important; 
}

.form-row {
  margin-top: 0;
  margin-bottom: 1.25rem;
}

.input-error,
.custom-multiselect.invalid :deep(.multiselect__tags) {
  border-color: #e74c3c !important;
  box-shadow: 0 0 0 2px rgba(231, 76, 60, 0.15) !important;
}

.input-error:focus {
  border-color: #e74c3c !important;
  box-shadow: 0 0 0 2px rgba(231, 76, 60, 0.15) !important;
}

.error-message {
  margin-top: -25px;
  margin-bottom: 1.25rem;
}

.variant-input{
  margin-top: 25px;
}

.variants-section {
  margin-top: 2rem;
}

.variant-block {
  background: #ffffff; 
  border: 1px solid #e5e9f0;
  border-radius: 12px;
  padding: 1rem 1.25rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.04);
}

.tasks-section{
  padding-bottom: 10px;
}

.task-chip {
  display: inline-flex;
  align-items: center;
  background: #2f80ed;
  color: white;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 14px;
  margin: 4px;
}

.remove-task {
  background: transparent;
  border: none;
  color: white;
  margin-left: 6px;
  cursor: pointer;
  font-weight: bold;
}

.remove-task:hover {
  color: #ff4d4d;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 2rem;
  gap: 1rem;
}

.btn {
  padding: 12px 18px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.btn-secondary {
  background: #2f80ed;
  color: #fff;
}

.btn-secondary:hover {
  background: #256bcc;
}

.btn-danger {
  background: #e74c3c;
  color: #fff;
}

.btn-danger:hover {
  background: #cf3b2c;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 1rem;
  }

  .card {
    padding: 1rem;
  }
}
</style>