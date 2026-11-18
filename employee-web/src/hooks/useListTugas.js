import { useCallback, useEffect, useState } from "react";
import { apiRequest } from "../services/api";
import { getTodayWIB } from "../utils/date";

export default function useListTugas() {
  const [tugas, setTugas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionLoading, setActionLoading] = useState(false);
  const [accessDenied, setAccessDenied] = useState(false);
  const [isCheckedOut, setIsCheckedOut] = useState(false);

  const loadTugasData = useCallback(async () => {
    const attendance = await apiRequest("/api/attendance/my");
    const attendanceHariIni = Array.isArray(attendance)
      ? attendance.find((item) => item.tanggal === getTodayWIB())
      : null;

    if (!attendanceHariIni?.jamMasuk) {
      return { tugas: [], accessDenied: true, isCheckedOut: false };
    }

    const data = await apiRequest("/api/tugas");
    return {
      tugas: Array.isArray(data) ? data : [],
      accessDenied: false,
      isCheckedOut: Boolean(attendanceHariIni.jamKeluar),
    };
  }, []);

  const applyTugasData = useCallback((data) => {
    setTugas(data.tugas);
    setAccessDenied(data.accessDenied);
    setIsCheckedOut(data.isCheckedOut);
  }, []);

  const fetchTugas = useCallback(async () => {
    setLoading(true);
    setError("");
    setAccessDenied(false);

    try {
      applyTugasData(await loadTugasData());
    } catch (err) {
      setError(err.message || "Gagal memuat daftar tugas.");
    } finally {
      setLoading(false);
    }
  }, [applyTugasData, loadTugasData]);

  useEffect(() => {
    let active = true;

    loadTugasData()
      .then((data) => {
        if (active) applyTugasData(data);
      })
      .catch((err) => {
        if (active) setError(err.message || "Gagal memuat daftar tugas.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [applyTugasData, loadTugasData]);

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
