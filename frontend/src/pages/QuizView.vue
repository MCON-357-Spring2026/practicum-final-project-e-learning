<template>
  <div class="quiz-view">
    <h1>Quiz</h1>
    <p v-if="loading">Loading quiz...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <p v-else>Quiz functionality coming soon.</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { quizApi } from '../api/quizApi'

const props = defineProps<{
  courseId: string
  quizId: string
}>()

const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    await quizApi.getById(props.quizId)
  } catch (e: any) {
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

.error {
  color: #e94560;
}
</style>
