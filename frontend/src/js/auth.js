export async function getUserInfo() {
  try {
    const res = await fetch("http://localhost:8081/api/v1/auth/me", {
      method: "GET",
      credentials: "include", // используем куки
    });

    if (!res.ok) {
      if (res.status === 401) throw new Error("UNAUTHORIZED");
      throw new Error("Failed to fetch user");
    }

    const data = await res.json();

    // Определяем роль
    const role = data.groupName === "Teacher" ? "teacher" : "student";

    return {
      id: data.id,
      name: `${data.firstName} ${data.lastName}`,
      email: data.email,
      role: role,
      groupName: data.groupName,
    };
  } catch (err) {
    throw err;
  }
}
