<template>
  <div class="create-quiz-page">
    <h1>Create Quiz</h1>
    <form @submit.prevent="handleCreate" class="quiz-form">
      <div class="form-group">
        <label for="title">Quiz Title</label>
        <input id="title" v-model="form.title" type="text" required />
      </div>
      <p v-if="error" class="error">{{ error }}</p>
      <button type="submit" :disabled="loading">{{ loading ? 'Creating...' : 'Create Quiz' }}</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { quizApi } from '../api/quizApi'

const props = defineProps<{
  courseId: string
}>()

const router = useRouter()

const form = reactive({
  title: ''
})

const error = ref('')
const loading = ref(false)

async function handleCreate() {
  loading.value = true
  error.value = ''
  try {
    await quizApi.create(form)
    router.push(`/courses/${props.courseId}`)
  } catch (e: any) {
    error.value = e.response?.data?.error || 'Failed to create quiz'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.create-quiz-page {
  max-width: 600px;
  margin: 2rem auto;
}

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

.form-group input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
}

button {
  padding: 0.6rem;
  background-color: #e94560;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
}

button:disabled {
  opacity: 0.6;
}

.error {
  color: #e94560;
}
</style>
