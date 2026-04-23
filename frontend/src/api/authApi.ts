import axiosClient from './axiosClient'
import type { HomeAddress } from './userApi'

export interface RegisterPayload {
  username: string
  password: string
  firstName: string
  lastName: string
  dateOfBirth: string | null
  gender: string | null
  address: HomeAddress | null
  email: string | null
  accountType: 'student' | 'teacher'
}

export const authApi = {
  login(username: string, password: string) {
    return axiosClient.post('/auth/login', { username, password })
  },

  register(payload: RegisterPayload) {
    return axiosClient.post('/auth/register', payload)
  }
}
