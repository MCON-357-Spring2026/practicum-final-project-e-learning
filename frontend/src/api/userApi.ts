import axiosClient from './axiosClient'

export interface HomeAddress {
  street: string
  city: string
  state: string
  zipCode: string
}

export interface UserInfo {
  id: string
  firstName: string
  lastName: string
  dateOfBirth: string | null
  gender: string | null
  address: HomeAddress | null
  username: string
  email: string | null
  role: string
  enrollmentIds: string[]
}

export interface UserProfile {
  id: string
  firstName: string
  lastName: string
  dateOfBirth: string
  gender: string | null
  address: HomeAddress | null
  username: string
  password: string
  email: string
  role: string
}

export interface TeacherInfo extends UserInfo {
  department: string | null
  courseIds: string[]
}

export const userApi = {
  getById(id: string) {
    return axiosClient.get<UserInfo>(`/users/${id}`)
  },

  getProfile(id: string) {
    return axiosClient.get<UserProfile>(`/users/${id}/profile`)
  }
}
