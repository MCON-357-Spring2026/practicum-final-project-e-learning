<template>
  <div class="course-quizzes-page">
    <h1>Quizzes</h1>

    <p v-if="loading">Loading quizzes...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <p v-else-if="!quizzes.length">No quizzes for this course yet.</p>

    <div v-else class="quiz-list">
      <router-link
        v-for="q in quizzes"
        :key="q.id"
        :to="`/courses/${id}/quiz/${q.id}`"
        class="quiz-link"
      >
        <QuizPreviewCard :quiz="q" :grade="getGrade(q.id)" />
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { quizApi } from '@/api/quizApi'
import type { QuizPreview } from '@/api/quizApi'
import axiosClient from '@/api/axiosClient'
import { useAuthStore } from '@/store/auth'
import QuizPreviewCard from '@/components/QuizPreviewCard.vue'

const props = defineProps<{ id: string }>()

const authStore = useAuthStore()
const quizzes = ref<QuizPreview[]>([])
const completedQuizzes = ref<Record<string, { score: number }>>({})
const loading = ref(true)
const error = ref('')

function getGrade(quizId: string) {
  return completedQuizzes.value[quizId] ?? null
}

onMounted(async () => {
  try {
    const { data } = await quizApi.getPreviewsByCourseId(props.id)
    quizzes.value = data

    if (authStore.user) {
      try {
        const res = await axiosClient.get<any[]>(`/enrollments/student/${authStore.user.id}`)
        const enrollment = res.data.find((e: any) => e.courseId === props.id)
        if (enrollment?.completedQuizzes) {
          completedQuizzes.value = enrollment.completedQuizzes
        }
      } catch {
        // not enrolled or failed to fetch – silently ignore
      }
    }
  } catch {
    error.value = 'Failed to load quizzes'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.course-quizzes-page {
  max-width: 700px;
  margin: 2rem auto;
}

.quiz-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.quiz-link {
  text-decoration: none;
  color: inherit;
}

.error {
  color: #e94560;
}
</style>
