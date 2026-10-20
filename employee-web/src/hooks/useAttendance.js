import { useCallback, useEffect, useState } from "react";
import {
  getMyAttendance,
  checkIn,
  checkOut,
} from "../services/attendanceService";

function getToday() {
  const today = new Date();

  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0");
  const day = String(today.getDate()).padStart(2, "0");

  return `${year}-${month}-${day}`;
}

export default function useAttendance() {
  const [attendance, setAttendance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [checkoutWarning, setCheckoutWarning] = useState("");

  const refreshAttendance = useCallback(async () => {
    const records = await getMyAttendance();

    console.log("Data attendance:", records);
    console.log("Tanggal hari ini:", getToday());

    const todayAttendance = Array.isArray(records)
      ? records.find((record) => record.tanggal === getToday())
      : null;

    console.log("Attendance hari ini:", todayAttendance);

    setAttendance(todayAttendance ?? null);
  }, []);

  useEffect(() => {
    refreshAttendance()
      .catch((err) => setError(err.message || "Gagal memuat absensi."))
      .finally(() => setLoading(false));
  }, [refreshAttendance]);

  const isCheckedIn = Boolean(attendance?.jamMasuk);
  const isCheckedOut = Boolean(attendance?.jamKeluar);

  const handleCheckIn = async () => {
    if (isCheckedIn || submitting) return;

    setSubmitting(true);
    setError("");

    try {
      await checkIn();
      await refreshAttendance();
    } catch (err) {
      setError(err.message || "Gagal melakukan check-in.");
    } finally {
      setSubmitting(false);
    }
  };

  const handleCheckOut = async () => {
    if (!isCheckedIn || isCheckedOut || submitting) return;

    setSubmitting(true);
    setError("");
    setCheckoutWarning("");

    try {
      await checkOut();
      await refreshAttendance();
    } catch (err) {
      const message = err.message || "";

      if (
        message.toLowerCase().includes("tugas") ||
        message.toLowerCase().includes("task")
      ) {
        setCheckoutWarning(
          "Anda belum menyelesaikan tugas. Selesaikan minimal satu tugas terlebih dahulu sebelum check-out.",
        );
      } else {
        setError(message || "Gagal melakukan check-out.");
      }
    } finally {
      setSubmitting(false);
    }
  };

  const closeCheckoutWarning = () => {
    setCheckoutWarning("");
  };

  return {
    isCheckedIn,
    isCheckedOut,
    loading,
    submitting,
    error,
    checkoutWarning,
    closeCheckoutWarning,
    handleCheckIn,
    handleCheckOut,
    refreshAttendance,
  };
}
