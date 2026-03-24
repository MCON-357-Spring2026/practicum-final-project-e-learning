import axiosClient from './axiosClient'

export interface Quiz {
  id: string
  courseId: string
  title: string
}

export const quizApi = {
  getAll() {
    return axiosClient.get('/quizzes/')
  },

  getById(id: string) {
    return axiosClient.get(`/quizzes/${id}`)
  },

  create(quiz: any) {
    return axiosClient.post('/quizzes/', quiz)
  },

  submit(quizId: string, enrollmentId: string, answers: number[]) {
    return axiosClient.patch(`/enrollments/quiz/${quizId}/submit`, { enrollmentId, answers })
  },

  delete(id: string) {
    return axiosClient.delete(`/quizzes/${id}`)
  }
}
