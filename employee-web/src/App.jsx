import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import ProtectedRoute from "./components/protectedRoute";
import Login from "./pages/login";
import Dashboard from "./pages/dashboard";
import ListVisit from "./pages/listVisit";
import AbsenKeluar from "./pages/absenKeluar";
import RiwayatAbsensi from "./pages/riwayatAbsensi";
import AbsenMasuk from "./pages/absenMasuk";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />

        <Route path="/preview-dashboard" element={<Dashboard />} />
        <Route path="/preview-listVisit" element={<ListVisit />} />

        <Route element={<ProtectedRoute />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/listVisit" element={<ListVisit />} />
          <Route path="/absenKeluar" element={<AbsenKeluar />} />
          <Route path="/absenMasuk" element={<AbsenMasuk />} />
          <Route path="/riwayatAbsensi" element={<RiwayatAbsensi />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
