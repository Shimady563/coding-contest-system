import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import './assets/main.css';
import './assets/styles/shared.css';
import './assets/styles/filters-pagination.css';
import './assets/styles/manage.css';
import { refreshAuth, logoutUser } from './js/auth';

async function initAuth() {
  try {
    await refreshAuth();
  } catch {
}
}

function setupFetchInterceptor() {
  const _fetch = window.fetch.bind(window);

  window.fetch = async (input, init = {}) => {
    init = {
      ...init,
      credentials: 'include',
      headers: {
        ...(init.headers || {}),
        'Content-Type': 'application/json',
      },
    };

    let response = await _fetch(input, init);

     if (response.status === 401) {
      const url = typeof input === 'string' ? input : input.url;

      if (url.includes('/logout') || url.includes('/refresh') || url.includes('/login')) {
        return response;
      }

      try {
        const refreshed = await refreshAuth();

        if (!refreshed) throw new Error('Refresh не удался');

        return await _fetch(input, init);
      } catch (err) {
        console.error('Ошибка обновления сессии:', err);

        await logoutUser();

        router.push({ name: 'Login' }).catch(() => {});
        return Promise.reject(err);
      }
    }

    return response;
  };
}

(async () => {
  await initAuth();
  setupFetchInterceptor();

  const app = createApp(App);
  app.use(router);
  app.mount('#app');
})();