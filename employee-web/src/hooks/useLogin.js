
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../services/api";

export default function useLogin() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setLoading(true);

    try {
      
      const data = await login(username, password);

      if (data.role !== "EMPLOYEE") {
        throw new Error("Akun ini tidak memiliki akses ke Employee Web");
      }

      sessionStorage.setItem("token", data.token);

      sessionStorage.setItem(
        "user",
        JSON.stringify({
          id: data.id,
          username: data.username,
          namaLengkap: data.namaLengkap,
          email: data.email,
          jabatan: data.jabatan,
          role: data.role,
          foto: data.foto,
          status: data.status,
        })
      );

      navigate("/dashboard", { replace: true });
    } catch (error) {
      setError(error.message || "Login gagal");
    } finally {
      setLoading(false);
    }
  };

  return {
    username,
    setUsername,
    password,
    setPassword,
    error,
    loading,
    handleSubmit,
  };
}