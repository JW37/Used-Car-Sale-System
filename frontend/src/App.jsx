import React from 'react'
import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import ProtectedRoute from './components/ProtectedRoute'

import Home from './pages/Home'
import Login from './pages/Login'
import Register from './pages/Register'
import CarList from './pages/CarList'
import CarDetails from './pages/CarDetails'
import SellCar from './pages/SellCar'
import MyListings from './pages/MyListings'
import Favorites from './pages/Favorites'
import AdminDashboard from './pages/AdminDashboard'

export default function App() {
  return (
    <>
      <Navbar />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/cars" element={<CarList />} />
          <Route path="/cars/:id" element={<CarDetails />} />

          <Route path="/sell" element={
            <ProtectedRoute roles={['SELLER', 'ADMIN']}><SellCar /></ProtectedRoute>
          } />
          <Route path="/my-listings" element={
            <ProtectedRoute roles={['SELLER', 'ADMIN']}><MyListings /></ProtectedRoute>
          } />
          <Route path="/favorites" element={
            <ProtectedRoute><Favorites /></ProtectedRoute>
          } />
          <Route path="/admin" element={
            <ProtectedRoute roles={['ADMIN']}><AdminDashboard /></ProtectedRoute>
          } />
        </Routes>
      </main>
      <Footer />
    </>
  )
}
