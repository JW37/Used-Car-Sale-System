import React from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/')
  }

  return (
    <nav className="navbar">
      <Link to="/" className="brand">🚗 AutoMart</Link>
      <div className="nav-links">
        <Link to="/cars">Buy Cars</Link>
        {user && (user.role === 'SELLER' || user.role === 'ADMIN') && (
          <>
            <Link to="/sell">Sell Car</Link>
            <Link to="/my-listings">My Listings</Link>
          </>
        )}
        {user && <Link to="/favorites">Favorites</Link>}
        {user && user.role === 'ADMIN' && <Link to="/admin">Admin</Link>}

        {user ? (
          <>
            <span className="nav-user">Hi, {user.name}</span>
            <button className="btn-link" onClick={handleLogout}>Logout</button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  )
}
