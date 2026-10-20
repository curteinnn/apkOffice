import { useState } from "react";
import useListVisit from "../hooks/useListVisit";

export default function ListVisit() {
  const { tugas, loading, error, actionLoading, updateStatus } = useListVisit();

  const [selectedTugas, setSelectedTugas] = useState(null);
  const [actionError, setActionError] = useState("");

  const formatTanggal = (tanggal) => {
    if (!tanggal) return "-";

    return new Date(`${tanggal}T00:00:00`).toLocaleDateString("id-ID", {
      day: "2-digit",
      month: "long",
      year: "numeric",
    });
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "PENDING":
        return "bg-orange-100 text-orange-700";
      case "IN_PROGRESS":
        return "bg-blue-100 text-blue-700";
      case "SUBMITTED":
        return "bg-purple-100 text-purple-700";
      case "REJECTED":
        return "bg-red-100 text-red-700";
      case "APPROVED":
        return "bg-green-100 text-green-700";
      default:
        return "bg-gray-100 text-gray-700";
    }
  };

  const handleStatus = async (action) => {
    if (!selectedTugas) return;

    try {
      setActionError("");

      const updated = await updateStatus(selectedTugas.id, action);
      setSelectedTugas(updated);
    } catch (err) {
      setActionError(err.message || "Gagal memperbarui status tugas.");
    }
  };

  const handleClose = () => {
    setSelectedTugas(null);
    setActionError("");
  };

  return (
    <main className="min-h-screen bg-linear-to-br from-blue-300 to-white p-4 sm:p-8">
      <section className="mx-auto w-full max-w-5xl py-6">
        <h1 className="px-1 text-2xl font-bold text-gray-900 sm:text-3xl">
          LIST CUSTOMER
        </h1>

        {loading ? (
          <p className="flex min-h-[60vh] items-center justify-center text-center text-gray-500">
            Memuat daftar tugas...
          </p>
        ) : error ? (
          <p className="flex min-h-[60vh] items-center justify-center text-center text-red-600">
            {error}
          </p>
        ) : tugas.length === 0 ? (
          <p className="flex min-h-[60vh] items-center justify-center text-center text-gray-500">
            Belum ada tugas.
          </p>
        ) : (
          <div className="mt-8 grid grid-cols-1 gap-5 sm:grid-cols-2">
            {tugas.map((item) => (
              <button
                key={item.id}
                type="button"
                onClick={() => {
                  setSelectedTugas(item);
                  setActionError("");
                }}
                className="flex min-h-40 flex-col rounded-xl bg-white p-6 text-left transition hover:bg-blue-100"
              >
                <h2 className="text-lg font-semibold text-blue-950">
                  {item.judul || "Tanpa judul"}
                </h2>

                <p className="mt-2 line-clamp-2 text-sm text-blue-800">
                  {item.deskripsi || "Tidak ada deskripsi."}
                </p>

                <div className="mt-auto flex items-center justify-between gap-2 pt-4 text-sm text-blue-950">
                  <span>{formatTanggal(item.tanggal)}</span>

                  <span
                    className={`rounded-full px-3 py-1 text-xs font-medium ${getStatusClass(item.status)}`}
                  >
                    {item.status || "PENDING"}
                  </span>
                </div>

                <span className="mt-3 text-xs text-blue-900">
                  Lihat detail →
                </span>
              </button>
            ))}
          </div>
        )}
      </section>

      {selectedTugas && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center overflow-y-auto bg-black/50 p-4"
          onClick={handleClose}
        >
          <section
            role="dialog"
            aria-modal="true"
            aria-labelledby="detail-tugas-title"
            className="my-auto w-full max-w-lg rounded-xl bg-white p-6 shadow-xl"
            onClick={(event) => event.stopPropagation()}
          >
            <div className="flex items-start justify-between gap-4">
              <h2
                id="detail-tugas-title"
                className="text-xl font-bold text-gray-900"
              >
                Detail Tugas
              </h2>

              <button
                type="button"
                onClick={handleClose}
                className="text-2xl leading-none text-gray-500 hover:text-gray-900"
                aria-label="Tutup detail"
              >
                &times;
              </button>
            </div>

            <div className="mt-6 space-y-4">
              <div>
                <p className="text-sm text-gray-500">Judul</p>
                <p className="font-semibold text-gray-900">
                  {selectedTugas.judul || "Tanpa judul"}
                </p>
              </div>

              <div>
                <p className="text-sm text-gray-500">Deskripsi</p>
                <p className="whitespace-pre-wrap text-gray-800">
                  {selectedTugas.deskripsi || "Tidak ada deskripsi."}
                </p>
              </div>

              <div>
                <p className="text-sm text-gray-500">Lokasi</p>
                <p className="text-gray-800">{selectedTugas.lokasi || "-"}</p>
              </div>

              <div>
                <p className="text-sm text-gray-500">Google Maps</p>

                {selectedTugas.linkGmaps ? (
                  <a
                    href={selectedTugas.linkGmaps}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="mt-1 inline-block text-blue-600 underline hover:text-blue-800"
                  >
                    Buka lokasi di Google Maps
                  </a>
                ) : (
                  <p className="mt-1 text-gray-500">Link belum tersedia.</p>
                )}
              </div>

              <div>
                <p className="text-sm text-gray-500">Tanggal</p>
                <p className="text-gray-800">
                  {formatTanggal(selectedTugas.tanggal)}
                </p>
              </div>

              <div>
                <p className="text-sm text-gray-500">Status</p>
                <span
                  className={`mt-2 inline-block rounded-full px-3 py-1 text-sm font-medium ${getStatusClass(selectedTugas.status)}`}
                >
                  {selectedTugas.status || "PENDING"}
                </span>
              </div>
            </div>

            {actionError && (
              <p className="mt-4 rounded-lg bg-red-50 p-3 text-sm text-red-600">
                {actionError}
              </p>
            )}

            {selectedTugas.status === "PENDING" && (
              <button
                type="button"
                disabled={actionLoading}
                onClick={() => handleStatus("start")}
                className="mt-6 w-full rounded-lg bg-blue-600 px-4 py-2.5 font-medium text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {actionLoading ? "Memproses..." : "Mulai Tugas"}
              </button>
            )}

            {["IN_PROGRESS", "REJECTED"].includes(selectedTugas.status) && (
              <button
                type="button"
                disabled={actionLoading}
                onClick={() => handleStatus("submit")}
                className="mt-6 w-full rounded-lg bg-green-600 px-4 py-2.5 font-medium text-white transition hover:bg-green-700 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {actionLoading ? "Mengirim..." : "Selesaikan Tugas"}
              </button>
            )}

            {selectedTugas.status === "SUBMITTED" && (
              <p className="mt-6 rounded-lg bg-purple-50 p-3 text-center text-sm font-medium text-purple-800">
                Tugas sudah dikirim dan menunggu verifikasi admin.
              </p>
            )}

            {selectedTugas.status === "APPROVED" && (
              <p className="mt-6 rounded-lg bg-green-50 p-3 text-center text-sm font-medium text-green-800">
                Tugas telah disetujui admin.
              </p>
            )}

            <button
              type="button"
              onClick={handleClose}
              className="mt-3 w-full rounded-lg border border-gray-300 px-4 py-2.5 font-medium text-gray-700 transition hover:bg-gray-50"
            >
              Tutup
            </button>
          </section>
        </div>
      )}
    </main>
  );
}
