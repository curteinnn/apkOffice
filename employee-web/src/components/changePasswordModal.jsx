import { useState } from "react";
import { apiRequest } from "../services/api";

export default function ChangePasswordModal({ onClose }) {
  const [passwordLama, setPasswordLama] = useState("");
  const [passwordBaru, setPasswordBaru] = useState("");
  const [konfirmasiPassword, setKonfirmasiPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const handleClose = () => {
    if (!loading) onClose();
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setMessage("");
    setError("");

    if (passwordBaru.length < 6) {
      setError("Password baru minimal 6 karakter.");
      return;
    }

    if (passwordBaru !== konfirmasiPassword) {
      setError("Konfirmasi password baru tidak sama.");
      return;
    }

    setLoading(true);

    try {
      const response = await apiRequest("/api/users/change-password", {
        method: "PUT",
        body: JSON.stringify({ passwordLama, passwordBaru }),
      });

      setMessage(
        typeof response === "string" ? response : "Password berhasil diubah.",
      );
      setPasswordLama("");
      setPasswordBaru("");
      setKonfirmasiPassword("");
    } catch (requestError) {
      setError(requestError.message || "Gagal mengubah password.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
      <section
        role="dialog"
        aria-modal="true"
        aria-labelledby="change-password-title"
        className="w-full max-w-md rounded-2xl bg-white p-6 shadow-xl"
      >
        <div className="flex items-start justify-between gap-4">
          <div>
            <h2
              id="change-password-title"
              className="text-xl font-bold text-gray-900"
            >
              Ubah Password
            </h2>
            <p className="mt-1 text-sm text-gray-600">
              Masukkan password lama dan password baru.
            </p>
          </div>
          <button
            type="button"
            onClick={handleClose}
            disabled={loading}
            aria-label="Tutup popup ubah password"
            className="rounded-md px-2 py-1 text-xl leading-none text-gray-500 hover:bg-gray-100 disabled:opacity-50"
          >
            &times;
          </button>
        </div>

        <form onSubmit={handleSubmit} className="mt-5 space-y-4">
          <label className="block text-sm font-medium text-gray-700">
            Password saat ini
            <input
              type="password"
              autoComplete="current-password"
              value={passwordLama}
              onChange={(event) => setPasswordLama(event.target.value)}
              required
              className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2 text-gray-900 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200"
            />
          </label>
          <label className="block text-sm font-medium text-gray-700">
            Password baru
            <input
              type="password"
              autoComplete="new-password"
              value={passwordBaru}
              onChange={(event) => setPasswordBaru(event.target.value)}
              minLength={6}
              required
              className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2 text-gray-900 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200"
            />
          </label>
          <label className="block text-sm font-medium text-gray-700">
            Konfirmasi password baru
            <input
              type="password"
              autoComplete="new-password"
              value={konfirmasiPassword}
              onChange={(event) => setKonfirmasiPassword(event.target.value)}
              minLength={6}
              required
              className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2 text-gray-900 outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-200"
            />
          </label>

          {error && (
            <p role="alert" className="rounded-lg bg-red-50 p-3 text-sm text-red-700">
              {error}
            </p>
          )}
          {message && (
            <p role="status" className="rounded-lg bg-green-50 p-3 text-sm text-green-700">
              {message}
            </p>
          )}

          <div className="flex justify-end gap-3 pt-1">
            <button
              type="button"
              onClick={handleClose}
              disabled={loading}
              className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 transition hover:bg-gray-50 disabled:opacity-50"
            >
              Batal
            </button>
            <button
              type="submit"
              disabled={loading}
              className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-700 disabled:cursor-wait disabled:opacity-50"
            >
              {loading ? "Menyimpan..." : "Simpan"}
            </button>
          </div>
        </form>
      </section>
    </div>
  );
}
