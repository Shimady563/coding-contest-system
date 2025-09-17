import { AUTH_URL, MANAGER_URL } from './api';

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

export async function fetchGroups() {
  try {
    const response = await fetch(`${MANAGER_URL}/groups`);

    if (!response.ok) {
      throw new Error("Ошибка при загрузке групп");
    }

    return await response.json();
  } catch (err) {
    console.error("Ошибка при получении групп:", err);
    return [];
  }
}

export async function getGroupIdForCurrentUser() {
  try {
    const userInfo = await getUserInfo();
    if (!userInfo || !userInfo.groupName) {
      throw new Error("Не удалось получить информацию о пользователе или группе");
    }

    const groups = await fetchGroups();
    const group = groups.find(g => g.name === userInfo.groupName);

    if (!group) {
      throw new Error(`Группа с именем ${userInfo.groupName} не найдена`);
    }

    return group.id;
  } catch (err) {
    console.error("Ошибка при получении ID группы пользователя:", err);
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

    return true;
  } catch (err) {
    console.error("Ошибка при выходе из аккаунта:", err);
    return false;
  }
}