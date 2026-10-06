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
      <section className="w-full max-w-5xl py-6 ">
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

        {/* <div className="mt-8 grid grid-cols-1 gap-4 lg:grid-cols-2">
          <div className="flex min-h-20 items-center rounded-xl bg-blue-300 p-5">
            <p className="font-medium text-blue-950">Informasi Kehadiran</p>
          </div>

          <div className="flex min-h-20 items-center rounded-xl bg-blue-300 p-5">
            <p className="font-medium text-blue-950">Informasi Pekerjaan</p>
          </div>
        </div> */}

        <div className="mt-8 grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-2">
          <button
            type="button"
            onClick={() => navigate("/absenMasuk")}
            className="flex min-h-40 flex-col items-center justify-center rounded-xl bg-blue-200 p-6 text-center transition hover:bg-blue-300"
          >
            <h2 className="text-lg font-semibold text-blue-950">Absen Masuk</h2>

            <p className="mt-2 text-sm text-blue-800">
              Lorem ipsum dolor sit amet consectetur adipisicing elit. Beatae
              molestiae recusandae deserunt id officiis hic vitae dolorum!
              Soluta inventore eligendi, voluptatibus
            </p>
          </button>

          <button
            type="button"
            onClick={() => navigate("/absenKeluar")}
            className="flex min-h-40 flex-col items-center justify-center rounded-xl bg-blue-200 p-6 text-center transition hover:bg-blue-300"
          >
            <h2 className="text-lg font-semibold text-blue-950">
              Absen Pulang
            </h2>

            <p className="mt-2 text-sm text-blue-800">
              Lorem ipsum dolor sit, amet consectetur adipisicing elit. Facilis
              pariatur nulla voluptas, facere, tenetur nam excepturi blanditiis
            </p>
          </button>

          <button
            type="button"
            onClick={() => navigate("/listVisit")}
            className="flex min-h-40 flex-col items-center justify-center rounded-xl bg-blue-200 p-6 text-center transition hover:bg-blue-300"
          >
            <h2 className="text-lg font-semibold text-blue-950">List Visit</h2>

            <p className="mt-2 text-sm text-blue-800">
              Lorem, ipsum dolor sit amet consectetur adipisicing elit. Alias,
            </p>
          </button>

          <button
            type="button"
            onClick={() => navigate("/riwayatAbsensi")}
            className="flex min-h-40 flex-col items-center justify-center rounded-xl bg-blue-200 p-6 text-center transition hover:bg-blue-300"
          >
            <h2 className="text-lg font-semibold text-blue-950">
              Riwayat Absensi
            </h2>

            <p className="mt-2 text-sm text-blue-800">
              Lorem ipsum, dolor sit amet consectetur adipisicing elit. Maxime
              nostrum quia, minus aliquid error tempora non exercitationem,
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
