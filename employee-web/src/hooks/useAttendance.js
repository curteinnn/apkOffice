import { useState } from "react";
import { getAttendance, saveAttendance } from "../utils/attendanceStorage";

export default function useAttendance(attendanceKey) {
  const [attendance, setAttendance] = useState(() =>
    getAttendance(attendanceKey),
  );

  const isCheckedIn = attendance.checkedIn;
  const isCheckedOut = attendance.checkedOut;

  const updateAttendance = (updates) => {
    const updated = {
      ...attendance,
      ...updates,
      date: new Date().toLocaleDateString("en-CA"),
    };

    setAttendance(updated);
    saveAttendance(attendanceKey, updated);
  };

  const handleCheckIn = () => {
    if (isCheckedIn) return;

    updateAttendance({
      checkedIn: true,
      checkedOut: false,
    });
  };

  const handleCheckOut = () => {
    if (!isCheckedIn || isCheckedOut) return;

    updateAttendance({ checkedOut: true });
  };

  return {
    isCheckedIn,
    isCheckedOut,
    handleCheckIn,
    handleCheckOut,
  };
}
