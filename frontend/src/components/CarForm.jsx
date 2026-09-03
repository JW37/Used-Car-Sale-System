import React, { useState } from 'react'

const FUEL_TYPES = ['PETROL', 'DIESEL', 'ELECTRIC', 'CNG', 'HYBRID']
const TRANSMISSIONS = ['MANUAL', 'AUTOMATIC']

const MAX_IMAGES = 6

export default function CarForm({ initialValues, onSubmit, submitLabel = 'List Car' }) {
  const [form, setForm] = useState(initialValues || {
    brand: '', model: '', year: '', price: '', fuelType: 'PETROL', transmission: 'MANUAL',
    kilometers: '', location: '', description: '',
  })
  const [files, setFiles] = useState([])
  const [previews, setPreviews] = useState([])
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleFilesChange = (e) => {
    const picked = Array.from(e.target.files || [])
    const combined = [...files, ...picked].slice(0, MAX_IMAGES)
    setFiles(combined)
    setPreviews(combined.map((f) => URL.createObjectURL(f)))
    e.target.value = '' // allow re-selecting the same file if removed and re-added
  }

  const removeFile = (index) => {
    const nextFiles = files.filter((_, i) => i !== index)
    setFiles(nextFiles)
    setPreviews(nextFiles.map((f) => URL.createObjectURL(f)))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSaving(true)
    try {
      await onSubmit({
        ...form,
        year: Number(form.year),
        price: Number(form.price),
        kilometers: Number(form.kilometers),
      }, files)
    } catch (err) {
      setError(err.response?.data?.message || 'Something went wrong')
    } finally {
      setSaving(false)
    }
  }

  return (
    <form className="car-form" onSubmit={handleSubmit}>
      {error && <div className="alert-error">{error}</div>}
      <div className="filter-row">
        <input name="brand" placeholder="Brand (e.g. Hyundai)" value={form.brand} onChange={handleChange} required />
        <input name="model" placeholder="Model (e.g. i20)" value={form.model} onChange={handleChange} required />
      </div>
      <div className="filter-row">
        <input name="year" type="number" placeholder="Year" value={form.year} onChange={handleChange} required />
        <input name="price" type="number" placeholder="Price (₹)" value={form.price} onChange={handleChange} required />
      </div>
      <div className="filter-row">
        <select name="fuelType" value={form.fuelType} onChange={handleChange}>
          {FUEL_TYPES.map((f) => <option key={f} value={f}>{f}</option>)}
        </select>
        <select name="transmission" value={form.transmission} onChange={handleChange}>
          {TRANSMISSIONS.map((t) => <option key={t} value={t}>{t}</option>)}
        </select>
      </div>
      <div className="filter-row">
        <input name="kilometers" type="number" placeholder="Kilometers driven" value={form.kilometers} onChange={handleChange} required />
        <input name="location" placeholder="Location (e.g. Chennai)" value={form.location} onChange={handleChange} required />
      </div>
      <textarea name="description" placeholder="Description" value={form.description} onChange={handleChange} rows={4} />

      <div className="photo-picker">
        <label className="photo-picker-label">
          Photos ({files.length}/{MAX_IMAGES})
          <input
            type="file"
            accept="image/*"
            multiple
            onChange={handleFilesChange}
            disabled={files.length >= MAX_IMAGES}
          />
        </label>
        {previews.length > 0 && (
          <div className="photo-preview-grid">
            {previews.map((src, i) => (
              <div className="photo-preview" key={i}>
                <img src={src} alt={`Preview ${i + 1}`} />
                <button type="button" className="photo-remove" onClick={() => removeFile(i)}>×</button>
              </div>
            ))}
          </div>
        )}
      </div>

      <button type="submit" className="btn btn-primary" disabled={saving}>
        {saving ? 'Saving...' : submitLabel}
      </button>
    </form>
  )
}
