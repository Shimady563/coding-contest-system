<template>
  <div class="page">
    <div class="card">
      <h1>{{ isEdit ? 'Редактирование контрольной работы' : 'Создание контрольной работы' }}</h1>

      <div v-if="loading" class="loading-container">
        <div class="spinner"></div>
        <span>Загрузка данных...</span>
      </div>
      <div v-else>
        <div class="form-group">
          <label for="name">Название <span class="required">*</span></label>
          <input id="name" v-model="contest.name" type="text" placeholder="Введите название" :class="{ 'invalid': !contest.name && submitted }" />
          <span v-if="!contest.name && submitted" class="error-message">Это поле обязательно</span>
        </div>

        <div class="form-group">
          <label for="description">Описание <span class="required">*</span></label>
          <textarea id="description" v-model="contest.description" placeholder="Краткое описание контрольной" :class="{ 'invalid': !contest.description && submitted }" />
          <span v-if="!contest.description && submitted" class="error-message">Это поле обязательно</span>
        </div>

        <div class="form-group">
          <label for="group">Группа <span class="required">*</span></label>
          <select id="group" v-model="contest.groupId" :class="{ 'invalid': !contest.groupId && submitted }">
            <option disabled value="">-- Выберите группу --</option>
            <option v-for="group in groups" :key="group.id" :value="group.id">{{ group.name }}</option>
          </select>
          <span v-if="!contest.groupId && submitted" class="error-message">Выберите группу</span>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="start">Начало <span class="required">*</span></label>
            <input id="start" type="datetime-local" v-model="contest.startTime" :class="{ 'invalid': !contest.startTime && submitted }" />
            <span v-if="!contest.startTime && submitted" class="error-message">Выберите время начала</span>
          </div>
          <div class="form-group">
            <label for="end">Окончание <span class="required">*</span></label>
            <input id="end" type="datetime-local" v-model="contest.endTime" :class="{ 'invalid': !contest.endTime && submitted }" />
            <span v-if="!contest.endTime && submitted" class="error-message">Выберите время окончания</span>
          </div>
        </div>

        <div v-if="!isEdit" class="form-group">
          <h2>Варианты <span class="required">*</span></h2>
          <span v-if="variants.length === 0 && submitted" class="error-message">Добавьте хотя бы один вариант</span>

          <div v-for="(variant, index) in variants" :key="index" class="testcase">
            <div class="form-group">
              <label>Название варианта <span class="required">*</span></label>
              <input v-model="variant.name" type="text" />
            </div>
            <div class="form-group">
              <label>Задания <span class="required">*</span></label>
              <TaskSelector :allTasks="tasks" @add-task="task => addTaskToVariant(index, task)" />
              <ul>
                <li v-for="t in variant.tasks" :key="t.id">{{ t.name }}</li>
              </ul>
            </div>
            <button class="btn btn-danger" type="button" @click="removeVariant(index)">Удалить вариант</button>
          </div>

          <button class="btn btn-secondary" type="button" @click="addVariant">➕ Добавить вариант</button>
        </div>

        <button class="btn btn-primary" :disabled="saving" @click="onSubmit">
          <span v-if="saving">⏳ Сохранение...</span>
          <span v-else>{{ isEdit ? '💾 Сохранить изменения' : '💾 Сохранить контрольную' }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import TaskSelector from '@/components/TaskSelector.vue';
import { fetchGroups } from '@/js/auth';
import { getContest, updateContest, createContest, listTasks, createContestVersion } from '@/js/api';

export default {
  components: { TaskSelector },
  props: { id: { type: String, required: false } },
  data() {
    return {
      contest: { name: '', description: '', groupId: '', startTime: '', endTime: '' },
      groups: [],
      tasks: [],
      variants: [],
      submitted: false,
      loading: false,
      saving: false,
      nextVariantNumber: 1,
    };
  },
  computed: { isEdit() { return !!this.id; } },
  async mounted() {
    await this.loadGroups();
    if (this.isEdit) {
      await this.loadContest();
    } else {
      await this.loadTasks();
    }
  },
  methods: {
    async loadGroups() {
      try { this.groups = await fetchGroups(); } catch (_) { this.$root.notify('Ошибка при загрузке групп', 'error'); }
    },
    async loadTasks() {
      try { const data = await listTasks(); this.tasks = data.content || []; } catch (_) {}
    },
    async loadContest() {
      this.loading = true;
      try {
        const data = await getContest(this.id);
        const format = (d) => { const date = new Date(d); const y = date.getFullYear(); const m = String(date.getMonth() + 1).padStart(2, '0'); const day = String(date.getDate()).padStart(2, '0'); const h = String(date.getHours()).padStart(2, '0'); const min = String(date.getMinutes()).padStart(2, '0'); return `${y}-${m}-${day}T${h}:${min}`; };
        this.contest = { id: data.id, name: data.name || '', description: data.description || '', groupId: data.groupId || '', startTime: data.startTime ? format(data.startTime) : '', endTime: data.endTime ? format(data.endTime) : '' };
      } catch (_) {
        this.$root.notify('Не удалось загрузить данные контрольной', 'error');
        this.$router.push('/manage-contests');
      } finally { this.loading = false; }
    },
    addVariant() { this.variants.push({ name: `Вариант ${this.nextVariantNumber++}`, tasks: [] }); },
    removeVariant(index) { this.variants.splice(index, 1); },
    addTaskToVariant(index, task) { const v = this.variants[index]; if (!v.tasks.find(t => t.id === task.id)) v.tasks.push(task); },
    validate() {
      this.submitted = true;
      if (!this.contest.name.trim()) { this.$root.notify('Введите название контрольной', 'error'); return false; }
      if (!this.contest.description.trim()) { this.$root.notify('Введите описание контрольной', 'error'); return false; }
      if (!this.contest.groupId) { this.$root.notify('Выберите группу', 'error'); return false; }
      if (!this.contest.startTime) { this.$root.notify('Выберите время начала', 'error'); return false; }
      if (!this.contest.endTime) { this.$root.notify('Выберите время окончания', 'error'); return false; }
      if (new Date(this.contest.startTime) >= new Date(this.contest.endTime)) { this.$root.notify('Время окончания должно быть позже времени начала', 'error'); return false; }
      if (!this.isEdit) {
        if (this.variants.length === 0) { this.$root.notify('Добавьте хотя бы один вариант', 'error'); return false; }
        for (const [i, v] of this.variants.entries()) {
          if (!v.name) { this.$root.notify(`Укажите название для варианта ${i + 1}`, 'error'); return false; }
          if (v.tasks.length === 0) { this.$root.notify(`Добавьте задания для варианта ${i + 1}`, 'error'); return false; }
        }
      }
      return true;
    },
    async onSubmit() {
      if (!this.validate()) return;
      this.saving = true;
      try {
        const payload = { name: this.contest.name.trim(), description: this.contest.description.trim(), groupId: Number(this.contest.groupId), startTime: this.contest.startTime, endTime: this.contest.endTime };
        if (this.isEdit) {
          await updateContest(this.id, payload);
          this.$root.notify('Контрольная успешно обновлена!', 'success');
          this.$router.push('/manage-contests');
        } else {
          const contest = await createContest(payload);
          const contestId = contest.id;
          for (const v of this.variants) {
            await createContestVersion({ name: v.name, contestId, taskIds: v.tasks.map(t => Number(t.id)) });
          }
          this.$root.notify('Контрольная работа успешно создана!', 'success');
          this.$router.push('/manage-contests');
        }
      } catch (e) {
        this.$root.notify('Не удалось сохранить изменения', 'error');
      } finally { this.saving = false; }
    }
  }
};
</script>

<style scoped>
/* Minimal component-specific overrides as needed */
</style>
