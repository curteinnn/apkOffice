import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  getMyAttendance,
  checkIn,
  checkOut,
} from "../services/attendanceService";

function getToday() {
  const now = new Date();

  return [
    now.getFullYear(),
    String(now.getMonth() + 1).padStart(2, "0"),
    String(now.getDate()).padStart(2, "0"),
  ].join("-");
}

export function useAttendance() {
  const navigate = useNavigate();

  const [attendance, setAttendance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const checkedIn = Boolean(attendance?.jamMasuk);
  const checkedOut = Boolean(attendance?.jamKeluar);

  const loadAttendance = useCallback(async () => {
    if (!sessionStorage.getItem("token")) {
      navigate("/", { replace: true });
      return;
    }

    setLoading(true);
    setError("");

    try {
      const records = await getMyAttendance();

      if (!Array.isArray(records)) {
        throw new Error("Data absensi tidak valid.");
      }

      const todayRecord =
        records.find((record) => record.tanggal === getToday()) ?? null;

      setAttendance(todayRecord);
    } catch (err) {
      setError(err.message || "Gagal mengambil data absensi.");
    } finally {
      setLoading(false);
    }
  }, [navigate]);

  useEffect(() => {
    loadAttendance();
  }, [loadAttendance]);

  const submitAttendance = async (action) => {
    if (loading || submitting) return;

    setSubmitting(true);
    setError("");
    setSuccess("");

    try {
      if (action === "check-in") {
        await checkIn();
        setSuccess("Absen masuk berhasil.");
      } else {
        await checkOut();
        setSuccess("Absen pulang berhasil.");
      }

      await loadAttendance();
    } catch (err) {
      setError(err.message || "Gagal menyimpan absensi.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleCheckIn = () => submitAttendance("check-in");
  const handleCheckOut = () => submitAttendance("check-out");

  const handleLogout = () => {
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("user");
    navigate("/", { replace: true });
  };

  return {
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
  };
}
