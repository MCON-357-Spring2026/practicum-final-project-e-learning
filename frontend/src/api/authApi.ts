import axiosClient from './axiosClient'

export const authApi = {
  login(email: string, password: string) {
    return axiosClient.post('/auth/login', { email, password })
  },

  register(email: string, password: string, name: string) {
    return axiosClient.post('/auth/register', { email, password, name })
  }
}
