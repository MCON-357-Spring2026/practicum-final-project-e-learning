<template>
  <div class="messages-layout">
    <MessagingSidebar />
    <div class="compose-page">
      <div class="compose-header">
        <h1>Compose Message</h1>
      </div>

    <form @submit.prevent="handleSend" class="compose-form">
      <!-- Blast Toggle (Teachers/Admins only) -->
      <div class="form-group toggle-row" v-if="isTeacherOrAdmin">
        <label>Message Blast</label>
        <label class="toggle">
          <input type="checkbox" v-model="blastMode" @change="onBlastToggle" />
          <span class="slider"></span>
        </label>
      </div>

      <!-- BLAST MODE -->
      <template v-if="blastMode && isTeacherOrAdmin">
        <div class="form-group">
          <label>Recipients</label>
          <select v-model="blastTarget" required>
            <option value="" disabled>Select recipients</option>
            <option value="teachers">All Teachers</option>
            <option value="admin">All Admins</option>
            <option value="teachers and admin">All Teachers &amp; Admins</option>
            <optgroup label="Course Enrollees">
              <option v-for="c in courses" :key="c.id" :value="c.id">{{ c.title }} (enrolled students)</option>
            </optgroup>
          </select>
        </div>
      </template>

      <!-- NORMAL MODE -->
      <template v-else>
        <!-- Course Selection -->
        <div class="form-group">
          <label>Course</label>
          <select v-model="selectedCourseId" @change="onCourseChange" required>
            <option value="" disabled>Select a course</option>
            <option v-for="c in courses" :key="c.id" :value="c.id">{{ c.title }}</option>
          </select>
        </div>

        <!-- Recipient Selection (Teachers pick a student, Students auto-target instructor) -->
        <div class="form-group" v-if="isTeacherOrAdmin">
          <label>Recipient (Student)</label>
          <select v-model="recipientId" :disabled="loadingStudents || students.length === 0" required>
            <option value="" disabled>
              {{ loadingStudents ? 'Loading students...' : students.length === 0 ? 'No enrolled students' : 'Select a student' }}
            </option>
            <option v-for="s in students" :key="s.id" :value="s.id">
              {{ s.firstName }} {{ s.lastName }} ({{ s.username }})
            </option>
          </select>
        </div>

        <div class="form-group" v-else>
          <label>To</label>
          <input type="text" :value="instructorLabel" disabled class="recipient-display" />
        </div>
      </template>

      <!-- Subject -->
      <div class="form-group">
        <label>Subject</label>
        <input v-model="subject" type="text" placeholder="Subject" required />
      </div>

      <!-- Body -->
      <div class="form-group">
        <label>Message</label>
        <textarea v-model="body" rows="8" placeholder="Write your message..." required></textarea>
      </div>

      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="success" class="success">{{ blastMode ? 'Blast sent!' : 'Message sent!' }}</p>

      <div class="form-actions">
        <button type="submit" class="btn-send" :disabled="sending">
          {{ sending ? 'Sending...' : blastMode ? 'Send Blast' : 'Send Message' }}
        </button>
      </div>
    </form>
  </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import MessagingSidebar from '@/components/MessagingSidebar.vue'
import { courseApi } from '@/api/courseApi'
import { userApi } from '@/api/userApi'
import { messageApi } from '@/api/messageApi'
import axiosClient from '@/api/axiosClient'
import type { Course } from '@/api/courseApi'
import type { UserInfo } from '@/api/userApi'

const authStore = useAuthStore()
const router = useRouter()

const courses = ref<Course[]>([])
const selectedCourseId = ref('')
const recipientId = ref('')
const subject = ref('')
const body = ref('')

const students = ref<UserInfo[]>([])
const loadingStudents = ref(false)
const instructorName = ref('')
const instructorLabel = ref('')

const blastMode = ref(false)
const blastTarget = ref('')

const sending = ref(false)
const error = ref('')
const success = ref(false)

const isTeacherOrAdmin = computed(() => {
  const role = authStore.user?.role
  return role === 'TEACHER' || role === 'ADMIN'
})

onMounted(async () => {
  if (!authStore.user) {
    router.push('/login')
    return
  }

  try {
    if (isTeacherOrAdmin.value) {
      // Teachers see their own courses
      const { data } = await courseApi.getByInstructor(authStore.user.id)
      courses.value = data
    } else {
      // Students see courses they are enrolled in
      const { data: enrollments } = await axiosClient.get(`/enrollments/student/${authStore.user.id}`)
      const courseIds: string[] = enrollments.map((e: any) => e.courseId)
      const coursePromises = courseIds.map((id: string) => courseApi.getById(id))
      const results = await Promise.all(coursePromises)
      courses.value = results.map(r => r.data)
    }
  } catch {
    error.value = 'Failed to load courses'
  }
})

