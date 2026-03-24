import axiosClient from './axiosClient'

export const authApi = {
  login(username: string, password: string) {
    return axiosClient.post('/auth/login', { username, password })
  },

  register(username: string, password: string, name: string) {
    return axiosClient.post('/auth/register', { username, password, name })
  }
}
