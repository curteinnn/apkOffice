import { useState } from "react";
import { login } from "../services/api";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setLoading(true);

    try {
      const data = await login(username, password);

      console.log("Login berhasil:", data);

      // sementara
      alert(`Selamat datang, ${data.namaLengkap}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="min-h-screen flex bg-linear-to-r from-blue-300 to-white px-6 py-10 text-white">
      <div className="mx-auto w-80 h-130 rounded-lg bg-white p-10 justify-center">
        <div className="mb-8">
          <h1 className="text-3xl text-black font-bold tracking-tight">
            Selamat Datang
          </h1>

          <p className="mt-2 text-sm text-zinc-400">
            Login untuk mengakses akun kamu.
          </p>
        </div>

        <form
          onSubmit={handleSubmit}
          className="w- justify-center items-center space-y-5"
        >
          <div>
            <label
              htmlFor="username"
              className="mb-2 block text-sm font-medium text-zinc-300"
            >
              Username
            </label>

            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Masukkan username"
              autoComplete="username"
              required
              className="w-full rounded-xl border border-gray-700 bg-white px-4 py-3 text-sm outline-none transition placeholder:text-zinc-600 focus:border-zinc-500"
            />
          </div>

          <div>
            <label
              htmlFor="password"
              className="mb-2 block text-sm font-medium text-zinc-300"
            >
              Password
            </label>

            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Masukkan password"
              autoComplete="current-password"
              required
              className="w-full rounded-xl border border-gray-700 bg-white px-4 py-3 text-sm outline-none transition placeholder:text-zinc-600 focus:border-zinc-500"
            />
          </div>

          {error && (
            <div className="rounded-xl border border-red-900/50 bg-red-950/40 px-4 py-3 text-sm text-red-400">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-xl bg-blue-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-blue-900 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {loading ? "Loading..." : "Login"}
          </button>
        </form>

        <p className="mt-8 text-center text-xs text-zinc-600">
          Employee Management System
        </p>
      </div>
    </main>
  );
}

export default Login;
