import React, { useEffect, useState } from 'react'
import { adminService } from '../services/adminService'

export default function AdminDashboard() {
  const [pending, setPending] = useState([])
  const [loading, setLoading] = useState(true)

  const load = () => {
    setLoading(true)
    adminService.pendingCars().then(setPending).finally(() => setLoading(false))
  }

  useEffect(() => { load() }, [])

  const handleApprove = async (id) => {
    await adminService.approve(id)
    load()
  }

  const handleReject = async (id) => {
    await adminService.reject(id)
    load()
  }

  if (loading) return <div className="container"><p>Loading...</p></div>

  return (
    <div className="container">
      <h2>Admin Dashboard — Pending Listings</h2>
      {pending.length === 0 ? (
        <p>No listings awaiting approval. 🎉</p>
      ) : (
        <table className="listings-table">
          <thead>
            <tr><th>Car</th><th>Seller</th><th>Price</th><th>Location</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {pending.map((car) => (
              <tr key={car.id}>
                <td>{car.brand} {car.model} ({car.year})</td>
                <td>{car.sellerName}</td>
                <td>₹{Number(car.price).toLocaleString('en-IN')}</td>
                <td>{car.location}</td>
                <td>
                  <button className="btn-link" onClick={() => handleApprove(car.id)}>Approve</button>
                  <button className="btn-link danger" onClick={() => handleReject(car.id)}>Reject</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
