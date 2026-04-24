<template>
  <div class="quiz-view">
    <p v-if="loading">Loading quiz...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <template v-else-if="quiz">
      <router-link :to="`/courses/${courseId}`" class="back-link">← Back to course</router-link>
      <h1>{{ quiz.title }}</h1>

      <template v-if="!enrolled">
        <div class="locked-card">
          <p class="locked-info">{{ quiz.questions.length }} question{{ quiz.questions.length !== 1 ? 's' : '' }}</p>
          <p class="locked-message">🔒 Quiz content is locked. Enroll in the course to take this quiz.</p>
        </div>
      </template>

      <template v-else-if="!result">
        <QuizQuestion
          v-for="(q, i) in quiz.questions"
          :key="i"
          :question="q"
          :questionNumber="i + 1"
          @answer="setAnswer(i, $event)"
        />
        <button class="submit-btn" :disabled="submitting" @click="handleSubmit">
          {{ submitting ? 'Submitting...' : 'Submit Quiz' }}
        </button>
      </template>

      <div v-else class="result-card">
        <h2>Results</h2>
        <p class="score">Score: {{ result.score }} / {{ result.total }}</p>
        <p class="feedback">{{ result.feedback }}</p>

        <div class="review">
          <div v-for="(q, i) in quiz.questions" :key="i" class="review-question">
            <h3>{{ i + 1 }}. {{ q.questionText }}</h3>
            <p v-if="result.responses[i] === -1" class="no-answer">No answer provided</p>
            <div class="review-options">
              <div
                v-for="(option, j) in q.options"
                :key="j"
                class="review-option"
                :class="{
                  correct: j === result.correctAnswers[i],
                  wrong: j === result.responses[i] && j !== result.correctAnswers[i]
                }"
              >
                {{ option }}
              </div>
            </div>
          </div>
        </div>

        <div class="result-actions">
          <button class="retry-btn" @click="retry">Retry Quiz</button>
          <router-link :to="`/courses/${courseId}`" class="back-link">Return to course</router-link>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { quizApi, type Quiz } from '@/api/quizApi'
import { useAuthStore } from '@/store/auth'
import axiosClient from '@/api/axiosClient'
import QuizQuestion from '@/components/QuizQuestion.vue'

const props = defineProps<{
  courseId: string
  quizId: string
}>()

interface Enrollment {
  id: string
  courseId: string
}

interface GradeResult {
  score: number
  total: number
  feedback: string
  responses: number[]
  correctAnswers: number[]
}

const authStore = useAuthStore()
const quiz = ref<Quiz | null>(null)
const answers = ref<number[]>([])
const enrolled = ref(false)
const loading = ref(false)
const submitting = ref(false)
const error = ref('')
const result = ref<GradeResult | null>(null)

function setAnswer(index: number, value: number) {
  answers.value[index] = value
}

function retry() {
  result.value = null
  answers.value = new Array(quiz.value?.questions.length ?? 0)
  error.value = ''
}

async function handleSubmit() {
  if (!authStore.user) {
    error.value = 'You must be logged in to submit a quiz.'
    return
  }

  submitting.value = true
  error.value = ''
  try {
    // Fetch enrollments for the current user and find the one matching this course
    const res = await axiosClient.get<Enrollment[]>(`/enrollments/student/${authStore.user.id}`)
    const enrollment = res.data.find(e => e.courseId === props.courseId)
    if (!enrollment) {
      error.value = 'You are not enrolled in this course.'
      submitting.value = false
      return
    }

    // Fill unanswered questions with -1
    const totalQuestions = quiz.value?.questions.length ?? 0
    const payload = Array.from({ length: totalQuestions }, (_, i) =>
      answers.value[i] !== undefined ? answers.value[i] : -1
    )

    const gradeRes = await quizApi.submit(props.quizId, enrollment.id, payload)
    result.value = gradeRes.data
  } catch (e: any) {
    error.value = e.response?.data?.error || e.response?.data?.message || 'Failed to submit quiz.'
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const res = await quizApi.getById(props.quizId)
    quiz.value = res.data
    answers.value = new Array(res.data.questions.length)

    if (authStore.user) {
      const enrollRes = await axiosClient.get<Enrollment[]>(`/enrollments/student/${authStore.user.id}`)
      enrolled.value = enrollRes.data.some(e => e.courseId === props.courseId)
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

.submit-btn {
  margin-top: 1rem;
  padding: 0.6rem 1.5rem;
  background-color: #e94560;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.result-card {
  margin-top: 1.5rem;
  padding: 1.5rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  text-align: center;
}

.score {
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0.75rem 0;
}

.feedback {
  color: #555;
}

.error {
  color: #e94560;
}

.review {
  margin-top: 1.5rem;
  text-align: left;
}

.review-question {
  margin-bottom: 1.25rem;
}

.review-question h3 {
  margin-bottom: 0.5rem;
}

.no-answer {
  color: #e94560;
  font-weight: bold;
  font-size: 0.9rem;
  margin: 0 0 0.25rem;
}

.review-options {
  display: flex;
  flex-direction: column;
  gap: 0.35rem;
}

.review-option {
  padding: 0.5rem 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.review-option.wrong {
  border-color: #e94560;
  background-color: #fef0f2;
}

.review-option.correct {
  border-color: #2ecc71;
  background-color: #eafaf1;
}

.result-actions {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  margin-top: 1.5rem;
  justify-content: center;
}

.retry-btn {
  padding: 0.6rem 1.5rem;
  background-color: #e94560;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
}

.retry-btn:hover {
  opacity: 0.9;
}

.locked-card {
  margin-top: 1rem;
  padding: 1.5rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  text-align: center;
}

.locked-info {
  color: #555;
  margin: 0 0 0.75rem;
}

.locked-message {
  font-weight: bold;
  color: #e94560;
  margin: 0;
}
</style>
