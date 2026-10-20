import { useEffect, useState } from "react";
import { apiRequest } from "../services/api";

export default function useListVisit() {
  const [tugas, setTugas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    apiRequest("/api/tugas")
      .then((data) => setTugas(Array.isArray(data) ? data : []))
      .catch((err) => setError(err.message || "Gagal memuat daftar tugas."))
      .finally(() => setLoading(false));
  }, []);

  return { tugas, loading, error };
}
