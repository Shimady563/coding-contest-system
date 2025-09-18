<template>
    <div class="access-denied-container">
      <div class="access-denied-card">
        <div class="icon-container warning-icon">⚠</div>
        <h1>Доступ запрещён</h1>
        <p class="message-access-denied">
          Контрольная работа ещё не началась или уже завершена. Пожалуйста, вернитесь позже.
        </p>
        <div class="actions">
          <router-link to="/contests" class="btn primary-btn">К контрольным</router-link>
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