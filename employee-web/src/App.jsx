import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import ProtectedRoute from "./components/protectedRoute";
import Login from "./pages/login";
import Dashboard from "./pages/dashboard";
import ListVisit from "./pages/listVisit";
import RiwayatAbsensi from "./pages/riwayatAbsensi";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />

        <Route path="/preview-dashboard" element={<Dashboard />} />
        <Route path="/preview-listVisit" element={<ListVisit />} />
        <Route path="/preview-riwayatAbsensi" element={<RiwayatAbsensi />} />

        <Route element={<ProtectedRoute />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/listVisit" element={<ListVisit />} />
          <Route path="/riwayatAbsensi" element={<RiwayatAbsensi />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
