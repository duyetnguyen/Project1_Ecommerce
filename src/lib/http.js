import axios from "axios";

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || "http://localhost:8080",
  withCredentials: false, // set true if you use cookies for auth
});

// Optional: attach Bearer token if you have auth
http.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Normalize Spring error shapes into { status, message, fields }
http.interceptors.response.use(
  (res) => res,
  (err) => {
    const status = err?.response?.status;
    const data = err?.response?.data;
    let normalized = { status, message: "Request failed", fields: null };
    if (data?.error === "validation_failed") {
      normalized.message = "Validation failed";
      normalized.fields = data.fields || null;
    } else if (data?.error) {
      normalized.message = data.message || data.error;
    } else if (typeof data === "string") {
      normalized.message = data;
    }
    return Promise.reject(normalized);
  }
);

export default http;