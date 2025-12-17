import { AUTH_URL } from './base'
import { fetchJson } from './base'

export async function login(payload) {
  return fetchJson(`${AUTH_URL}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(payload),
  })
}

export async function signup(payload) {
  return fetchJson(`${AUTH_URL}/signup`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify(payload),
  })
}

export async function getUserInfo() {
  try {
    const data = await fetchJson(`${AUTH_URL}/me`, {
      method: 'GET',
    })

    const role = data.groupName === 'Teacher' ? 'teacher' : 'student'

    return {
      id: data.id,
      firstName: data.firstName,
      lastName: data.lastName,
      email: data.email,
      groupName: data.groupName,
      groupId: data.groupId,
      role,
    }
  } catch (err) {
    return null
  }
}

export async function logoutUser() {
  try {
    await fetchJson(`${AUTH_URL}/logout`, {
      method: 'POST',
    })

    localStorage.removeItem('seenWelcome')
    return true
  } catch (err) {
    return false
  }
}

export async function refreshAuth() {
  try {
    await fetchJson(`${AUTH_URL}/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
    })

    return true
  } catch {
    return false
  }
}
