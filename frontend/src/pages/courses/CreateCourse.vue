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
        <label for="image">Course Image</label>
        <div class="image-input-group">
          <label class="upload-btn" for="imageFile">Upload Image</label>
          <input id="imageFile" type="file" accept="image/*" class="file-input" @change="onImageSelected" />
          <span class="or-divider">or</span>
          <input id="image" v-model="form.image" type="text" placeholder="Paste an image URL" />
        </div>
        <div v-if="imagePreview" class="image-preview">
          <img :src="imagePreview" alt="Course image preview" />
          <button type="button" class="remove-btn" @click="clearImage">&times;</button>
        </div>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
      <button type="submit" :disabled="loading">{{ loading ? 'Creating...' : 'Create Course' }}</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { courseApi } from '@/api/courseApi'

const authStore = useAuthStore()
const router = useRouter()

const form = reactive({
  title: '',
  description: '',
  department: '',
  courseNum: 0,
  credits: 0,
  image: ''
})

const error = ref('')
const loading = ref(false)
const imagePreview = ref('')

function onImageSelected(e: Event) {
  const input = e.target as HTMLInputElement
  if (!input.files?.length) return
  const file = input.files[0]
  const reader = new FileReader()
  reader.onload = () => {
    const dataUrl = reader.result as string
    form.image = dataUrl
    imagePreview.value = dataUrl
  }
  reader.readAsDataURL(file)
  input.value = ''
}

function clearImage() {
  form.image = ''
  imagePreview.value = ''
}

watch(() => form.image, (val) => {
  if (val && !val.startsWith('data:')) {
    imagePreview.value = val
  }
})

async function handleCreate() {
  loading.value = true
  error.value = ''
  try {
    const res = await courseApi.create(form)
    router.push(`/courses/${res.data.id}`)
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

.image-input-group {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.image-input-group input[type="text"] {
  flex: 1;
}

.file-input {
  display: none;
}

.upload-btn {
  padding: 0.5rem 1rem;
  background-color: #e94560;
  color: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  white-space: nowrap;
}

.upload-btn:hover {
  background-color: #d63350;
}

.or-divider {
  color: #999;
  font-size: 0.85rem;
}

.image-preview {
  position: relative;
  display: inline-block;
  margin-top: 0.5rem;
}

.image-preview img {
  max-width: 100%;
  max-height: 200px;
  border-radius: 6px;
  border: 1px solid #ddd;
}

.remove-btn {
  position: absolute;
  top: 4px;
  right: 4px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  font-size: 1rem;
  line-height: 24px;
  text-align: center;
  cursor: pointer;
  padding: 0;
}
</style>
