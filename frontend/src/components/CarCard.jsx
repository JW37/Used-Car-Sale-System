import React from 'react'
import { Link } from 'react-router-dom'

export default function CarCard({ car }) {
  const image = car.imageUrls && car.imageUrls.length > 0 ? car.imageUrls[0] : null

  return (
    <div className="car-card">
      <div className="car-card-image">
        {image ? <img src={image} alt={`${car.brand} ${car.model}`} /> : <div className="image-placeholder">No Image</div>}
      </div>
      <div className="car-card-body">
        <h3>{car.brand} {car.model}</h3>
        <p className="price">₹{Number(car.price).toLocaleString('en-IN')}</p>
        <p className="meta">{car.year} • {car.fuelType} • {car.transmission}</p>
        <p className="meta">{car.kilometers?.toLocaleString('en-IN')} km • {car.location}</p>
        <Link to={`/cars/${car.id}`} className="btn btn-outline">View Details</Link>
      </div>
    </div>
  )
}
