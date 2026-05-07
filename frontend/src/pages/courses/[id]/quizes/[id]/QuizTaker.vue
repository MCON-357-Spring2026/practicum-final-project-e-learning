<template>
  <template v-if="!enrolled">
    <div class="locked-card">
      <p class="locked-info">{{ quiz.questions.length }} question{{ quiz.questions.length !== 1 ? 's' : '' }}</p>
      <p class="locked-message">🔒 Quiz content is locked. Enroll in the course to take this quiz.</p>
    </div>
  </template>

  <template v-else>
    <QuizQuestion
      v-for="(q, i) in quiz.questions"
      :key="i"
      :question="q"
      :questionNumber="i + 1"
      @answer="setAnswer(i, $event)"
    />
    <p v-if="error" class="error">{{ error }}</p>
    <button class="submit-btn" :disabled="submitting" @click="handleSubmit">
      {{ submitting ? 'Submitting...' : 'Submit Quiz' }}
    </button>
  </template>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { Quiz } from '@/api/quizApi'
import { quizApi } from '@/api/quizApi'
import QuizQuestion from '@/components/QuizQuestion.vue'

export interface GradeResult {
  score: number
  total: number
  feedback: string
  responses: number[]
  correctAnswers?: number[]
}

const props = defineProps<{
  quiz: Quiz
  enrolled: boolean
  enrollmentId: string
}>()

const emit = defineEmits<{
  (e: 'submitted', result: GradeResult): void
}>()

const answers = ref<number[]>(new Array(props.quiz.questions.length))
const submitting = ref(false)
const error = ref('')

function setAnswer(index: number, value: number) {
  answers.value[index] = value
}

async function handleSubmit() {
  submitting.value = true
  error.value = ''
  try {
    const totalQuestions = props.quiz.questions.length
    const payload = Array.from({ length: totalQuestions }, (_, i) =>
      answers.value[i] !== undefined ? answers.value[i] : -1
    )
    const gradeRes = await quizApi.submit(props.quiz.id, props.enrollmentId, payload)
    emit('submitted', gradeRes.data)
  } catch (e: any) {
    error.value = e.response?.data?.error || e.response?.data?.message || 'Failed to submit quiz.'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
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

.error {
  color: #e94560;
  margin-top: 0.75rem;
}
</style>
