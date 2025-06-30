export async function getUserInfo() {
  try {
    const res = await fetch("http://localhost:8081/api/v1/auth/me", {
      method: "GET",
      credentials: "include", // ВАЖНО для отправки cookie
    });

    if (!res.ok) {
      if (res.status === 401) return null;
      throw new Error("Не удалось получить пользователя");
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
  } catch (err) {
    console.error("Ошибка получения информации о пользователе:", err);
    return null;
  }
}

export async function fetchGroups() {
  try {
    const response = await fetch("http://localhost:8080/api/v1/groups");

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
