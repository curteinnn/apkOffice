import useRiwayatAbsensi from "../hooks/useRiwayatAbsensi";

export default function RiwayatAbsensi() {
  const { riwayat, loading, error, formatTanggal, formatJam } =
    useRiwayatAbsensi();

  return (
    <main className="min-h-screen bg-gradient-to-br from-blue-400 to-white p-4 sm:p-8">
      <section className="mx-auto min-h-[calc(100vh-4rem)] w-full max-w-5xl bg-white p-4 shadow-sm sm:p-6">
        <h1 className="mb-2 text-2xl font-bold text-gray-900 sm:text-3xl">
          RIWAYAT ABSENSI
        </h1>

        <p className="mb-6 text-sm text-gray-500">
          Riwayat check-in dan check-out kamu.
        </p>

        <div className="w-full overflow-x-auto">
          <table className="w-full min-w-[600px] text-left text-sm">
            <thead className="bg-gray-100 text-gray-700">
              <tr>
                <th className="px-4 py-3">Tanggal</th>
                <th className="px-4 py-3">Jam Masuk</th>
                <th className="px-4 py-3">Jam Keluar</th>
                <th className="px-4 py-3">Status</th>
              </tr>
            </thead>

            <tbody className="divide-y divide-gray-200">
              {loading ? (
                <tr>
                  <td colSpan={4} className="px-4 py-10 text-center">
                    Memuat riwayat absensi...
                  </td>
                </tr>
              ) : error ? (
                <tr>
                  <td
                    colSpan={4}
                    className="px-4 py-10 text-center text-red-600"
                  >
                    {error}
                  </td>
                </tr>
              ) : riwayat.length === 0 ? (
                <tr>
                  <td
                    colSpan={4}
                    className="px-4 py-10 text-center text-gray-500"
                  >
                    Belum ada riwayat absensi.
                  </td>
                </tr>
              ) : (
                riwayat.map((item) => (
                  <tr key={item.id}>
                    <td className="px-4 py-3">{formatTanggal(item.tanggal)}</td>
                    <td className="px-4 py-3">{formatJam(item.jamMasuk)}</td>
                    <td className="px-4 py-3">{formatJam(item.jamKeluar)}</td>
                    <td className="px-4 py-3">{item.status || "-"}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </section>
    </main>
  );
}
