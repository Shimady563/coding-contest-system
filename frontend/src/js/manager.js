import { fetchJson, MANAGER_URL } from './base';
import { getUserInfo } from './auth';

// ==================== TASK MANAGEMENT ====================
export async function createTask(task) {
  return fetchJson(`${MANAGER_URL}/tasks`, {
    method: 'POST',
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

export async function updateTask(taskId, task) {
  return fetchJson(`${MANAGER_URL}/tasks/${taskId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(task),
  });
}

export async function deleteTask(taskId) {
  return fetchJson(`${MANAGER_URL}/tasks/${taskId}`, {
    method: 'DELETE'
  });
}

export async function getTasksByContestVersion(contestVersionId) {
  return fetchJson(`${MANAGER_URL}/tasks/contest-version?contestVersionId=${encodeURIComponent(contestVersionId)}`);
}

export async function listTasksWithParams(params) {
  const query = new URLSearchParams(params || {}).toString();
  return fetchJson(`${MANAGER_URL}/tasks?${query}`);
}

// ==================== CONTEST MANAGEMENT ====================
export async function createContest(payload) {
  return fetchJson(`${MANAGER_URL}/contests`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

export async function getContest(contestId) {
  return fetchJson(`${MANAGER_URL}/contests/${contestId}`);
}

export async function listContests(params = {}) {
  const query = new URLSearchParams(params).toString();
  return fetchJson(`${MANAGER_URL}/contests?${query}`);
}

export async function updateContest(contestId, payload) {
  return fetchJson(`${MANAGER_URL}/contests/${contestId}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

export async function deleteContest(contestId) {
  return fetchJson(`${MANAGER_URL}/contests/${contestId}`, {
    method: 'DELETE'
  });
}

export async function getContestsByGroup(groupId) {
  return fetchJson(`${MANAGER_URL}/contests/group?groupId=${encodeURIComponent(groupId)}`);
}

// ==================== CONTEST VERSION MANAGEMENT ====================
export async function createContestVersion(payload) {
  return fetchJson(`${MANAGER_URL}/contest-versions`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

export async function deleteContestVersion(versionId) {
  return fetchJson(`${MANAGER_URL}/contest-versions/${versionId}`, {
    method: 'DELETE'
  });
}

export async function getContestVersionsByContest(contestId) {
  return fetchJson(`${MANAGER_URL}/contest-versions?contestId=${encodeURIComponent(contestId)}`);
}

// ==================== SOLUTION MANAGEMENT ====================
export async function listSolutions(params) {
  const query = new URLSearchParams(params || {}).toString();
  return fetchJson(`${MANAGER_URL}/solutions?${query}`);
}

export async function submitSolution(payload) {
  return fetchJson(`${MANAGER_URL}/submissions`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
}

// ==================== USER MANAGEMENT ====================
export async function listUsers(params) {
  const query = new URLSearchParams(params || {}).toString();
  return fetchJson(`${MANAGER_URL}/users?${query}`);
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

export async function startContestForUser(userId, { contestVersionId, contestId }) {
  return fetchJson(`${MANAGER_URL}/users/start/${userId}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ contestVersionId, contestId }),
  });
}

// ==================== GROUP MANAGEMENT ====================
export async function fetchGroups() {
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

export async function getGroupIdForCurrentUser() {
  const userInfo = await getUserInfo();
  if (!userInfo || !userInfo.groupName) {
    return null;
  }

  const groups = await fetchGroups();
  const group = groups.find(g => g.name === userInfo.groupName);
  return group ? group.id : null;
}