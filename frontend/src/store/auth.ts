import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '../api/authApi'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<{ id: string; username: string; role: string } | null>(null)

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

  async function register(username: string, password: string, name: string) {
    const response = await authApi.register(username, password, name)
    setToken(response.data.token)
    user.value = response.data.user
  }

  function logout() {
    clearAuth()
  }

  return { token, user, isAuthenticated, login, register, logout }
})
