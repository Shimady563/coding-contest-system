import { config } from '../config';

export const AUTH_URL = config.VITE_APP_AUTH_URL;
export const MANAGER_URL = config.VITE_APP_MANAGER_URL;

export async function fetchJson(url, options = {}) {
  try {
    const res = await fetch(url, {
      credentials: "include",
      headers: {
        Accept: "application/json",
        ...options.headers,
      },
      ...options,
    });

    const data = await res.json().catch(() => ({}));

    if (res.ok) {
      // 200 / 201 / 204
      return data;
    }

    let message ='Ошибка при выполнении запроса'

    if (res.status === 400 && data?.violations?.length) {
      const v = data.violations.map(v => `${v.field}: ${v.message}`).join(', ');
      message = `Ошибка валидации: ${v}`;
    }
    else if (res.status === 401) {
      message = "Сессия истекла. Войдите в систему заново.";
      if (!options.skipAuthRedirect) {
        setTimeout(() => {
          localStorage.clear();
          window.location.href = "/login";
        }, 500);
      }
    }
    else if (res.status === 403) {
      const msg = (data?.message || '').toLowerCase();

      if (msg.includes('invalid password')) message = 'Неверный пароль';
      else if (msg.includes('bad credentials')) message = 'Неверный email или пароль';
      else if (msg.includes('user not found')) message = 'Пользователь не найден';
      else if (msg.includes('access denied')) message = 'Доступ запрещён';
      else message = 'Ошибка авторизации. Проверьте введённые данные.';
    }
    else if (res.status === 404 || data?.code === 404) {
      message = 'Пользователь не найден';
    }
    else if (res.status === 409) {
      message = 'Такой пользователь уже существует';
    }
    else if (res.status === 500) {
      message = 'Внутренняя ошибка сервера. Попробуйте позже.';
    }
    else if (data?.message) {
      message = data.message;
    }
    else {
      message = data.message || `Ошибка запроса (${res.status})`;
    }

    const error = new Error(message);
    error.status = res.status;
    error.data = data;
    throw error;

  } catch (err) { 
    console.warn("[fetchJson] Ошибка запроса:", err.message);
    throw err;
  }
}