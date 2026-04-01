<template>
  <div class="quiz-question">
    <h3>{{ questionNumber }}. {{ question.text }}</h3>
    <div class="options">
      <label
        v-for="(option, index) in question.options"
        :key="index"
        class="option"
        :class="{ selected: selectedAnswer === index }"
      >
        <input
          type="radio"
          :name="`question-${questionNumber}`"
          :value="index"
          v-model="selectedAnswer"
          @change="$emit('answer', index)"
        />
        {{ option }}
      </label>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

defineProps<{
  question: { text: string; options: string[] }
  questionNumber: number
}>()

defineEmits<{
  (e: 'answer', index: number): void
}>()

const selectedAnswer = ref<number | null>(null)
</script>

<style scoped>
.quiz-question {
  margin-bottom: 1.5rem;
}

.quiz-question h3 {
  margin-bottom: 0.75rem;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.option {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
}

.option.selected {
  border-color: #e94560;
  background-color: #fef0f2;
}
</style>
