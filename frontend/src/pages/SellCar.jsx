import React from 'react'
import { useNavigate } from 'react-router-dom'
import CarForm from '../components/CarForm'
import { carService } from '../services/carService'

export default function SellCar() {
  const navigate = useNavigate()

  const handleCreate = async (payload, files) => {
    const car = await carService.create(payload)

    if (files && files.length > 0) {
      const uploads = await Promise.all(
        files.map((file) => carService.uploadImage(car.id, file))
      )
      const imageUrls = uploads.map((u) => u.imageUrl)
      await carService.update(car.id, { ...payload, imageUrls })
    }
    navigate('/my-listings', { state: { justCreatedId: car.id } })
  }

  return (
    <div className="container">
      <h2>List Your Car</h2>
      <p className="hint">Your listing will be reviewed by an admin before it appears in the marketplace. You can add up to 6 photos.</p>
      <CarForm onSubmit={handleCreate} submitLabel="List Car" />
    </div>
  )
}
