import React, { useEffect, useState } from 'react'
import { carService } from '../services/carService'
import CarCard from '../components/CarCard'
import SearchFilter from '../components/SearchFilter'

export default function CarList() {
  const [cars, setCars] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [activeFilters, setActiveFilters] = useState(null)
  const [loading, setLoading] = useState(true)

  const loadCars = (filters, pageNum) => {
    setLoading(true)
    const request = filters && Object.keys(filters).length > 0
      ? carService.search(filters, pageNum)
      : carService.getApprovedCars(pageNum)

    request
      .then((data) => {
        setCars(data.content)
        setTotalPages(data.totalPages)
      })
      .catch(() => setCars([]))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    loadCars(activeFilters, page)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page])

  const handleSearch = (filters) => {
    setActiveFilters(filters)
    setPage(0)
    loadCars(filters, 0)
  }

  return (
    <div className="container">
      <SearchFilter onSearch={handleSearch} />

      {loading ? (
        <p>Loading...</p>
      ) : cars.length === 0 ? (
        <p>No cars match your search.</p>
      ) : (
        <>
          <div className="car-grid">
            {cars.map((car) => <CarCard key={car.id} car={car} />)}
          </div>
          <div className="pagination">
            <button disabled={page === 0} onClick={() => setPage((p) => p - 1)}>Previous</button>
            <span>Page {page + 1} of {totalPages || 1}</span>
            <button disabled={page + 1 >= totalPages} onClick={() => setPage((p) => p + 1)}>Next</button>
          </div>
        </>
      )}
    </div>
  )
}
