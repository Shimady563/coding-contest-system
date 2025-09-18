import { config } from '../config';

export const AUTH_URL = config.VITE_APP_AUTH_URL;
export const MANAGER_URL = config.VITE_APP_MANAGER_URL;

export async function fetchJson(url, options = {}) {
  const response = await fetch(url, { credentials: 'include', ...options });
  if (!response.ok) {
    let message = `HTTP ${response.status}`;
    try {
      const data = await response.json();
      message = data?.message || data?.error || message;
    } catch {}
    throw new Error(message);
  }
  const text = await response.text();
  if (!text) return null;
  try { return JSON.parse(text); } catch { return text; }
}