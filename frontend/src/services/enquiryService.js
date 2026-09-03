import api from './api'

export const enquiryService = {
  async create(payload) {
    const { data } = await api.post('/api/enquiries', payload)
    return data
  },
  async myEnquiries() {
    const { data } = await api.get('/api/enquiries/my')
    return data
  },
  async sellerEnquiries() {
    const { data } = await api.get('/api/enquiries/seller')
    return data
  },
}
