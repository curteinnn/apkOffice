export default function Dashboard() {
  return (
    <div className="min-h-screen bg-gray-100 px-5 pt-6 pb-24">
      <div className="mb-6">
        <p className="text-sm text-gray-500">Selamat datang 👋</p>
        <h1 className="text-2xl font-bold text-gray-900">Aldry</h1>
      </div>

      <div className="mb-5 rounded-2xl bg-black p-5 text-white shadow-lg">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-gray-400">Status hari ini</p>
            <h2 className="mt-1 text-xl font-semibold">Belum Absen</h2>
          </div>

          <div className="flex h-12 w-12 items-center justify-center rounded-full bg-white/10"></div>
        </div>

        <button className="mt-5 w-full rounded-xl bg-white py-3 font-semibold text-black transition hover:bg-gray-200">
          Absen Sekarang
        </button>
      </div>

      <div>
        <h2 className="mb-3 text-lg font-semibold text-gray-900">Menu</h2>

        <div className="grid grid-cols-2 gap-4">
          <button className="rounded-2xl bg-white p-5 text-left shadow-sm transition hover:-translate-y-1 hover:shadow-md">
            <div className="mb-3 text-2xl">📅</div>
            <h3 className="font-semibold text-gray-900">Absensi</h3>
            <p className="mt-1 text-sm text-gray-500">Riwayat kehadiran</p>
          </button>

          <button className="rounded-2xl bg-white p-5 text-left shadow-sm transition hover:-translate-y-1 hover:shadow-md">
            <div className="mb-3 text-2xl">📋</div>
            <h3 className="font-semibold text-gray-900">Tugas</h3>
            <p className="mt-1 text-sm text-gray-500">Upload pekerjaan</p>
          </button>

          <button className="rounded-2xl bg-white p-5 text-left shadow-sm transition hover:-translate-y-1 hover:shadow-md">
            <div className="mb-3 text-2xl">💰</div>
            <h3 className="font-semibold text-gray-900">Penghasilan</h3>
            <p className="mt-1 text-sm text-gray-500">Lihat pembayaran</p>
          </button>

          <button className="rounded-2xl bg-white p-5 text-left shadow-sm transition hover:-translate-y-1 hover:shadow-md">
            <div className="mb-3 text-2xl">📝</div>
            <h3 className="font-semibold text-gray-900">Izin</h3>
            <p className="mt-1 text-sm text-gray-500">Ajukan izin</p>
          </button>
        </div>
      </div>
    </div>
  );
}
