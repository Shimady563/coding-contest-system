<template>
    <div class="access-denied-container">
      <div class="access-denied-card">
        <div class="icon-container warning-icon">⚠</div>
        <h1>Доступ запрещён</h1>
        <p class="message">
          Контрольная работа ещё не началась или уже завершена. Пожалуйста, вернитесь позже.
        </p>
        <div class="actions">
          <router-link to="/" class="btn primary-btn">На главную</router-link>
        </div>
      </div>
    </div>
</template>
  
<script>
import '@/assets/styles/access-denied.css';

export default {
    name: 'AccessDeniedPage',
  };
</script>