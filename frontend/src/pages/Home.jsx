import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { carService } from '../services/carService'
import CarCard from '../components/CarCard'

export default function Home() {
  const [cars, setCars] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    carService.getApprovedCars(0, 6)
      .then((data) => setCars(data.content))
      .catch(() => setCars([]))
      .finally(() => setLoading(false))
  }, [])

  return (
    <div>
      <section className="hero">
        <h1>Find Your Perfect Used Car</h1>
        <p>Buy and sell quality pre-owned cars with confidence</p>
        <Link to="/cars" className="btn btn-primary">Browse Cars</Link>
      </section>

      <section className="container">
        <h2>Featured Cars</h2>
        {loading ? (
          <p>Loading...</p>
        ) : cars.length === 0 ? (
          <p>No approved listings yet — be the first to sell a car!</p>
        ) : (
          <div className="car-grid">
            {cars.map((car) => <CarCard key={car.id} car={car} />)}
          </div>
        )}
      </section>
    </div>
  )
}
