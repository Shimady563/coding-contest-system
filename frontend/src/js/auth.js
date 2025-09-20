import { AUTH_URL } from "./base";

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
    try { const d = await res.json(); message = d?.message || d?.error || message; } catch { }
    throw new Error(message);
  }
  return true;
}

export async function getUserInfo() {
  try {
    const res = await fetch(`${AUTH_URL}/me`, {
      method: "GET",
      credentials: "include",
    });

    if (!res.ok) {
      return null;
    }

    const data = await res.json();

    const role = data.groupName === "Teacher" ? "teacher" : "student";

    return {
      id: data.id,
      firstName: data.firstName,
      lastName: data.lastName,
      email: data.email,
      groupName: data.groupName,
      groupId: data.groupId,
      role: role,
    };
  } catch {
    return null;
  }
}

export async function logoutUser() {
  try {
    const res = await fetch(`${AUTH_URL}/logout`, {
      method: "POST",
      credentials: "include",
    });

    if (!res.ok) {
      throw new Error("Не удалось выйти из аккаунта");
    }

    localStorage.removeItem("seenWelcome");

    return true;
  } catch (err) {
    console.error("Ошибка при выходе из аккаунта:", err);
    return false;
  }
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