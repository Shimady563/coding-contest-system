import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import './assets/main.css';

function setupFetchInterceptor() {
    const _fetch = window.fetch.bind(window);
  
    window.fetch = async (input, init = {}) => {
      init = {
        ...init,
        credentials: 'include',
        headers: {
          ...(init.headers || {}),
          'Content-Type': 'application/json',
        }
      };
  
      let response = await _fetch(input, init);
  
      if (response.status === 401) {
        try {
          const refreshRes = await _fetch('http://localhost:8081/api/v1/auth/refresh', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' }
          });
  
          if (refreshRes.ok) {
            return _fetch(input, init);
          } else {
            await router.push({ name: 'Login' });
            return Promise.reject(new Error('Сессия истекла. Перенаправление на вход.'));
          }
        } catch (e) {
            return new Response(null, { status: 500, statusText: 'Fetch error suppressed' });}
      }
  
      return response;
    };
}
  
setupFetchInterceptor();
  

const app = createApp(App);
app.use(router);
app.use(store);
app.mount('#app');
