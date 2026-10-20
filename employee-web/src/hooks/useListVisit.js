import { useCallback, useEffect, useState } from "react";
import { apiRequest } from "../services/api";

export default function useListVisit() {
  const [tugas, setTugas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionLoading, setActionLoading] = useState(false);

  const fetchTugas = useCallback(async () => {
    try {
      const data = await apiRequest("/api/tugas");
      setTugas(Array.isArray(data) ? data : []);
      setError("");
    } catch (err) {
      setError(err.message || "Gagal memuat daftar tugas.");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchTugas();
  }, [fetchTugas]);

  const updateStatus = async (id, action) => {
    setActionLoading(true);
    setError("");

    try {
      const updated = await apiRequest(`/api/tugas/${id}/${action}`, {
        method: "PUT",
      });

      setTugas((current) =>
        current.map((item) => (item.id === id ? updated : item)),
      );

      return updated;
    } catch (err) {
      setError(err.message || "Gagal memperbarui status tugas.");
      throw err;
    } finally {
      setActionLoading(false);
    }
  };

  return {
    tugas,
    loading,
    error,
    actionLoading,
    updateStatus,
  };
}
