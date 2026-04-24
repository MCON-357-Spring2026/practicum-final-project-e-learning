import { defineStore } from 'pinia'
import { ref } from 'vue'
import { courseApi, type Course } from '@/api/courseApi'

export const useCourseStore = defineStore('course', () => {
  const courses = ref<Course[]>([])
  const currentCourse = ref<Course | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  async function fetchAll() {
    loading.value = true
    error.value = null
    try {
      const response = await courseApi.getAll()
      courses.value = response.data
    } catch (e: any) {
      error.value = e.response?.data?.error || 'Failed to load courses'
    } finally {
      loading.value = false
    }
  }

  async function fetchById(id: string) {
    loading.value = true
    error.value = null
    try {
      const response = await courseApi.getById(id)
      currentCourse.value = response.data
    } catch (e: any) {
      error.value = e.response?.data?.error || 'Course not found'
    } finally {
      loading.value = false
    }
  }

  async function create(course: Omit<Course, 'id'>) {
    loading.value = true
    error.value = null
    try {
      const response = await courseApi.create(course)
      courses.value.push(response.data)
      return response.data
    } catch (e: any) {
      error.value = e.response?.data?.error || 'Failed to create course'
      throw e
    } finally {
      loading.value = false
    }
  }

  return { courses, currentCourse, loading, error, fetchAll, fetchById, create }
})
