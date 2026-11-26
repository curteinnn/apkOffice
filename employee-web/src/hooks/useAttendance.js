import { useCallback, useEffect, useState } from "react";
import {
  getMyAttendance,
  checkIn,
  checkOut,
} from "../services/attendanceService";
import { getTodayWIB } from "../utils/date";

export default function useAttendance() {
  const [attendance, setAttendance] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [checkoutWarning, setCheckoutWarning] = useState("");

  const loadTodayAttendance = useCallback(async () => {
    const records = await getMyAttendance();

    return Array.isArray(records)
      ? records.find((record) => record.tanggal === getTodayWIB())
      : null;
  }, []);

  const refreshAttendance = useCallback(async () => {
    const todayAttendance = await loadTodayAttendance();
    setAttendance(todayAttendance ?? null);
  }, [loadTodayAttendance]);

  useEffect(() => {
    let active = true;

    loadTodayAttendance()
      .then((todayAttendance) => {
        if (active) setAttendance(todayAttendance ?? null);
      })
      .catch((err) => {
        if (active) setError(err.message || "Gagal memuat absensi.");
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, [loadTodayAttendance]);

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
          message || "Masih ada tugas yang belum selesai sebelum check-out.",
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
