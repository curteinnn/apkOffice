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

  const refreshAttendance = useCallback(async () => {
    const records = await getMyAttendance();
    const todayAttendance = Array.isArray(records)
      ? records.find((record) => record.tanggal === getToday())
      : null;

    setAttendance(todayAttendance ?? null);
  }, []);

  useEffect(() => {
    refreshAttendance()
      .catch((err) => setError(err.message))
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
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleCheckOut = async () => {
    if (!isCheckedIn || isCheckedOut || submitting) return;

    setSubmitting(true);
    setError("");

    try {
      await checkOut();
      await refreshAttendance();
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return {
    isCheckedIn,
    isCheckedOut,
    loading,
    submitting,
    error,
    handleCheckIn,
    handleCheckOut,
    refreshAttendance,
  };
}
