<template>
  <div class="student-dashboard">
    <h1>My Dashboard</h1>

    <div v-if="authStore.user?.role === 'PENDING'" class="pending-info">
      Once you are approved to be a teacher, you will be given access to the teacher dashboard and pages.
    </div>

    <p v-if="loading">Loading your enrollments...</p>
    <p v-else-if="error" class="error">{{ error }}</p>

    <template v-else>
      <p v-if="enrollments.length === 0" class="empty">You are not enrolled in any courses yet.
        <router-link to="/courses">Browse courses</router-link>
      </p>

      <div v-else class="enrollment-list">
        <div v-for="enrollment in enrollments" :key="enrollment.id" class="enrollment-card">
          <div class="enrollment-header">
            <h3>
              <router-link :to="`/courses/${enrollment.courseId}`">{{ enrollment.courseTitle }}</router-link>
            </h3>
            <span :class="['status-badge', statusClass(enrollment.progress)]">{{ enrollment.progress }}</span>
          </div>

          <ProgressBar :percentage="progressPercent(enrollment)" />

          <div class="enrollment-stats">
            <span>Lessons: {{ enrollment.completedLessons.length }}</span>
            <span>Quizzes: {{ Object.keys(enrollment.completedQuizzes).length }}</span>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'
import axiosClient from '@/api/axiosClient'
import ProgressBar from '@/components/ProgressBar.vue'
import type { Course } from '@/api/courseApi'

interface Grade {
  score: number
  total: number
}

interface Enrollment {
  id: string
  studentId: string
  courseId: string
  progress: string
  completedLessons: string[]
  completedQuizzes: Record<string, Grade>
  courseTitle?: string
  totalLessons?: number
  totalQuizzes?: number
}

const authStore = useAuthStore()
const enrollments = ref<Enrollment[]>([])
const loading = ref(true)
const error = ref('')

function statusClass(progress: string) {
  if (progress === 'Completed') return 'completed'
  if (progress === 'In Progress') return 'in-progress'
  return 'not-started'
}

function progressPercent(enrollment: Enrollment) {
  const total = (enrollment.totalLessons ?? 0) + (enrollment.totalQuizzes ?? 0)
  if (total === 0) return 0
  const done = enrollment.completedLessons.length + Object.keys(enrollment.completedQuizzes).length
  return Math.round((done / total) * 100)
}

onMounted(async () => {
  const user = authStore.user
  if (!user) {
    error.value = 'You must be logged in to view your dashboard.'
    loading.value = false
    return
  }

  try {
    const enrollRes = await axiosClient.get<Enrollment[]>(`/enrollments/student/${user.id}`)
    const items = enrollRes.data

    // Fetch course details for titles and totals
    const courseIds = [...new Set(items.map(e => e.courseId))]
    const courseMap = new Map<string, Course>()

    await Promise.all(
      courseIds.map(async (id) => {
        try {
          const res = await axiosClient.get<Course>(`/courses/${id}`)
          courseMap.set(id, res.data)
        } catch {
          // course may have been deleted
        }
      })
    )

    enrollments.value = items.map(e => {
      const course = courseMap.get(e.courseId)
      return {
        ...e,
        courseTitle: course?.title ?? 'Unknown Course',
        totalLessons: course?.lessonIDs?.length ?? 0,
        totalQuizzes: course?.quizIDs?.length ?? 0
      }
    })
  } catch (e: any) {
    error.value = e.response?.data?.error || 'Failed to load enrollments.'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.student-dashboard {
  max-width: 750px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.student-dashboard h1 {
  margin-bottom: 1.5rem;
}

.empty {
  color: #666;
}

.enrollment-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.enrollment-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
}

.enrollment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.enrollment-header h3 {
  margin: 0;
  font-size: 1.1rem;
}

.status-badge {
  font-size: 0.75rem;
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.status-badge.not-started {
  background-color: #eee;
  color: #666;
}

.status-badge.in-progress {
  background-color: #fff3cd;
  color: #856404;
}

.status-badge.completed {
  background-color: #d4edda;
  color: #155724;
}

.enrollment-stats {
  display: flex;
  gap: 1.5rem;
  margin-top: 0.75rem;
  font-size: 0.85rem;
  color: #666;
}

.pending-info {
  background-color: #fff3cd;
  border: 1px solid #ffc107;
  color: #856404;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  font-size: 0.95rem;
}

.error {
  color: #e94560;
}
</style>