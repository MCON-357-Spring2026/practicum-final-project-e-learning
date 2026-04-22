<template>
  <div class="course-preview">
    <p v-if="loading">Loading course...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <template v-else-if="course">
      <div class="course-header">
        <h1>{{ course.title }}</h1>
        <img v-if="course.image" :src="course.image" :alt="course.title" class="course-image" />
      </div>

      <div class="info-band">
        <span v-if="instructorName">{{ instructorName }}</span>
        <span>{{ course.department }} {{ course.courseNum }}</span>
        <span>{{ course.credits }} credits</span>
      </div>

      <p class="description">{{ course.description }}</p>

      <div class="preview-summary">
        <p>{{ course.lessonIDs?.length ?? 0 }} lessons &bull; {{ course.quizIDs?.length ?? 0 }} quizzes</p>
      </div>

      <div class="enroll-section">
        <template v-if="authStore.isAuthenticated">
          <button v-if="enrolling" class="btn" disabled>Enrolling...</button>
          <button v-else class="btn" @click="enroll">Enroll in this course</button>
          <p v-if="enrollError" class="error">{{ enrollError }}</p>
        </template>
        <p v-else class="login-prompt">
          <router-link :to="{ path: '/login', query: { redirect: $route.fullPath } }">Log in</router-link>
          to enroll in this course.
        </p>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { courseApi, type Course } from '@/api/courseApi'
import axiosClient from '@/api/axiosClient'

const props = defineProps<{ id: string }>()

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const course = ref<Course | null>(null)
const instructorName = ref('')
const loading = ref(true)
const error = ref('')
const enrolling = ref(false)
const enrollError = ref('')

async function enroll() {
  if (!authStore.user) return
  enrolling.value = true
  enrollError.value = ''
  try {
    await axiosClient.post('/enrollments/', {
      studentId: authStore.user.id,
      courseId: props.id
    })
    router.push(`/courses/${props.id}`)
  } catch (e: any) {
    enrollError.value = e.response?.data?.error || 'Failed to enroll.'
  } finally {
    enrolling.value = false
  }
}

onMounted(async () => {
  try {
    const res = await courseApi.getById(props.id)
    course.value = res.data

    if (res.data.instructorId) {
      try {
        const tRes = await axiosClient.get(`/teachers/${res.data.instructorId}/preview`)
        instructorName.value = `${tRes.data.firstName} ${tRes.data.lastName}`
      } catch { /* instructor may be deleted */ }
    }
  } catch {
    error.value = 'Course not found.'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.course-preview {
  max-width: 900px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.course-header {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.course-header h1 {
  flex: 1;
  margin: 0;
}

.course-image {
  width: 200px;
  height: 140px;
  object-fit: cover;
  border-radius: 8px;
}

.info-band {
  display: flex;
  gap: 1.5rem;
  padding: 0.75rem 0;
  margin-top: 0.75rem;
  border-top: 1px solid #ddd;
  border-bottom: 1px solid #ddd;
  color: #555;
  font-size: 0.95rem;
}

.description {
  margin: 1.25rem 0;
  line-height: 1.7;
}

.preview-summary {
  color: #666;
  font-size: 0.95rem;
  margin-bottom: 1.5rem;
}

.enroll-section {
  margin-top: 1rem;
}

.btn {
  background-color: #e94560;
  color: #fff;
  border: none;
  padding: 0.6rem 1.5rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
}

.btn:hover {
  background-color: #c73a52;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-prompt a {
  color: #e94560;
  font-weight: 600;
}

.error {
  color: #e94560;
}
</style>
