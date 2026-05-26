<template>
  <div class="quiz-view">
    <p v-if="loading">Loading quiz...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <template v-else-if="quiz">
      <router-link :to="`/courses/${courseId}`" class="back-link">← Back to course</router-link>
      <h1>{{ quiz.title }}</h1>

      <!-- Instructor answer key -->
      <div v-if="isInstructor" class="answer-key">
        <h2>Answer Key</h2>
        <div v-for="(q, i) in fullQuestions" :key="i" class="answer-key-question">
          <h3>{{ i + 1 }}. {{ q.questionText }}</h3>
          <div class="answer-key-options">
            <div
              v-for="(option, j) in q.options"
              :key="j"
              class="answer-key-option"
              :class="{ correct: j === q.correctOptionIndex }"
            >
              {{ option }}
            </div>
          </div>
        </div>
      </div>

      <!-- Student views -->
      <template v-else>
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
    </template>
    <TutorButton />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { quizApi, type Quiz } from '@/api/quizApi'
import { courseApi } from '@/api/courseApi'
import { useAuthStore } from '@/store/auth'
import axiosClient from '@/api/axiosClient'
import QuizTaker from './QuizTaker.vue'
import type { GradeResult } from './QuizTaker.vue'
import QuizResult from './QuizResult.vue'
import TutorButton from '@/components/TutorButton.vue'

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
const isInstructor = ref(false)
const fullQuestions = ref<{ questionText: string; options: string[]; correctOptionIndex: number }[]>([])

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
    // Check if user is the course instructor
    const courseRes = await courseApi.getById(props.courseId).catch(() => null)
    if (courseRes && courseRes.data.instructorId === authStore.user.id) {
      isInstructor.value = true
      const editRes = await quizApi.getForEdit(props.quizId)
      const editData = editRes.data as any
      quiz.value = { id: editData.id, courseId: editData.courseId, title: editData.title, questions: editData.questions }
      fullQuestions.value = editData.questions
    } else {
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

.answer-key {
  margin-top: 1.5rem;
}

.answer-key h2 {
  margin-bottom: 1rem;
}

.answer-key-question {
  margin-bottom: 1.25rem;
}

.answer-key-question h3 {
  margin-bottom: 0.5rem;
}

.answer-key-options {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.answer-key-option {
  padding: 0.5rem 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.answer-key-option.correct {
  border-color: #22c55e;
  background-color: #f0fdf4;
  font-weight: bold;
}
</style>
