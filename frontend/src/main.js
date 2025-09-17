import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import './assets/main.css';
import './assets/styles/shared.css';
import './assets/styles/filters-pagination.css';
import './assets/styles/manage.css';
import { AUTH_URL } from './js/api';

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
          const refreshRes = await _fetch(`${AUTH_URL}/refresh`, {
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
        } catch {
            return new Response(null, { status: 500, statusText: 'Fetch error suppressed' });}
      }
  
      return response;
    };
}
  
setupFetchInterceptor();
  

const app = createApp(App);
app.use(router);
app.mount('#app');
