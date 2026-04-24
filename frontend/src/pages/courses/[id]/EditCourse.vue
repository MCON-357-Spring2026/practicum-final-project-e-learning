<template>
  <div class="edit-course-page">
    <h1>Edit Course</h1>
    <p v-if="loadingCourse">Loading course...</p>
    <p v-else-if="loadError" class="error">{{ loadError }}</p>
    <form v-else @submit.prevent="handleUpdate" class="course-form">
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
      <button type="submit" :disabled="saving">{{ saving ? 'Saving...' : 'Save Changes' }}</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { courseApi } from '@/api/courseApi'

const props = defineProps<{
  id: string
}>()

const router = useRouter()

const form = reactive({
  title: '',
  description: '',
  department: '',
  courseNum: 0,
  credits: 0,
  image: ''
})

const loadingCourse = ref(true)
const loadError = ref('')
const error = ref('')
const saving = ref(false)

onMounted(async () => {
  try {
    const response = await courseApi.getById(props.id)
    const course = response.data
    form.title = course.title
    form.description = course.description
    form.department = course.department
    form.courseNum = course.courseNum
    form.credits = course.credits
    form.image = course.image
  } catch (e: any) {
    loadError.value = e.response?.data?.error || 'Failed to load course'
  } finally {
    loadingCourse.value = false
  }
})

async function handleUpdate() {
  saving.value = true
  error.value = ''
  try {
    await courseApi.update(props.id, form)
    router.push(`/courses/${props.id}`)
  } catch (e: any) {
    error.value = e.response?.data?.error || 'Failed to update course'
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.edit-course-page {
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
