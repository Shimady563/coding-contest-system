<template>
  <div class="floating-wrapper">
    <div class="floating-label">
      <component
        :is="type === 'textarea' ? 'textarea' : 'input'"
        :type="type !== 'textarea' ? type : undefined"
        :value="modelValue"
        :id="id"
        :name="name"
        :required="required"
        :placeholder="placeholder"
        :autocomplete="autocomplete"
        :class="{ 'input-error': error }"
        @input="$emit('update:modelValue', $event.target.value)"
      ></component>
      <label :for="id">{{ label }}</label>
    </div>
    <slot />
  </div>
</template>

<script>
export default {
  props: {
    modelValue: String,
    type: { type: String, default: 'text' },
    id: String,
    name: String,
    label: String,
    required: Boolean,
    placeholder: String,
    autocomplete: String,
    error: Boolean,
  },
  emits: ['update:modelValue'],
}
</script>

<style scoped>
.floating-label {
  position: relative;
  margin-bottom: 1.8rem;
}

.floating-label input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #ccc;
  border-radius: 8px;
  outline: none;
  transition: all 0.25s ease;
  background: #fff;
  font-size: 15px;
  color: #333;
  box-sizing: border-box;
}

.floating-label textarea {
  width: 100%;
  min-height: 100px;
  padding: 12px 14px;
  border: 1px solid #ccc;
  border-radius: 8px;
  outline: none;
  transition: all 0.25s ease;
  font-size: 15px;
  color: #333;
  box-sizing: border-box;
  resize: vertical;
}

.floating-label label {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(0, 0, 0, 0.5);
  pointer-events: none;
  padding: 0 4px;
  font-size: 15px;
  transition: all 0.25s ease;
  background: transparent;
}

.floating-label input:focus + label,
.floating-label input:not(:placeholder-shown) + label,
.floating-label textarea:focus + label,
.floating-label textarea:not(:placeholder-shown) + label,
.floating-label.active label,
.floating-label:focus-within label {
  top: -8px;
  left: 10px;
  font-size: 12px;
  color: #2f80ed;
  background: #fff;
  transform: none;
}

.floating-label input:focus,
.floating-label textarea:focus {
  border-color: #2f80ed;
  box-shadow: 0 0 0 2px rgba(47, 128, 237, 0.12);
}

.floating-label input:invalid:not(:focus):not(:placeholder-shown),
.floating-label textarea:invalid:not(:focus):not(:placeholder-shown) {
  border-color: #f44336;
}

.floating-wrapper {
  display: flex;
  flex-direction: column;
}

.multiselect-floating {
  position: relative;
}

.multiselect-floating label {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  color: rgba(0, 0, 0, 0.5);
  pointer-events: none;
  padding: 0 4px;
  font-size: 15px;
  transition: all 0.25s ease;
  background: #fff;
  z-index: 3;
}

.multiselect-floating.active label {
  top: -8px;
  left: 10px;
  font-size: 12px;
  color: #2f80ed;
  background: #fff;
  transform: none;
}

.multiselect-floating.active :deep(.multiselect__placeholder) {
  display: none;
}

.multiselect-floating :deep(.multiselect),
.multiselect-floating :deep(.multiselect__tags),
.multiselect-floating :deep(.multiselect__content-wrapper) {
  z-index: auto !important;
}

.input-error {
  border-color: #e74c3c !important;
  box-shadow: 0 0 0 2px rgba(231, 76, 60, 0.12);
}

.floating-label input.input-error + label,
.floating-label textarea.input-error + label {
  top: -8px;
  left: 10px;
  font-size: 12px;
  color: #2f80ed;
  background: #fff;
  transform: none;
}
</style>
