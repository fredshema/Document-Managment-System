import axios from "axios";

const API_URL = process.env.REACT_APP_API_URL || "https://dms-api.fly.dev";

console.log(process.env)

const axiosInstance = axios.create({
  baseURL: API_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) config.headers.Authorization = `Bearer ${token}`;

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response.status === 403) {
      localStorage.clear();
      window.location.reload();
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
