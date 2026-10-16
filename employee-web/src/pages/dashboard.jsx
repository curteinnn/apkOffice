import { useNavigate } from "react-router-dom";
import { useGSAP } from "@gsap/react";
import { animationDashboard } from "../animations/animationDashboard";
import { useAttendance } from "../hooks/useAttendance";

export default function Dashboard() {
  const navigate = useNavigate();
  const user = JSON.parse(sessionStorage.getItem("user") || "null");

  const {
    attendance,
    loading,
    submitting,
    error,
    success,
    checkedIn,
    checkedOut,
    handleCheckIn,
    handleCheckOut,
    handleLogout,
  } = useAttendance();

  useGSAP(() => {
    animationDashboard();
  });

  return (
    <main className="min-h-screen bg-slate-50 px-4 py-6 sm:px-6 lg:px-8">
      <div className="mx-auto max-w-6xl">
        <header className="mb-8 flex items-center justify-between gap-4">
          <div>
            <p className="text-sm text-slate-500">Employee Management System</p>
            <h1 className="mt-1 text-2xl font-bold text-slate-900">
              Dashboard
            </h1>
            <p className="mt-2 text-sm text-slate-600">
              Selamat datang, {user?.namaLengkap || user?.username || "User"}.
            </p>
          </div>

          <button
            type="button"
            onClick={handleLogout}
            className="rounded-lg border border-slate-200 bg-white px-4 py-2 text-sm font-medium text-slate-700 transition hover:bg-slate-100"
          >
            Keluar
          </button>
        </header>

        {error && (
          <p
            role="alert"
            className="mb-5 rounded-lg bg-red-50 p-3 text-sm text-red-700"
          >
            {error}
          </p>
        )}

        {success && (
          <p
            role="status"
            className="mb-5 rounded-lg bg-emerald-50 p-3 text-sm text-emerald-700"
          >
            {success}
          </p>
        )}

        <section className="mb-6 grid gap-4 sm:grid-cols-2">
          <article className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <p className="text-sm text-slate-500">Status Absensi</p>
            <h2 className="mt-3 text-xl font-semibold text-slate-900">
              {loading
                ? "Memuat..."
                : checkedOut
                  ? "Sudah Pulang"
                  : checkedIn
                    ? "Sudah Absen Masuk"
                    : "Belum Absen"}
            </h2>
            <p className="mt-2 text-sm text-slate-500">
              {new Date().toLocaleDateString("id-ID", {
                weekday: "long",
                day: "numeric",
                month: "long",
                year: "numeric",
              })}
            </p>
          </article>

          <article className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
            <p className="text-sm text-slate-500">Jam Kerja Hari Ini</p>
            <div className="mt-4 flex gap-8">
              <div>
                <p className="text-xs text-slate-500">Masuk</p>
                <p className="mt-1 text-xl font-semibold text-slate-900">
                  {attendance?.jamMasuk
                    ? new Date(attendance.jamMasuk).toLocaleTimeString(
                        "id-ID",
                        {
                          hour: "2-digit",
                          minute: "2-digit",
                          hour12: false,
                        },
                      )
                    : "--:--"}
                </p>
              </div>
              <div>
                <p className="text-xs text-slate-500">Pulang</p>
                <p className="mt-1 text-xl font-semibold text-slate-900">
                  {attendance?.jamKeluar
                    ? new Date(attendance.jamKeluar).toLocaleTimeString(
                        "id-ID",
                        {
                          hour: "2-digit",
                          minute: "2-digit",
                          hour12: false,
                        },
                      )
                    : "--:--"}
                </p>
              </div>
            </div>
          </article>
        </section>

        <section className="mb-6">
          <h2 className="mb-4 text-lg font-semibold text-slate-900">
            Menu Absensi
          </h2>

          <div className="grid gap-4 sm:grid-cols-2">
            <button
              type="button"
              disabled={loading || submitting || checkedIn}
              onClick={handleCheckIn}
              className="flex min-h-36 flex-col items-center justify-center rounded-xl bg-blue-100 p-6 text-center transition hover:bg-blue-200 disabled:cursor-not-allowed disabled:opacity-50"
            >
              <span className="text-lg font-semibold text-blue-950">
                {checkedIn ? "Sudah Absen Masuk" : "Absen Masuk"}
              </span>
              <span className="mt-2 text-sm text-blue-800">
                Catat waktu kedatangan
              </span>
            </button>

            <button
              type="button"
              disabled={loading || submitting || !checkedIn || checkedOut}
              onClick={handleCheckOut}
              className="flex min-h-36 flex-col items-center justify-center rounded-xl bg-emerald-100 p-6 text-center transition hover:bg-emerald-200 disabled:cursor-not-allowed disabled:opacity-50"
            >
              <span className="text-lg font-semibold text-emerald-950">
                {checkedOut ? "Sudah Absen Pulang" : "Absen Pulang"}
              </span>
              <span className="mt-2 text-sm text-emerald-800">
                Catat waktu kepulangan
              </span>
            </button>
          </div>
        </section>

        <section>
          <h2 className="mb-4 text-lg font-semibold text-slate-900">
            Informasi Lainnya
          </h2>

          <div className="grid gap-4 sm:grid-cols-2">
            <button
              type="button"
              onClick={() => navigate("/listVisit")}
              className="rounded-xl border border-slate-200 bg-white p-5 text-left transition hover:bg-slate-100"
            >
              <h3 className="font-semibold text-slate-900">Daftar Kunjungan</h3>
              <p className="mt-2 text-sm text-slate-500">
                Lihat daftar kunjungan yang tersedia.
              </p>
            </button>

            <button
              type="button"
              onClick={() => navigate("/riwayatAbsensi")}
              className="rounded-xl border border-slate-200 bg-white p-5 text-left transition hover:bg-slate-100"
            >
              <h3 className="font-semibold text-slate-900">Riwayat Absensi</h3>
              <p className="mt-2 text-sm text-slate-500">
                Lihat catatan absensi sebelumnya.
              </p>
            </button>
          </div>
        </section>
      </div>
    </main>
  );
}
