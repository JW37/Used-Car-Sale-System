import React, { createContext, useContext, useState } from 'react'
import { authService } from '../services/authService'


const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('automart_user')
    return stored ? JSON.parse(stored) : null
  })

  const login = async (email, password) => {
    const data = await authService.login({ email, password })
    persistSession(data)
    return data
  }

  const register = async (payload) => {
    const data = await authService.register(payload)
    persistSession(data)
    return data
  }

  const persistSession = (data) => {
    const profile = { userId: data.userId, name: data.name, email: data.email, role: data.role }
    localStorage.setItem('automart_token', data.token)
    localStorage.setItem('automart_user', JSON.stringify(profile))
    setUser(profile)
  }

  const logout = () => {
    localStorage.removeItem('automart_token')
    localStorage.removeItem('automart_user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
