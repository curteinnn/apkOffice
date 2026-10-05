import { useNavigate } from "react-router-dom";

export default function Dashboard() {
  const navigate = useNavigate();
  const user = JSON.parse(sessionStorage.getItem("user") || "null");
  const handleLogout = () => {
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("user");

    navigate("/", { replace: true });
  };

  return (
    <main className="flex min-h-screen items-center justify-center bg-linear-to-br from-blue-400 to-white p-4 sm:p-8">
      <section className="w-full max-w-5xl py-6">
        <header className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="title px-1 text-2xl font-bold text-gray-900 sm:text-3xl">
              WELCOME TO MYIN
            </h1>

            <p className="mt-2 px-1 text-sm text-gray-700">
              Halo, {user?.namaLengkap || "Karyawan"}!
            </p>
          </div>

          <div className="rounded-lg bg-white/70 px-4 py-3 shadow-sm">
            <p className="text-sm text-gray-500">Jabatan</p>
            <p className="font-medium text-gray-800">{user?.jabatan || "-"}</p>
          </div>
        </header>

        {/* Informasi */}
        <div className="mt-8 grid grid-cols-1 gap-4 lg:grid-cols-2">
          <div className="flex min-h-20 items-center rounded-xl bg-blue-300 p-5">
            <p className="font-medium text-blue-950">Informasi Kehadiran</p>
          </div>

          <div className="flex min-h-20 items-center rounded-xl bg-blue-300 p-5">
            <p className="font-medium text-blue-950">Informasi Pekerjaan</p>
          </div>
        </div>

        {/* Menu utama */}
        <div className="mt-8 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-2">
          <button
            type="button"
            className="flex min-h-40 flex-col items-center justify-center rounded-xl bg-blue-200 p-6 text-center transition hover:bg-blue-300"
          >
            <h2 className="text-lg font-semibold text-blue-950">
              Rekam Kehadiran
            </h2>

            <p className="mt-2 text-sm text-blue-800">
              Rekam kehadiran Anda saat memulai pekerjaan.
            </p>
          </button>

          <button
            type="button"
            className="flex min-h-40 flex-col items-center justify-center rounded-xl bg-blue-200 p-6 text-center transition hover:bg-blue-300"
          >
            <h2 className="text-lg font-semibold text-blue-950">
              Riwayat Kehadiran
            </h2>

            <p className="mt-2 text-sm text-blue-800">
              Lihat catatan kehadiran Anda.
            </p>
          </button>

          <button
            type="button"
            className="flex min-h-40 flex-col items-center justify-center rounded-xl bg-blue-200 p-6 text-center transition hover:bg-blue-300"
          >
            <h2 className="text-lg font-semibold text-blue-950">
              Tugas Pekerjaan
            </h2>

            <p className="mt-2 text-sm text-blue-800">
              Lihat dan kirim bukti pekerjaan.
            </p>
          </button>

          <button
            type="button"
            className="flex min-h-40 flex-col items-center justify-center rounded-xl bg-blue-200 p-6 text-center transition hover:bg-blue-300"
          >
            <h2 className="text-lg font-semibold text-blue-950">
              Pengajuan Izin
            </h2>

            <p className="mt-2 text-sm text-blue-800">
              Ajukan izin atau keperluan lainnya.
            </p>
          </button>

          <button
            onClick={handleLogout}
            className="rounded-lg bg-red-500 px-4 w-20 py-2 text-white transition hover:bg-red-600"
          >
            Logout
          </button>
        </div>
      </section>
    </main>
  );
}
