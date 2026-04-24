import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/authApi'
import type { RegisterPayload } from '@/api/authApi'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<{ id: string; username: string; role: string } | null>(null)

  // Restore user from JWT payload on init
  if (token.value) {
    try {
      const payload = JSON.parse(atob(token.value.split('.')[1]))
      const expiry = payload.exp * 1000 // exp is in seconds
      if (Date.now() >= expiry) {
        // Token expired — clear auth
        token.value = null
        localStorage.removeItem('token')
      } else {
        user.value = { id: payload.userId, username: payload.sub, role: payload.role }
      }
    } catch {
      token.value = null
      localStorage.removeItem('token')
    }
  }

  const isAuthenticated = computed(() => !!token.value)

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function clearAuth() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  async function login(username: string, password: string) {
    const response = await authApi.login(username, password)
    setToken(response.data.token)
    user.value = response.data.user
  }

  async function register(payload: RegisterPayload) {
    const response = await authApi.register(payload)
    setToken(response.data.token)
    user.value = response.data.user
  }

  function logout() {
    clearAuth()
  }

  return { token, user, isAuthenticated, login, register, logout }
})
