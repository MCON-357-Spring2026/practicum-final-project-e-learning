import axiosClient from './axiosClient'

export interface Lesson {
  id: string
  title: string
  description: string
  minutes: number
  text: string
  media: string[]
  resources: string[]
}

export interface LessonPreview {
  id: string
  title: string
  description: string
  minutes: number
}

export const lessonApi = {
  getAll() {
    return axiosClient.get<Lesson[]>('/lessons/')
  },

  getById(id: string) {
    return axiosClient.get<Lesson>(`/lessons/${id}`)
  },

  create(lesson: Omit<Lesson, 'id'>) {
    return axiosClient.post<Lesson>('/lessons/', lesson)
  },

  update(id: string, lesson: Partial<Lesson>) {
    return axiosClient.patch<Lesson>(`/lessons/${id}`, lesson)
  },

  replace(id: string, lesson: Omit<Lesson, 'id'>) {
    return axiosClient.put<Lesson>(`/lessons/${id}`, lesson)
  },

  delete(id: string) {
    return axiosClient.delete(`/lessons/${id}`)
  },

  getPreviewsByCourseId(courseId: string) {
    return axiosClient.get<LessonPreview[]>(`/lessons/course/${courseId}/previews`)
  }
}
