// /src/js/auth.js

export function getAccessToken() {
  const tokenData = JSON.parse(localStorage.getItem("tokenData"));
  return tokenData?.accessToken || null;
}

export function getRefreshToken() {
  const tokenData = JSON.parse(localStorage.getItem("tokenData"));
  return tokenData?.refreshToken || null;
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

export async function getUserInfo() {
  try {
    const accessToken = getAccessToken();
    const tokenData = JSON.parse(localStorage.getItem("tokenData")); // нужно для типа (Bearer)
    if (!accessToken || !tokenData?.type) {
      return null;
    }

    const authHeader = `${tokenData.type} ${accessToken}`;
    const res = await fetch("http://localhost:8081/api/v1/auth/me", {
      method: "GET",
      headers: {
        "Authorization": authHeader,
      },
    });

    if (!res.ok) {
      if (res.status === 401) return null;
      throw new Error("Failed to fetch user");
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
    console.error("Error fetching user info:", err);
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
