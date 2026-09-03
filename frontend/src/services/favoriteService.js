import api from './api'

export const favoriteService = {
  async list() {
    const { data } = await api.get('/api/favorites')
    return data
  },
  async add(carId) {
    await api.post(`/api/favorites/${carId}`)
  },
  async remove(carId) {
    await api.delete(`/api/favorites/${carId}`)
  },
}
