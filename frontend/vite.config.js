import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Dev-server proxy: the React app calls "/api/..." and Vite forwards it
// to the Spring Boot server on :8080, so we avoid CORS headaches while
// developing (CORS is still configured server-side for direct calls too).
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': 'http://localhost:8080',
      '/uploads': 'http://localhost:8080'
    }
  }
})
