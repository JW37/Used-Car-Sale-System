import React, { useEffect, useState } from 'react'
import { carService } from '../services/carService'

const STATUS_COLORS = {
  PENDING: 'badge-pending',
  APPROVED: 'badge-approved',
  REJECTED: 'badge-rejected',
  SOLD: 'badge-sold',
}

export default function MyListings() {
  const [cars, setCars] = useState([])
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    carService.myListings().then(setCars).finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this listing?')) return
    await carService.remove(id)
    load()
  }

  const handleMarkSold = async (id) => {
    await carService.markSold(id)
    load()
  }

  if (loading) return <div className="container"><p>Loading...</p></div>

  return (
    <div className="container">
      <h2>My Listings</h2>
      {cars.length === 0 ? (
        <p>You haven't listed any cars yet.</p>
      ) : (
        <table className="listings-table">
          <thead>
            <tr><th>Car</th><th>Price</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {cars.map((car) => (
              <tr key={car.id}>
                <td>{car.brand} {car.model} ({car.year})</td>
                <td>₹{Number(car.price).toLocaleString('en-IN')}</td>
                <td><span className={`badge ${STATUS_COLORS[car.status]}`}>{car.status}</span></td>
                <td>
                  {car.status !== 'SOLD' && (
                    <button className="btn-link" onClick={() => handleMarkSold(car.id)}>Mark Sold</button>
                  )}
                  <button className="btn-link danger" onClick={() => handleDelete(car.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
