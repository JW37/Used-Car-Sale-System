import api from './api'

export const carService = {
  async getApprovedCars(page = 0, size = 12) {
    const { data } = await api.get('/api/cars', { params: { page, size } })
    return data
  },
  async search(filters, page = 0, size = 12) {
    const { data } = await api.get('/api/cars/search', { params: { ...filters, page, size } })
    return data
  },
  async getById(id) {
    const { data } = await api.get(`/api/cars/${id}`)
    return data
  },
  async myListings() {
    const { data } = await api.get('/api/cars/my-listings')
    return data
  },
  async create(payload) {
    const { data } = await api.post('/api/cars', payload)
    return data
  },
  async update(id, payload) {
    const { data } = await api.put(`/api/cars/${id}`, payload)
    return data
  },
  async remove(id) {
    await api.delete(`/api/cars/${id}`)
  },
  async markSold(id) {
    const { data } = await api.patch(`/api/cars/${id}/sold`)
    return data
  },
  async uploadImage(carId, file) {
    const formData = new FormData()
    formData.append('file', file)
    const { data } = await api.post(`/api/cars/${carId}/images`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    return data
  },
}
