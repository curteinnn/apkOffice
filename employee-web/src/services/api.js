const API_URL = (import.meta.env.VITE_API_URL || "").replace(/\/+$/, "");

export async function login(username, password) {
  const response = await fetch("${API_URL}/api/auth/login", {
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
        "Server mengembalikan response bukan JSON (HTTP ${response.status}): ${text.slice(0, 200)}",
      );
    }
  }

  if (!response.ok) {
    throw new Error(data.message || "Login gagal (HTTP ${response.status})");
  }

  if (!text) {
    throw new Error("Server mengembalikan response kosong.");
  }

  return data;
}

export async function apiRequest(endpoint, options = {}) {
  if (!API_URL) {
    throw new Error("URL backend belum dikonfigurasi.");
  }

  const headers = {
    Accept: "application/json",
    ...options.headers,
  };

  if (options.body) {
    headers["Content-Type"] = "application/json";
  }

  const token = sessionStorage.getItem("token");

  if (token) {
    headers.Authorization = "Bearer ${token}";
  }

  const response = await fetch("${API_URL}${endpoint}", {
    ...options,
    headers,
  });

  const text = await response.text();
  let data = null;

  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
  }

  if (!response.ok) {
    const message =
      typeof data === "object" && data !== null ? data.message : data;

    throw new Error(message || `Request gagal (HTTP ${response.status})`);
  }

  return data;
}
