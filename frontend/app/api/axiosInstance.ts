import axios from "axios";

const baseURL = "http://127.0.0.1:9999/api/v1";

// Public Axios Instance - For requests that don't need authentication
export const publicAxios = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Private Axios Instance - For requests that need authentication
export const privateAxios = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor for adding auth token to privateAxios
privateAxios.interceptors.request.use(
  (config) => {
    const data = localStorage.getItem("auth_data");
    if (data) {
      try {
        const { accessToken } = JSON.parse(data);
        if (accessToken) {
          config.headers.Authorization = `Bearer ${accessToken}`;
        }
      } catch (e) {
        console.error("Error parsing auth data from localStorage", e);
      }
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for privateAxios to handle token expiry (401)
let isRefreshing = false;
let failedQueue: any[] = [];

const processQueue = (error: any, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve(token);
    }
  });
  failedQueue = [];
};

privateAxios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Check if error is 401 (Unauthorized) and it's not a retry already
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`;
            return privateAxios(originalRequest);
          })
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        const data = localStorage.getItem("auth_data");
        if (data) {
          const { refreshToken } = JSON.parse(data);
          
          // Using standard axios here to avoid interceptors on the refresh call
          const response = await axios.post(`${baseURL}/auth/refresh-token`, {
            refreshToken: refreshToken
          });

          if (response.data.status === "SUCCESS") {
            const newData = response.data.data;
            localStorage.setItem("auth_data", JSON.stringify(newData));
            
            // Dispatch custom event to sync with Redux if needed, 
            // but for now updating localStorage is the source for next requests
            processQueue(null, newData.accessToken);
            
            originalRequest.headers.Authorization = `Bearer ${newData.accessToken}`;
            return privateAxios(originalRequest);
          }
        }
      } catch (refreshError) {
        processQueue(refreshError, null);
        // Clear storage and redirect to login if refresh fails
        localStorage.removeItem("auth_data");
        window.location.href = "/login";
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);
