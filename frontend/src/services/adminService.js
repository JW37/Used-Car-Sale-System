import api from './api'

export const adminService = {
  async pendingCars() {
    const { data } = await api.get('/api/admin/cars/pending')
    return data
  },
  async approve(id) {
    const { data } = await api.patch(`/api/admin/cars/${id}/approve`)
    return data
  },
  async reject(id) {
    const { data } = await api.patch(`/api/admin/cars/${id}/reject`)
    return data
  },
}
