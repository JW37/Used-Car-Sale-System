import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { carService } from '../services/carService'
import { favoriteService } from '../services/favoriteService'
import { enquiryService } from '../services/enquiryService'
import { useAuth } from '../context/AuthContext'

export default function CarDetails() {
  const { id } = useParams()
  const { user } = useAuth()
  const navigate = useNavigate()
  const [car, setCar] = useState(null)
  const [message, setMessage] = useState('')
  const [status, setStatus] = useState('')
  const [activeImage, setActiveImage] = useState(0)

  useEffect(() => {
    carService.getById(id).then((data) => {
      setCar(data)
      setActiveImage(0)
    }).catch(() => setCar(null))
  }, [id])

  const handleFavorite = async () => {
    if (!user) return navigate('/login')
    try {
      await favoriteService.add(car.id)
      setStatus('Saved to favorites!')
    } catch (err) {
      setStatus(err.response?.data?.message || 'Could not save favorite')
    }
  }

  const handleEnquiry = async (e) => {
    e.preventDefault()
    if (!user) return navigate('/login')
    try {
      await enquiryService.create({ carId: car.id, message })
      setStatus('Enquiry sent to the seller!')
      setMessage('')
    } catch (err) {
      setStatus(err.response?.data?.message || 'Could not send enquiry')
    }
  }

  if (!car) return <div className="container"><p>Loading...</p></div>

  const images = car.imageUrls || []
  const mainImage = images.length > 0 ? images[activeImage] : null

  return (
    <div className="container car-details">
      <div className="car-details-grid">
        <div>
          <div className="car-details-image">
            {mainImage ? <img src={mainImage} alt={`${car.brand} ${car.model}`} /> : <div className="image-placeholder">No Image</div>}
          </div>
          {images.length > 1 && (
            <div className="thumbnail-strip">
              {images.map((url, i) => (
                <img
                  key={i}
                  src={url}
                  alt={`${car.brand} ${car.model} thumbnail ${i + 1}`}
                  className={i === activeImage ? 'thumbnail active' : 'thumbnail'}
                  onClick={() => setActiveImage(i)}
                />
              ))}
            </div>
          )}
        </div>
        <div className="car-details-info">
          <h2>{car.brand} {car.model}</h2>
          <p className="price">₹{Number(car.price).toLocaleString('en-IN')}</p>
          <ul className="spec-list">
            <li>{car.year} Model</li>
            <li>{car.fuelType}</li>
            <li>{car.transmission}</li>
            <li>{car.kilometers?.toLocaleString('en-IN')} km</li>
            <li>{car.location}</li>
            <li>Seller: {car.sellerName}</li>
          </ul>

          <h3>Description</h3>
          <p>{car.description || 'No description provided.'}</p>

          {status && <div className="alert-info">{status}</div>}

          <div className="action-buttons">
            <button className="btn btn-outline" onClick={handleFavorite}>❤️ Save Car</button>
          </div>

          <form className="enquiry-form" onSubmit={handleEnquiry}>
            <h3>Contact Seller</h3>
            <textarea
              placeholder="Ask a question about this car..."
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              required
              rows={3}
            />
            <button type="submit" className="btn btn-primary">Send Enquiry</button>
          </form>
        </div>
      </div>
    </div>
  )
}
