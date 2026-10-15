const API_URL = import.meta.env.VITE_API_URL;

export async function login(username, password) {
  const response = await fetch(`${API_URL}/api/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
    },
    body: JSON.stringify({
      username,
      password,
    }),
  });

  const text = await response.text();

  let data = {};

  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      throw new Error(
        `Server mengembalikan response bukan JSON (HTTP ${response.status}): ${text.slice(0, 200)}`,
      );
    }
  }

  if (!response.ok) {
    throw new Error(data.message || `Login gagal (HTTP ${response.status})`);
  }

  if (!text) {
    throw new Error("Server mengembalikan response kosong.");
  }

  return data;
}
