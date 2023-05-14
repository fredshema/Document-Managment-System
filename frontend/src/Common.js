import axios from "axios";

const API_URL = process.env.REACT_APP_API_URL || "https://dms-api.fly.dev";
const token = localStorage.getItem("token");

const axiosInstance = axios.create({
  baseURL: API_URL,
  headers: {
    "Content-Type": "application/json",
    Authorization: token ? `Bearer ${token}` : "",
  },
});

axiosInstance.interceptors.response.use(
  (response) => {
    if (response.status === 403) {
      localStorage.clear();
      window.location.reload();
    }

    return response;
  },
  (error) => {
    if (error.response.status === 403) {
      localStorage.clear();
      window.location.reload();
    }
  }
);

export default axiosInstance;
