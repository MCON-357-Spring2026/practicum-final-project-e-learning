import axiosClient from './axiosClient'

export interface UserInfo {
  id: string
  firstName: string
  lastName: string
}

export const userApi = {
  getById(id: string) {
    return axiosClient.get<UserInfo>(`/users/${id}`)
  }
}
