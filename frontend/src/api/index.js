import axios from "axios";

const api = axios.create({
  baseURL: "/api",
  timeout: 10000,
});

// 请求拦截器：自动带上 token
api.interceptors.request.use((config) => {
  const user = localStorage.getItem("loginUser");
  if (user) {
    const data = JSON.parse(user);
    if (data.token) {
      config.headers.Authorization = "Bearer " + data.token;
    }
  }
  return config;
});

// 响应拦截器：401 跳转登录
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("loginUser");
      window.location.href = "/login";
      return Promise.reject({ userMessage: "登录已过期，请重新登录" });
    }
    const message = error.response?.data?.message || "请求失败";
    return Promise.reject({ ...error, userMessage: message });
  }
);

export default api;
