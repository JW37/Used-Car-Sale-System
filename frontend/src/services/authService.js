import api from './api'

export const authService = {
  async register(payload) {
    const { data } = await api.post('/api/auth/register', payload)
    return data
  },
  async login(payload) {
    const { data } = await api.post('/api/auth/login', payload)
    return data
  },
}
