import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import ProtectedRoute from "./components/protectedRoute";
import Login from "./pages/login";
import Dashboard from "./pages/dashboard";
import ListTugas from "./pages/listTugas";
import RiwayatAbsensi from "./pages/riwayatAbsensi";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />

        <Route path="/preview-dashboard" element={<Dashboard />} />
        <Route path="/preview-list-tugas" element={<ListTugas />} />
        <Route path="/preview-riwayatAbsensi" element={<RiwayatAbsensi />} />

        <Route element={<ProtectedRoute />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/list-tugas" element={<ListTugas />} />
          <Route path="/riwayatAbsensi" element={<RiwayatAbsensi />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
