import { config } from '../config';

export const AUTH_URL = config.VITE_APP_AUTH_URL;
export const MANAGER_URL = config.VITE_APP_MANAGER_URL;

async function fetchJson(url, options = {}) {
  const response = await fetch(url, { credentials: 'include', ...options });
  if (!response.ok) {
    let message = `HTTP ${response.status}`;
    try {
      const data = await response.json();
      message = data?.message || data?.error || message;
    } catch {
      // ignore body parse errors
    }
    throw new Error(message);
  }
  // Some endpoints might return no content (204) or empty body on success
  const text = await response.text();
  if (!text) return null;
  try { return JSON.parse(text); } catch { return text; }
}

// Tasks
export async function createTask(task) {
  return fetchJson(`${MANAGER_URL}/tasks`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(task),
  });
}

export async function updateTask(taskId, task) {
  return fetchJson(`${MANAGER_URL}/tasks/${taskId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(task),
  });
}

export async function getTask(taskId) {
  return fetchJson(`${MANAGER_URL}/tasks/${taskId}`);
}

export async function listTasks(params = { pageSize: 10000 }) {
  const query = new URLSearchParams(params).toString();
  return fetchJson(`${MANAGER_URL}/tasks?${query}`);
}

export async function getTasksByContestVersion(contestVersionId) {
  return fetchJson(`${MANAGER_URL}/tasks/contest-version?contestVersionId=${encodeURIComponent(contestVersionId)}`);
}

// Contests
export async function createContest(payload) {
  return fetchJson(`${MANAGER_URL}/contests`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

export async function updateContest(contestId, payload) {
  return fetchJson(`${MANAGER_URL}/contests/${contestId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

export async function getContest(contestId) {
  return fetchJson(`${MANAGER_URL}/contests/${contestId}`);
}

export async function createContestVersion(payload) {
  return fetchJson(`${MANAGER_URL}/contest-versions`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

// Contest Versions
export async function getContestVersionsByContest(contestId) {
  return fetchJson(`${MANAGER_URL}/contest-versions?contestId=${encodeURIComponent(contestId)}`);
}

// Student flow
export async function getContestsByGroup(groupId) {
  return fetchJson(`${MANAGER_URL}/contests/group?groupId=${encodeURIComponent(groupId)}`);
}

export async function listSolutions(params) {
  const query = new URLSearchParams(params || {}).toString();
  return fetchJson(`${MANAGER_URL}/solutions?${query}`);
}

export async function listTasksWithParams(params) {
  const query = new URLSearchParams(params || {}).toString();
  return fetchJson(`${MANAGER_URL}/tasks?${query}`);
}

export async function listUsers(params) {
  const query = new URLSearchParams(params || {}).toString();
  return fetchJson(`${MANAGER_URL}/users?${query}`);
}

export async function getGroups() {
  return fetchJson(`${MANAGER_URL}/groups`);
}

export async function getGroupsPage(params) {
  const query = new URLSearchParams(params || {}).toString();
  return fetchJson(`${MANAGER_URL}/groups/page?${query}`);
}

export async function createGroup(payload) {
  return fetchJson(`${MANAGER_URL}/groups/new`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

export async function deleteGroup(groupId) {
  return fetchJson(`${MANAGER_URL}/groups/${groupId}`, { method: 'DELETE' });
}

export async function updateUser(userId, payload) {
  return fetchJson(`${MANAGER_URL}/users/${userId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

export async function deleteUser(userId) {
  return fetchJson(`${MANAGER_URL}/users/${userId}`, { method: 'DELETE' });
}

export async function login(payload) {
  const res = await fetch(`${AUTH_URL}/login`, {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  if (!res.ok) throw new Error('Ошибка авторизации');
  return true;
}

export async function signup(payload) {
  const res = await fetch(`${AUTH_URL}/signup`, {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  if (!res.ok) {
    let message = 'Ошибка регистрации';
    try { const d = await res.json(); message = d?.message || d?.error || message; } catch { /* ignore parse errors */ }
    throw new Error(message);
  }
  return true;
}

export async function submitSolution(payload) {
  return fetchJson(`${MANAGER_URL}/submissions`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

export async function startContestForUser(userId, { contestVersionId, contestId }) {
  return fetchJson(`${MANAGER_URL}/users/start/${userId}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ contestVersionId, contestId }),
  });
}

export async function refreshAuth() {
  const response = await fetch(`${AUTH_URL}/refresh`, {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
  });
  if (!response.ok) throw new Error('Не удалось обновить токен');
  return true;
}


