<template>
  <div class="create-lesson-page">
    <h1>Create Lesson</h1>
    <form @submit.prevent="handleCreate" class="lesson-form">
      <div class="form-group">
        <label for="title">Title</label>
        <input id="title" v-model="form.title" type="text" required />
      </div>
      <div class="form-group">
        <label for="description">Description</label>
        <textarea id="description" v-model="form.description" rows="4"></textarea>
      </div>
      <div class="form-group">
        <label for="minutes">Duration (minutes)</label>
        <input id="minutes" v-model.number="form.minutes" type="number" required />
      </div>
      <p v-if="error" class="error">{{ error }}</p>
      <button type="submit" :disabled="loading">{{ loading ? 'Creating...' : 'Create Lesson' }}</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { lessonApi } from '../api/lessonApi'

const props = defineProps<{
  courseId: string
}>()

const router = useRouter()

const form = reactive({
  title: '',
  description: '',
  minutes: 0,
  resources: [] as string[]
})

const error = ref('')
const loading = ref(false)

async function handleCreate() {
  loading.value = true
  error.value = ''
  try {
    await lessonApi.create(form)
    router.push(`/courses/${props.courseId}`)
  } catch (e: any) {
    error.value = e.response?.data?.error || 'Failed to create lesson'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.create-lesson-page {
  max-width: 600px;
  margin: 2rem auto;
}

.lesson-form {
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
