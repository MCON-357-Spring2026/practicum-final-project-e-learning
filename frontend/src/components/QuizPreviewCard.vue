<template>
  <div class="quiz-preview-card">
    <div class="quiz-info">
      <h3>{{ quiz.title }}</h3>
      <p class="question-count">{{ quiz.numberOfQuestions }} question{{ quiz.numberOfQuestions !== 1 ? 's' : '' }}</p>
    </div>
    <div v-if="grade" class="attempt-badge">
      <span class="attempted-label">Completed</span>
      <span class="score">{{ grade.score }}%</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { QuizPreview } from '@/api/quizApi'

interface Grade {
  score: number
}

defineProps<{
  quiz: QuizPreview
  grade?: Grade | null
}>()
</script>

<style scoped>
.quiz-preview-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  transition: box-shadow 0.2s;
}

.quiz-preview-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.quiz-info {
  flex: 1;
  min-width: 0;
}

.quiz-info h3 {
  margin: 0 0 0.25rem;
  font-size: 1.1rem;
}

.question-count {
  color: #666;
  font-size: 0.9rem;
  margin: 0;
}

.attempt-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.2rem;
  flex-shrink: 0;
  margin-left: 1rem;
}

.attempted-label {
  font-size: 0.75rem;
  color: #fff;
  background-color: #28a745;
  padding: 0.15rem 0.5rem;
  border-radius: 4px;
}

.score {
  font-weight: 600;
  font-size: 0.95rem;
  color: #333;
}
</style>
