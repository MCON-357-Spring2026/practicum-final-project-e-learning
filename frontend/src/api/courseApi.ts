import axiosClient from './axiosClient'

export interface Course {
  id: string
  title: string
  description: string
  instructorId: string
  department: string
  credits: number
  courseNum: number
  lessonIDs: string[]
  quizIDs: string[]
  image: string
}

export interface CreateCoursePayload {
  title: string
  description: string
  department: string
  courseNum: number
  credits: number
  image: string
}

export const courseApi = {
  getAll() {
    return axiosClient.get<Course[]>('/courses/')
  },

  getById(id: string) {
    return axiosClient.get<Course>(`/courses/${id}`)
  },

  getByInstructor(instructorId: string) {
    return axiosClient.get<Course[]>(`/courses/instructor/${instructorId}`)
  },

  create(course: CreateCoursePayload) {
    return axiosClient.post<Course>('/courses/', course)
  },

  update(id: string, course: Partial<Course>) {
    return axiosClient.patch<Course>(`/courses/${id}`, course)
  },

  replace(id: string, course: Omit<Course, 'id'>) {
    return axiosClient.put<Course>(`/courses/${id}`, course)
  },

  delete(id: string) {
    return axiosClient.delete(`/courses/${id}`)
  }
}
