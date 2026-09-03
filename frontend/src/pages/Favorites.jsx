import React, { useEffect, useState } from 'react'
import { favoriteService } from '../services/favoriteService'
import CarCard from '../components/CarCard'

export default function Favorites() {
  const [cars, setCars] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    favoriteService.list().then(setCars).finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="container"><p>Loading...</p></div>

  return (
    <div className="container">
      <h2>My Favorites</h2>
      {cars.length === 0 ? (
        <p>No saved cars yet. Browse the marketplace and tap "Save Car" on ones you like.</p>
      ) : (
        <div className="car-grid">
          {cars.map((car) => <CarCard key={car.id} car={car} />)}
        </div>
      )}
    </div>
  )
}
