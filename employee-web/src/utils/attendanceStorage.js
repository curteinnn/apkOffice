const getToday = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");

  return `${year}-${month}-${day}`;
};

export const getAttendance = (key = "attendance") => {
  try {
    const saved = localStorage.getItem(key);

    if (saved) {
      const data = JSON.parse(saved);

      if (data.date === getToday()) {
        return data;
      }
    }
  } catch (error) {
    console.error("Gagal membaca absensi:", error);
  }

  return {
    date: getToday(),
    checkedIn: false,
    checkedOut: false,
  };
};

export const saveAttendance = (key, attendance) => {
  try {
    localStorage.setItem(key, JSON.stringify(attendance));
  } catch (error) {
    console.error("Gagal menyimpan absensi:", error);
  }
};
