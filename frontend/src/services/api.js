import axios from 'axios'

// One shared axios instance for the whole app. Base URL is empty because
// the Vite dev proxy forwards "/api/*" to Spring Boot (see vite.config.js).
// In a production build you'd set VITE_API_URL to the real backend origin.
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '',
})

// Attach the JWT to every outgoing request automatically, instead of
// remembering to add the header manually in every service call.
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('automart_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// If the token is invalid/expired, the backend returns 401. We catch that
// globally, clear the stale session, and let the UI redirect to /login.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('automart_token')
      localStorage.removeItem('automart_user')
    }
    return Promise.reject(error)
  }
)

export default api
