<template>
  <div class="create-course-page">
    <h1>Create Course</h1>
    <form @submit.prevent="handleCreate" class="course-form">
      <div class="form-group">
        <label for="title">Title</label>
        <input id="title" v-model="form.title" type="text" required />
      </div>
      <div class="form-group">
        <label for="description">Description</label>
        <textarea id="description" v-model="form.description" rows="4"></textarea>
      </div>
      <div class="form-row">
        <div class="form-group">
          <label for="department">Department</label>
          <input id="department" v-model="form.department" type="text" required />
        </div>
        <div class="form-group">
          <label for="courseNum">Course Number</label>
          <input id="courseNum" v-model.number="form.courseNum" type="number" required />
        </div>
        <div class="form-group">
          <label for="credits">Credits</label>
          <input id="credits" v-model.number="form.credits" type="number" required />
        </div>
      </div>
      <div class="form-group">
        <label for="image">Image URL</label>
        <input id="image" v-model="form.image" type="text" />
      </div>
      <p v-if="error" class="error">{{ error }}</p>
      <button type="submit" :disabled="loading">{{ loading ? 'Creating...' : 'Create Course' }}</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCourseStore } from '../store/course'

const courseStore = useCourseStore()
const router = useRouter()

const form = reactive({
  title: '',
  description: '',
  department: '',
  courseNum: 0,
  credits: 0,
  instructor: '',
  image: '',
  lessonIDs: [] as number[]
})

const error = ref('')
const loading = ref(false)

async function handleCreate() {
  loading.value = true
  error.value = ''
  try {
    const created = await courseStore.create(form)
    router.push(`/courses/${created.id}`)
  } catch (e: any) {
    error.value = e.response?.data?.error || 'Failed to create course'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.create-course-page {
  max-width: 600px;
  margin: 2rem auto;
}

.course-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-row {
  display: flex;
  gap: 1rem;
}

.form-row .form-group {
  flex: 1;
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
