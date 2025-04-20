export async function getUserInfo() {
  try {
    const tokenData = JSON.parse(localStorage.getItem("tokenData"));
    if (!tokenData || !tokenData.accessToken) {
      return null; // просто возвращаем null, не кидаем исключение
    }

    const authHeader = `${tokenData.type} ${tokenData.accessToken}`;
    const res = await fetch("http://localhost:8081/api/v1/auth/me", {
      method: "GET",
      headers: {
        "Authorization": authHeader,
      },
    });

    if (!res.ok) {
      if (res.status === 401) return null; // также возвращаем null
      throw new Error("Failed to fetch user");
    }

    const data = await res.json();
    const role = data.groupName === "Teacher" ? "teacher" : "student";

    return {
      id: data.id,
      name: `${data.firstName} ${data.lastName}`,
      email: data.email,
      role: role,
      groupName: data.groupName,
    };
  } catch (err) {
    console.error("Error fetching user info:", err);
    return null;
  }
}
