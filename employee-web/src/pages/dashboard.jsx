import { useNavigate } from "react-router-dom";
import { animationDashboard } from "../animations/animationDashboard";
import { useGSAP } from "@gsap/react";
import { useState } from "react";
import { getAttendance, saveAttendance } from "../utils/attendanceStorage";

export default function Dashboard() {
  const navigate = useNavigate();
  const user = JSON.parse(sessionStorage.getItem("user") || "null");

  const attendanceKey = `myin-attendance-${
    user?.id ?? user?.email ?? user?.username ?? user?.namaLengkap ?? "default"
  }`;

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

    updateAttendance({
      checkedOut: true,
    });
  };

  const handleLogout = () => {
    sessionStorage.removeItem("token");
    sessionStorage.removeItem("user");

    navigate("/", { replace: true });
  };

  useGSAP(() => {
    animationDashboard();
  });

  return (
    <main className="flex min-h-screen items-center justify-center bg-linear-to-br from-blue-400 to-white p-4 sm:p-8">
      <section className="w-full max-w-5xl py-3">
        <header className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="title px-1 text-2xl font-bold text-gray-900 sm:text-3xl">
              WELCOME TO MYIN
            </h1>

            <p className="mt-2 px-1 text-sm text-gray-700">
              Halo, {user?.namaLengkap || "Karyawan"}!
            </p>
          </div>

          <div className="rounded-lg bg-white/70 px-4 py-3 shadow-sm">
            <p className="text-sm text-gray-500">Jabatan</p>
            <p className="font-medium text-gray-800">{user?.jabatan || "-"}</p>
          </div>
        </header>

        {/* Informasi Absensi */}
        <div className="mt-8 grid grid-cols-1 gap-4 lg:grid-cols-2">
          <div className="flex min-h-20 items-center justify-between rounded-xl bg-white p-5">
            <div>
              <p className="font-medium text-blue-950">Absen Masuk</p>
              <p className="mt-1 text-xs text-gray-500">
                {isCheckedIn ? "Sudah absen hari ini" : "Belum absen"}
              </p>
            </div>

            <button
              type="button"
              onClick={handleCheckIn}
              disabled={isCheckedIn}
              className={`rounded-lg px-5 py-2 text-sm text-white transition ${
                isCheckedIn
                  ? "cursor-not-allowed bg-green-500"
                  : "cursor-pointer bg-blue-500 hover:bg-blue-600"
              }`}
            >
              {isCheckedIn ? "Sudah Check In" : "Check In"}
            </button>
          </div>

          <div className="flex min-h-20 items-center justify-between rounded-xl bg-white p-5">
            <div>
              <p className="font-medium text-blue-950">Absen Keluar</p>
              <p className="mt-1 text-xs text-gray-500">
                {isCheckedOut
                  ? "Sudah absen keluar"
                  : isCheckedIn
                    ? "Belum absen keluar"
                    : "Check In terlebih dahulu"}
              </p>
            </div>

            <button
              type="button"
              onClick={handleCheckOut}
              disabled={!isCheckedIn || isCheckedOut}
              className={`rounded-lg px-4 py-2 text-sm text-white transition ${
                !isCheckedIn || isCheckedOut
                  ? "cursor-not-allowed bg-gray-400"
                  : "cursor-pointer bg-blue-500 hover:bg-blue-600"
              }`}
            >
              {isCheckedOut ? "Sudah Check Out" : "Check Out"}
            </button>
          </div>
        </div>

        {/* Menu Dashboard */}
        <div className="mt-8 grid grid-cols-1 gap-5 sm:grid-cols-2">
          <button
            type="button"
            onClick={() => navigate("/listVisit")}
            className="card-dashboard flex min-h-40 flex-col items-center justify-center rounded-xl bg-blue-200 p-6 text-center transition hover:bg-blue-300"
          >
            <h2 className="text-lg font-semibold text-blue-950">List Visit</h2>

            <p className="mt-2 text-sm text-blue-800">
              Lorem, ipsum dolor sit amet consectetur adipisicing elit. Alias,
            </p>
          </button>

          <button
            type="button"
            onClick={() => navigate("/riwayatAbsensi")}
            className="card-dashboard flex min-h-40 flex-col items-center justify-center rounded-xl bg-blue-200 p-6 text-center transition hover:bg-blue-300"
          >
            <h2 className="text-lg font-semibold text-blue-950">
              Riwayat Absensi
            </h2>

            <p className="mt-2 text-sm text-blue-800">
              Lorem ipsum, dolor sit amet consectetur adipisicing elit. Maxime
              nostrum quia, minus aliquid error tempora non exercitationem.
            </p>
          </button>

          <button
            type="button"
            onClick={handleLogout}
            className="card-dashboard w-20 rounded-lg bg-red-500 px-4 py-2 text-white transition hover:bg-red-600"
          >
            Logout
          </button>
        </div>
      </section>
    </main>
  );
}
