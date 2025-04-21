// /src/js/auth.js

export function getAccessToken() {
  const tokenData = JSON.parse(localStorage.getItem("tokenData"));
  return tokenData?.accessToken || null;
}

export function getRefreshToken() {
  const tokenData = JSON.parse(localStorage.getItem("tokenData"));
  return tokenData?.refreshToken || null;
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
