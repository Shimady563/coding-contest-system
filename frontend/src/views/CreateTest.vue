<template>
  <div class="create-test">
    <h1>Создание контрольной</h1>
    <TestInfoForm v-model="testInfo" />
    <VariantList v-model="variants" />
    <button class="save-btn" @click="saveTest">Сохранить контрольную</button>
  </div>
</template>

<script>
import TestInfoForm from '../components/TestInfoForm.vue'
import VariantList from '../components/VariantList.vue'

export default {
  components: { TestInfoForm, VariantList },
  data() {
    return {
      testInfo: {
        name: '',
        description: '',
        group: '',
        startTime: '',
        endTime: ''
      },
      variants: []
    }
  },
  methods: {
    saveTest() {
      console.log({
        ...this.testInfo,
        variants: this.variants
      })
      alert('Контрольная успешно сохранена!')
    }
  }
}
</script>

<style scoped>
.create-test {
  max-width: 800px;
  margin: 30px auto;
  padding: 20px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}
.save-btn {
  margin-top: 20px;
  background: #007bff;
  color: #fff;
  padding: 10px 16px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}
.save-btn:hover {
  background: #0056b3;
}
</style>
