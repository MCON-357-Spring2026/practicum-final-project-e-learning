<template>
  <div class="quiz-view">
    <p v-if="loading">Loading quiz...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <template v-else-if="quiz">
      <router-link :to="`/courses/${courseId}`" class="back-link">← Back to course</router-link>
      <h1>{{ quiz.title }}</h1>

      <QuizResult
        v-if="result"
        :quiz="quiz"
        :courseId="courseId"
        :result="result"
        @retry="handleRetry"
      />
      <QuizTaker
        v-else
        :key="attemptKey"
        :quiz="quiz"
        :enrolled="enrolled"
        :enrollmentId="enrollmentId"
        @submitted="onSubmitted"
      />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { quizApi, type Quiz } from '@/api/quizApi'
import { useAuthStore } from '@/store/auth'
import axiosClient from '@/api/axiosClient'
import QuizTaker from './QuizTaker.vue'
import type { GradeResult } from './QuizTaker.vue'
import QuizResult from './QuizResult.vue'

const props = defineProps<{
  courseId: string
  quizId: string
}>()

interface EnrollmentProgress {
  enrollmentId: string
  userId: string
  courseId: string
  completedLessons: string[]
  completedQuizzes: Record<string, { quizId: string; responses: number[]; score: number; feedback: string }>
}

const authStore = useAuthStore()
const router = useRouter()
const route = useRoute()

const quiz = ref<Quiz | null>(null)
const enrolled = ref(false)
const enrollmentId = ref('')
const result = ref<GradeResult | null>(null)
const loading = ref(true)
const error = ref('')
const attemptKey = ref(0)

function onSubmitted(gradeResult: GradeResult) {
  result.value = gradeResult
}

function handleRetry() {
  result.value = null
  attemptKey.value++
}

onMounted(async () => {
  if (!authStore.user) {
    router.replace({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }

  try {
    const [quizRes, enrollRes] = await Promise.all([
      quizApi.getById(props.quizId),
      axiosClient.get<EnrollmentProgress>(`/enrollments/student/${authStore.user.id}/course/${props.courseId}`)
        .catch(() => null)
    ])

    quiz.value = quizRes.data

    if (enrollRes) {
      enrolled.value = true
      enrollmentId.value = enrollRes.data.enrollmentId

      const existingGrade = enrollRes.data.completedQuizzes?.[props.quizId]
      if (existingGrade) {
        result.value = {
          score: existingGrade.score,
          total: quizRes.data.questions.length,
          feedback: existingGrade.feedback,
          responses: existingGrade.responses
          // correctAnswers intentionally omitted — not stored in enrollment grade
        }
      }
    }
  } catch {
    error.value = 'Quiz not found'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.quiz-view {
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
