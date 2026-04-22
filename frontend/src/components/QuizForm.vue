<template>
  <form @submit.prevent="$emit('submit')" class="quiz-form">
    <div class="form-group">
      <label for="title">Quiz Title</label>
      <input id="title" v-model="form.title" type="text" required />
    </div>

    <!-- Questions section -->
    <div class="questions-section">
      <h2>Questions</h2>
      <div v-for="(question, qi) in form.questions" :key="qi" class="question-card">
        <div class="question-header">
          <strong>Question {{ qi + 1 }}</strong>
          <button type="button" class="remove-btn" @click="removeQuestion(qi)">&times;</button>
        </div>
        <div class="form-group">
          <label>Question Text</label>
          <input v-model="question.questionText" type="text" required placeholder="Enter question..." />
        </div>
        <div class="choices-section">
          <label>Choices</label>
          <div v-for="(_, ci) in question.options" :key="ci" class="choice-row">
            <label class="radio-label" :class="{ 'correct-choice': question.correctOptionIndex === ci }">
              <input
                type="radio"
                :name="`correct-${qi}`"
                :checked="question.correctOptionIndex === ci"
                @change="question.correctOptionIndex = ci"
              />
              <input
                v-model="question.options[ci]"
                type="text"
                class="choice-input"
                required
                :placeholder="`Choice ${ci + 1}`"
              />
            </label>
            <button
              type="button"
              class="remove-btn small"
              :disabled="question.options.length <= 1"
              @click="removeChoice(qi, ci)"
            >&times;</button>
          </div>
          <button type="button" class="add-btn" @click="addChoice(qi)">+ Add Choice</button>
        </div>
        <p v-if="question.correctOptionIndex === -1" class="field-error">Select a correct answer</p>
      </div>
      <button type="button" class="add-btn" @click="addQuestion">+ Add Question</button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>
    <button type="submit" :disabled="loading || !isValid">{{ loading ? 'Saving...' : submitLabel }}</button>
  </form>
</template>

<script setup lang="ts">
import { computed } from 'vue'

export interface QuestionForm {
  questionText: string
  options: string[]
  correctOptionIndex: number
}

export interface QuizFormData {
  title: string
  questions: QuestionForm[]
}

const props = defineProps<{
  form: QuizFormData
  submitLabel: string
  loading: boolean
  error: string
}>()

defineEmits<{
  (e: 'submit'): void
}>()

const isValid = computed(() => {
  if (!props.form.title.trim()) return false
  if (props.form.questions.length === 0) return false
  return props.form.questions.every(q =>
    q.questionText.trim() !== '' &&
    q.options.length >= 1 &&
    q.options.every(o => o.trim() !== '') &&
    q.correctOptionIndex >= 0 &&
    q.correctOptionIndex < q.options.length
  )
})

function addQuestion() {
  props.form.questions.push({ questionText: '', options: [''], correctOptionIndex: -1 })
}

function removeQuestion(index: number) {
  props.form.questions.splice(index, 1)
}

function addChoice(qi: number) {
  props.form.questions[qi].options.push('')
}

function removeChoice(qi: number, ci: number) {
  const q = props.form.questions[qi]
  q.options.splice(ci, 1)
  if (q.correctOptionIndex === ci) {
    q.correctOptionIndex = -1
  } else if (q.correctOptionIndex > ci) {
    q.correctOptionIndex--
  }
}
</script>

<style scoped>
.quiz-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.form-group input,
.form-group textarea {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
}

.questions-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.questions-section h2 {
  margin: 0;
}

.question-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.choices-section {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.choice-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
  padding: 0.35rem 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.radio-label.correct-choice {
  border-color: #2ecc71;
  background-color: #eafaf1;
}

.choice-input {
  flex: 1;
  border: none !important;
  outline: none;
  font-size: 1rem;
  background: transparent;
}

.remove-btn {
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  font-size: 14px;
  cursor: pointer;
  line-height: 1;
  padding: 0;
  flex-shrink: 0;
}

.remove-btn.small {
  width: 22px;
  height: 22px;
  font-size: 13px;
}

.remove-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.add-btn {
  align-self: flex-start;
  background: none;
  color: #e94560;
  border: 1px dashed #e94560;
  border-radius: 4px;
  padding: 0.4rem 0.75rem;
  cursor: pointer;
  font-size: 0.9rem;
}

.field-error {
  color: #e94560;
  font-size: 0.85rem;
  margin: 0;
}

button[type="submit"] {
  padding: 0.6rem;
  background-color: #e94560;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
}

button[type="submit"]:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error {
  color: #e94560;
}
</style>
