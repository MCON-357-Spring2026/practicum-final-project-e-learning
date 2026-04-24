import axiosClient from './axiosClient'

export interface LimitedTeacher {
  id: string
  firstName: string
  lastName: string
  department: string | null
  courses: Record<string, string>
}

export interface FullTeacher extends LimitedTeacher {
  dateOfBirth: string | null
  gender: string | null
  address: { street: string; city: string; state: string; zipCode: string } | null
  email: string | null
  role: string
  enrollments: Record<string, string>
}

export const teacherApi = {
  getAll() {
    return axiosClient.get<LimitedTeacher[]>('/teachers/')
  },

  getById(id: string) {
    return axiosClient.get<LimitedTeacher | FullTeacher>(`/teachers/${id}`)
  },

  getByDepartment(department: string) {
    return axiosClient.get<LimitedTeacher[]>(`/teachers/department/${department}`)
  },

  updateDepartment(id: string, department: string) {
    return axiosClient.patch(`/teachers/${id}/department`, department)
  },

  promoteToTeacher(id: string) {
    return axiosClient.patch(`/teachers/${id}/promote/teacher`)
  },

  promoteToAdmin(id: string) {
    return axiosClient.patch(`/teachers/${id}/promote/admin`)
  }
}
