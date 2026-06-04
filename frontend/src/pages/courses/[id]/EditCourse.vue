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
          <select id="department" v-model="form.department" required>
            <option value="" disabled>-- Select Department --</option>
            <option v-for="dept in DEPARTMENTS" :key="dept" :value="dept">{{ DEPARTMENT_LABELS[dept] }}</option>
          </select>
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
          <button type="button" class="remove-img-btn" @click="clearImage">&times;</button>
        </div>
      </div>
      <p v-if="error" class="error">{{ error }}</p>
      <button type="submit" :disabled="saving">{{ saving ? 'Saving...' : 'Save Changes' }}</button>
    </form>

    <!-- Lessons Section -->
    <section v-if="!loadingCourse && !loadError" class="manage-section">
      <div class="section-header">
        <h2>Lessons</h2>
        <router-link :to="`/courses/${id}/create-lesson`" class="add-link">+ Add Lesson</router-link>
      </div>
      <p v-if="loadingLessons">Loading lessons...</p>
      <ul v-else-if="lessons.length" class="item-list">
        <li v-for="lesson in lessons" :key="lesson.id" class="item-row">
          <span class="item-title">{{ lesson.title }}</span>
          <div class="item-actions">
            <router-link :to="`/courses/${id}/lessons/${lesson.id}`" class="action-link view">View</router-link>
            <router-link :to="`/courses/${id}/lessons/${lesson.id}/edit`" class="action-link edit">Edit</router-link>
          </div>
        </li>
      </ul>
      <p v-else class="empty-text">No lessons yet.</p>
    </section>

    <!-- Quizzes Section -->
    <section v-if="!loadingCourse && !loadError" class="manage-section">
      <div class="section-header">
        <h2>Quizzes</h2>
        <router-link :to="`/courses/${id}/create-quiz`" class="add-link">+ Add Quiz</router-link>
      </div>
      <p v-if="loadingQuizzes">Loading quizzes...</p>
      <ul v-else-if="quizzes.length" class="item-list">
        <li v-for="quiz in quizzes" :key="quiz.id" class="item-row">
          <span class="item-title">{{ quiz.title }}</span>
          <div class="item-actions">
            <router-link :to="`/courses/${id}/quiz/${quiz.id}`" class="action-link view">View</router-link>
            <router-link :to="`/courses/${id}/quiz/${quiz.id}/edit`" class="action-link edit">Edit</router-link>
          </div>
        </li>
      </ul>
      <p v-else class="empty-text">No quizzes yet.</p>
    </section>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { courseApi } from '@/api/courseApi'
import { lessonApi } from '@/api/lessonApi'
import { quizApi } from '@/api/quizApi'
import { DEPARTMENTS, DEPARTMENT_LABELS } from '@/constants/departments'

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
const imagePreview = ref('')

const lessons = ref<any[]>([])
const quizzes = ref<any[]>([])
const loadingLessons = ref(true)
const loadingQuizzes = ref(true)

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
    if (course.image) imagePreview.value = course.image
  } catch (e: any) {
    loadError.value = e.response?.data?.error || 'Failed to load course'
  } finally {
    loadingCourse.value = false
  }

  // Load lessons and quizzes
  try {
    const lessonRes = await lessonApi.getPreviewsByCourseId(props.id)
    lessons.value = lessonRes.data
  } catch { /* empty */ } finally {
    loadingLessons.value = false
  }
  try {
    const quizRes = await quizApi.getPreviewsByCourseId(props.id)
    quizzes.value = quizRes.data
  } catch { /* empty */ } finally {
    loadingQuizzes.value = false
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

.remove-img-btn {
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

.manage-section {
  margin-top: 2rem;
  border-top: 1px solid #eee;
  padding-top: 1.5rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.section-header h2 {
  margin: 0;
  font-size: 1.25rem;
}

.add-link {
  color: #e94560;
  font-weight: 600;
  text-decoration: none;
  font-size: 0.95rem;
}

.add-link:hover {
  text-decoration: underline;
}

.item-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.6rem 0.75rem;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 6px;
}

.item-title {
  font-weight: 500;
}

.item-actions {
  display: flex;
  gap: 0.75rem;
}

.action-link {
  text-decoration: none;
  font-size: 0.85rem;
  font-weight: 500;
}

.action-link.view {
  color: #333;
}

.action-link.edit {
  color: #e94560;
}

.action-link:hover {
  text-decoration: underline;
}

.empty-text {
  color: #999;
  font-size: 0.9rem;
}
</style>
