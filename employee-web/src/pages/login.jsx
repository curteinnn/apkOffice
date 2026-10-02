import { useState } from "react";
import { login } from "../services/api";
import Logo from "../assets/logo.png";
import { useGSAP } from "@gsap/react";
import { animationLogin } from "../animations/animationLogin";

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
      alert(`Selamat datang, ${data.namaLengkap}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useGSAP(() => {
    animationLogin();
  });

  return (
    <main className="min-h-screen flex items-center justify-center bg-linear-to-br from-blue-300 to-white p-4">
      <div className="card w-75 rounded-2xl bg-white p-6 shadow-xl sm:p-10 md:w-100 lg:p-12 lg:w-150">
        <div className="flex flex-col gap-10 lg:flex-row lg:items-center lg:gap-12">
          <section className="w-full lg:flex-1">
            <div className="mb-8">
              <h1 className="text-center text-2xl font-bold tracking-tight text-black lg:text-left">
                Login
              </h1>
            </div>

            <form
              onSubmit={handleSubmit}
              className="form flex w-full flex-col gap-5"
            >
              <div>
                <label
                  htmlFor="username"
                  className="mb-2 block text-sm font-medium text-gray-700"
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
                  className="w-full rounded-xl border border-gray-300 px-4 py-3 text-sm text-black outline-none transition placeholder:text-gray-400 focus:border-blue-600 focus:ring-2 focus:ring-blue-100"
                />
              </div>

              <div>
                <label
                  htmlFor="password"
                  className="mb-2 block text-sm font-medium text-gray-700"
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
                  className="w-full rounded-xl border border-gray-300 px-4 py-3 text-sm text-black outline-none transition placeholder:text-gray-400 focus:border-blue-600 focus:ring-2 focus:ring-blue-100"
                />
              </div>

              {error && (
                <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
                  {error}
                </div>
              )}

              <button
                type="submit"
                disabled={loading}
                className="w-full rounded-xl bg-blue-700 px-4 py-3 text-sm font-semibold text-white transition hover:bg-blue-900 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {loading ? "Loading..." : "Login"}
              </button>
            </form>
          </section>

          <section className="hidden flex-1 items-center justify-center lg:flex">
            <img
              src={Logo}
              alt="Logo"
              className="logo h-auto w-full max-w-80 object-contain"
            />
          </section>
        </div>
      </div>
    </main>
  );
}

export default Login;
