<template>
  <div class="info-form">
    <div class="form-group">
      <label>Название:</label>
      <input v-model="form.name" type="text" placeholder="Название контрольной" />
    </div>

    <div class="form-group">
      <label>Описание:</label>
      <textarea v-model="form.description" placeholder="Описание"></textarea>
    </div>

    <div class="form-group">
      <label>Группа:</label>
      <select v-model="form.group">
        <option disabled value="">Выберите группу</option>
        <option>Группа 101</option>
        <option>Группа 202</option>
        <option>Группа 303</option>
      </select>
    </div>

    <div class="form-group">
      <label>Начало:</label>
      <input v-model="form.startTime" type="datetime-local" />
    </div>

    <div class="form-group">
      <label>Конец:</label>
      <input v-model="form.endTime" type="datetime-local" />
    </div>
  </div>
</template>

<script>
export default {
  props: ['modelValue'],
  computed: {
    form: {
      get() { return this.modelValue },
      set(val) { this.$emit('update:modelValue', val) }
    }
  }
}
</script>

<style scoped>
.info-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 30px;
}
.form-group {
  display: flex;
  flex-direction: column;
}
input, textarea, select {
  padding: 8px;
  border-radius: 6px;
  border: 1px solid #ccc;
}
</style>
