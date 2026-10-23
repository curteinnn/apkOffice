import { useCallback, useEffect, useState } from "react";
import { apiRequest } from "../services/api";

const getTanggalWIB = () => {
  const parts = new Intl.DateTimeFormat("en-GB", {
    timeZone: "Asia/Jakarta",
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }).formatToParts(new Date());

  const values = Object.fromEntries(
    parts.map(({ type, value }) => [type, value]),
  );

  return `${values.year}-${values.month}-${values.day}`;
};

export default function useListVisit() {
  const [tugas, setTugas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionLoading, setActionLoading] = useState(false);
  const [accessDenied, setAccessDenied] = useState(false);
  const [isCheckedOut, setIsCheckedOut] = useState(false);

  const fetchTugas = useCallback(async () => {
    setLoading(true);
    setError("");
    setAccessDenied(false);

    try {
      const attendance = await apiRequest("/api/attendance/my");
      const hariIni = getTanggalWIB();

      const attendanceHariIni = Array.isArray(attendance)
        ? attendance.find((item) => item.tanggal === hariIni)
        : null;

      // Belum check-in: tidak boleh membuka List Visit.
      if (!attendanceHariIni?.jamMasuk) {
        setTugas([]);
        setAccessDenied(true);
        setIsCheckedOut(false);
        return;
      }

      // Sudah check-out: tetap boleh melihat daftar dan status tugas.
      setIsCheckedOut(Boolean(attendanceHariIni.jamKeluar));

      const data = await apiRequest("/api/tugas");
      setTugas(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(err.message || "Gagal memuat daftar tugas.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTugas();
  }, [fetchTugas]);

  const updateStatus = async (id, action) => {
    // Perlindungan di frontend; backend juga wajib memeriksa absensi.
    if (isCheckedOut) {
      const message =
        "Anda sudah check-out. Tugas hanya bisa dilihat, tidak bisa diubah.";

      setError(message);
      throw new Error(message);
    }

    setActionLoading(true);
    setError("");

    try {
      const updated = await apiRequest(`/api/tugas/${id}/${action}`, {
        method: "PUT",
      });

      setTugas((current) =>
        current.map((item) => (item.id === id ? updated : item)),
      );

      return updated;
    } catch (err) {
      setError(err.message || "Gagal memperbarui status tugas.");
      throw err;
    } finally {
      setActionLoading(false);
    }
  };

  return {
    tugas,
    loading,
    error,
    actionLoading,
    accessDenied,
    isCheckedOut,
    updateStatus,
    refreshTugas: fetchTugas,
  };
}