async function onCourseChange() {
  recipientId.value = ''
  students.value = []
  instructorName.value = ''
  instructorLabel.value = ''
  error.value = ''

  if (!selectedCourseId.value) return

  if (isTeacherOrAdmin.value) {
    // Load enrolled students for the selected course
    loadingStudents.value = true
    try {
      const { data: studentIds } = await axiosClient.get<string[]>(
        `/enrollments/course/${selectedCourseId.value}/students`
      )
      if (studentIds.length === 0) {
        students.value = []
        return
      }
      const userPromises = studentIds.map((id: string) => userApi.getById(id))
      const results = await Promise.all(userPromises)
      students.value = results.map(r => r.data)
    } catch {
      error.value = 'Failed to load students'
    } finally {
      loadingStudents.value = false
    }
  } else {
    // Students: resolve the course instructor
    const course = courses.value.find(c => c.id === selectedCourseId.value)
    if (course?.instructorId) {
      recipientId.value = course.instructorId
      try {
        const { data: instructor } = await userApi.getById(course.instructorId)
        instructorName.value = `${instructor.firstName} ${instructor.lastName}`
        instructorLabel.value = `${instructor.firstName} ${instructor.lastName} (${instructor.username})`
      } catch {
        instructorLabel.value = 'Instructor'
      }
    }
  }
}

function onBlastToggle() {
  // Reset fields when toggling
  blastTarget.value = ''
  recipientId.value = ''
  students.value = []
  selectedCourseId.value = ''
  error.value = ''
  success.value = false
}

async function handleSend() {
  sending.value = true
  error.value = ''
  success.value = false

  try {
    if (blastMode.value && isTeacherOrAdmin.value) {
      if (!blastTarget.value) {
        error.value = 'Please select recipients'
        sending.value = false
        return
      }
      await axiosClient.post('/message-blasts/', {
        recipients: blastTarget.value,
        subject: subject.value,
        body: body.value
      })
    } else {
      if (!recipientId.value) {
        error.value = 'Please select a recipient'
        sending.value = false
        return
      }
      await messageApi.send({
        senderId: authStore.user!.id,
        receiverId: recipientId.value,
        subject: subject.value,
        body: body.value
      })
    }
    success.value = true
    setTimeout(() => router.push('/messages/sent'), 1200)
  } catch {
    error.value = blastMode.value ? 'Failed to send blast' : 'Failed to send message'
  } finally {
    sending.value = false
  }
}
</script>

<style scoped>
.messages-layout {
  display: flex;
}

.compose-page {
  flex: 1;
  max-width: 700px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.compose-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.compose-header h1 {
  font-size: 1.5rem;
  font-weight: 400;
  color: #202124;
}

.btn-back {
  color: #555;
  font-size: 0.9rem;
  text-decoration: none;
  padding: 0.4rem 0.6rem;
  border-radius: 4px;
}

.btn-back:hover {
  background-color: #f0f0f0;
}

.compose-form {
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  font-size: 0.85rem;
  font-weight: 500;
  color: #333;
  margin-bottom: 0.3rem;
}

.toggle-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.toggle-row label:first-child {
  margin-bottom: 0;
}

.toggle {
  position: relative;
  display: inline-block;
  width: 44px;
  height: 24px;
}

.toggle input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: #ccc;
  border-radius: 24px;
  transition: 0.2s;
}

.slider::before {
  content: "";
  position: absolute;
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: #fff;
  border-radius: 50%;
  transition: 0.2s;
}

.toggle input:checked + .slider {
  background-color: #e94560;
}

.toggle input:checked + .slider::before {
  transform: translateX(20px);
}

.form-group select,
.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.55rem 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 0.95rem;
  font-family: inherit;
  background: #fff;
  box-sizing: border-box;
}

.form-group select:focus,
.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #e94560;
}

.form-group textarea {
  resize: vertical;
}

.recipient-display {
  background: #f5f5f5 !important;
  color: #333;
}

.form-actions {
  text-align: right;
  margin-top: 1rem;
}

.btn-send {
  background-color: #e94560;
  color: #fff;
  border: none;
  padding: 0.55rem 1.5rem;
  border-radius: 4px;
  font-size: 0.95rem;
  cursor: pointer;
}

.btn-send:hover:not(:disabled) {
  background-color: #d63a54;
}

.btn-send:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error {
  color: #e94560;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.success {
  color: #2e7d32;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}
</style>
