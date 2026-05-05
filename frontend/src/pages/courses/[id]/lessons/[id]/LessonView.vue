<template>
  <div class="lesson-view">
    <p v-if="loading">Loading lesson...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <template v-else-if="lesson">
      <router-link :to="`/courses/${courseId}`" class="back-link">← Back to course</router-link>
      <LessonPlayer
        :lesson="lesson"
        :showComplete="!!authStore.user"
        :completed="lessonCompleted"
        :completing="completing"
        :completeError="completeError"
        @complete="markComplete"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { lessonApi, type Lesson } from '@/api/lessonApi'
import { useAuthStore } from '@/store/auth'
import axiosClient from '@/api/axiosClient'
import LessonPlayer from '@/components/LessonPlayer.vue'

const props = defineProps<{
  courseId: string
  lessonId: string
}>()

const authStore = useAuthStore()
const lesson = ref<Lesson | null>(null)
const loading = ref(false)
const error = ref('')
const lessonCompleted = ref(false)
const completing = ref(false)
const completeError = ref('')

async function markComplete() {
  if (!authStore.user) return
  completing.value = true
  completeError.value = ''
  try {
    const res = await axiosClient.patch(
      `/enrollments/course/${props.courseId}/lesson/${props.lessonId}/complete`,
      {}
    )
    lessonCompleted.value = true
  } catch (e: any) {
    completeError.value = e.response?.data?.error || 'Failed to mark lesson as complete'
  } finally {
    completing.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const response = await lessonApi.getById(props.lessonId)
    lesson.value = response.data
  } catch (e: any) {
    error.value = 'Lesson not found'
  } finally {
    loading.value = false
  }

  // Check if already completed
  if (authStore.user) {
    try {
      const enrollRes = await axiosClient.get(
        `/enrollments/student/${authStore.user.id}/course/${props.courseId}`
      )
      const enrollment = enrollRes.data
      if (enrollment.completedLessons?.includes(props.lessonId)) {
        lessonCompleted.value = true
      }
    } catch { /* not enrolled */ }
  }
})
</script>

<style scoped>
.lesson-view {
  max-width: 800px;
  margin: 2rem auto;
}

.back-link {
  display: inline-block;
  margin-bottom: 1rem;
  color: #e94560;
  text-decoration: none;
}

.back-link:hover {
  text-decoration: underline;
}

.error {
  color: #e94560;
}
</style>
