export const config = {
  VITE_APP_AUTH_URL: window.config?.VITE_APP_AUTH_URL,
  VITE_APP_MANAGER_URL: window.config?.VITE_APP_MANAGER_URL,
  clientKey: window.config?.SMART_CAPTCHA_CLIENT_KEY,
  serverKey: window.config?.SMART_CAPTCHA_SECRET_KEY
};