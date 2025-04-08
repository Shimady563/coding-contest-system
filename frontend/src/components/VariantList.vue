<template>
  <div class="variant-list">
    <h2>Варианты</h2>
    <button class="add-btn" @click="addVariant">➕ Добавить вариант</button>
    <VariantItem v-for="(variant, index) in modelValue" :key="index" :variant="variant" :index="index" />
  </div>
</template>

<script>
import VariantItem from './VariantItem.vue'
export default {
  props: ['modelValue'],
  components: { VariantItem },
  methods: {
    addVariant() {
      this.$emit('update:modelValue', [...this.modelValue, { title: `Вариант ${this.modelValue.length + 1}`, tasks: [] }])
    }
  }
}
</script>

<style scoped>
.variant-list {
  background: #f7f9fa;
  padding: 20px;
  border-radius: 8px;
}
.add-btn {
  margin-bottom: 15px;
  padding: 6px 12px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.add-btn:hover {
  background: #218838;
}
</style>
