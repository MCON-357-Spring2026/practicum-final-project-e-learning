import axiosClient from './axiosClient'

export interface QuizQuestion {
  questionText: string
  options: string[]
}

export interface Quiz {
  id: string
  courseId: string
  title: string
  questions: QuizQuestion[]
}

export interface QuizPreview {
  id: string
  title: string
  numberOfQuestions: number
}

export const quizApi = {
  getAll() {
    return axiosClient.get('/quizzes/')
  },

  getById(id: string) {
    return axiosClient.get(`/quizzes/${id}`)
  },

  getForEdit(id: string) {
    return axiosClient.get(`/quizzes/${id}/edit`)
  },

  create(quiz: any) {
    return axiosClient.post('/quizzes/', quiz)
  },

  update(id: string, quiz: any) {
    return axiosClient.patch(`/quizzes/${id}`, quiz)
  },

  submit(quizId: string, enrollmentId: string, answers: number[]) {
    return axiosClient.patch(`/enrollments/quiz/${quizId}/submit`, { enrollmentId, answers })
  },

  delete(id: string) {
    return axiosClient.delete(`/quizzes/${id}`)
  },

  getPreviewsByCourseId(courseId: string) {
    return axiosClient.get<QuizPreview[]>(`/quizzes/course/${courseId}/preview`)
  }
}
