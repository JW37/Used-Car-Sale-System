import React, { useState } from 'react'

const FUEL_TYPES = ['PETROL', 'DIESEL', 'ELECTRIC', 'CNG', 'HYBRID']

export default function SearchFilter({ onSearch }) {
  const [filters, setFilters] = useState({
    keyword: '', brand: '', minPrice: '', maxPrice: '', fuelType: '', year: '', location: '',
  })

  const handleChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    const cleaned = Object.fromEntries(
      Object.entries(filters).filter(([, v]) => v !== '')
    )
    onSearch(cleaned)
  }

  return (
    <form className="search-filter" onSubmit={handleSubmit}>
      <input name="keyword" placeholder="Search brand or model" value={filters.keyword} onChange={handleChange} />
      <div className="filter-row">
        <input name="brand" placeholder="Brand" value={filters.brand} onChange={handleChange} />
        <input name="location" placeholder="Location" value={filters.location} onChange={handleChange} />
      </div>
      <div className="filter-row">
        <input name="minPrice" type="number" placeholder="Min Price" value={filters.minPrice} onChange={handleChange} />
        <input name="maxPrice" type="number" placeholder="Max Price" value={filters.maxPrice} onChange={handleChange} />
      </div>
      <div className="filter-row">
        <select name="fuelType" value={filters.fuelType} onChange={handleChange}>
          <option value="">All Fuel Types</option>
          {FUEL_TYPES.map((f) => <option key={f} value={f}>{f}</option>)}
        </select>
        <input name="year" type="number" placeholder="Year" value={filters.year} onChange={handleChange} />
      </div>
      <button type="submit" className="btn btn-primary">🔍 Search Cars</button>
    </form>
  )
}
