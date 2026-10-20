import { useEffect, useState } from "react";
import { getMyAttendance } from "../services/attendanceService";

export default function useRiwayatAbsensi() {
  const [riwayat, setRiwayat] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    getMyAttendance()
      .then(setRiwayat)
      .catch((err) => {
        setError(err.message || "Gagal memuat riwayat absensi.");
      })
      .finally(() => setLoading(false));
  }, []);

  const formatTanggal = (tanggal) => {
    if (!tanggal) return "-";

    return new Date(`${tanggal}T00:00:00`).toLocaleDateString("id-ID", {
      day: "2-digit",
      month: "long",
      year: "numeric",
    });
  };

  const formatJam = (waktu) => {
    if (!waktu) return "-";

    return new Date(waktu).toLocaleTimeString("id-ID", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    });
  };

  return {
    riwayat,
    loading,
    error,
    formatTanggal,
    formatJam,
  };
}
