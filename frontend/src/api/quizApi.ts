import axiosClient from './axiosClient'

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

  submit(id: string, answers: any) {
    return axiosClient.post(`/quizzes/${id}/submit`, answers)
  },

  delete(id: string) {
    return axiosClient.delete(`/quizzes/${id}`)
  }
}
