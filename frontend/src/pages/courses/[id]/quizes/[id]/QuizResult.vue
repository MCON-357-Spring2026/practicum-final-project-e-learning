<template>
  <div class="result-card">
    <h2>Results</h2>
    <p class="score">Score: {{ Number(result.score).toFixed(1) }}%</p>
    <p class="feedback">{{ result.feedback }}</p>

    <div v-if="result.correctAnswers?.length" class="review">
      <div v-for="(q, i) in quiz.questions" :key="i" class="review-question">
        <h3>{{ i + 1 }}. {{ q.questionText }}</h3>
        <p v-if="result.responses[i] === -1" class="no-answer">No answer provided</p>
        <div class="review-options">
          <div
            v-for="(option, j) in q.options"
            :key="j"
            class="review-option"
            :class="{
              correct: j === result.correctAnswers![i],
              wrong: j === result.responses[i] && j !== result.correctAnswers![i]
            }"
          >
            {{ option }}
          </div>
        </div>
      </div>
    </div>

    <div class="result-actions">
      <button class="retry-btn" @click="$emit('retry')">Retry Quiz</button>
      <router-link :to="`/courses/${courseId}`" class="back-link">Return to course</router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Quiz } from '@/api/quizApi'
import type { GradeResult } from './QuizTaker.vue'

defineProps<{
  quiz: Quiz
  courseId: string
  result: GradeResult
}>()

defineEmits<{
  (e: 'retry'): void
}>()
</script>

<style scoped>
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

.back-link {
  color: #e94560;
  text-decoration: none;
}

.back-link:hover {
  text-decoration: underline;
}
</style>
