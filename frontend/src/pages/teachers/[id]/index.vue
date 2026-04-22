<template>
  <div class="teacher-detail">
    <p v-if="loading">Loading teacher details...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <template v-else-if="teacher">
      <h1>{{ teacher.firstName }} {{ teacher.lastName }}</h1>

      <section class="info-section">
        <h2>Personal Information</h2>
        <ul class="info-list">
          <li><strong>Email:</strong> {{ teacher.email ?? 'N/A' }}</li>
          <li><strong>Gender:</strong> {{ teacher.gender ?? 'N/A' }}</li>
          <li><strong>Date of Birth:</strong> {{ formattedDob }}</li>
          <li v-if="teacher.address">
            <strong>Address:</strong>
            {{ teacher.address.street }}, {{ teacher.address.city }},
            {{ teacher.address.state }} {{ teacher.address.zipCode }}
          </li>
        </ul>
      </section>

      <section class="info-section">
        <h2>Department</h2>
        <p>{{ teacher.department ?? 'Not assigned' }}</p>
      </section>

      <section class="info-section">
        <h2>Courses ({{ courseCount }})</h2>
        <ul v-if="courseCount > 0" class="info-list">
          <li v-for="(title, id) in teacher.courses" :key="id">
            <router-link :to="`/courses/${id}`">{{ title }}</router-link>
          </li>
        </ul>
        <p v-else>No courses yet.</p>
      </section>

      <section class="info-section">
        <h2 class="collapsible" @click="enrollmentsOpen = !enrollmentsOpen">
          Enrollments ({{ enrollmentCount }})
          <span class="toggle-icon">{{ enrollmentsOpen ? '▾' : '▸' }}</span>
        </h2>
        <template v-if="enrollmentsOpen">
          <ul v-if="enrollmentCount > 0" class="info-list">
            <li v-for="(title, id) in teacher.enrollments" :key="id">{{ title }}</li>
          </ul>
          <p v-else>No enrollments.</p>
        </template>
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { teacherApi } from '@/api/teacherApi'
import type { FullTeacher } from '@/api/teacherApi'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const teacher = ref<FullTeacher | null>(null)
const loading = ref(true)
const error = ref('')
const enrollmentsOpen = ref(false)

const courseCount = computed(() => Object.keys(teacher.value?.courses ?? {}).length)
const enrollmentCount = computed(() => Object.keys(teacher.value?.enrollments ?? {}).length)
const formattedDob = computed(() => {
  if (!teacher.value?.dateOfBirth) return 'N/A'
  return new Date(teacher.value.dateOfBirth).toLocaleDateString()
})

onMounted(async () => {
  const id = route.params.id as string
  const user = authStore.user
  const isPrivileged = user && (user.role === 'ADMIN' || user.id === id)

  if (!isPrivileged) {
    router.replace(`/teachers/${id}/preview`)
    return
  }

  try {
    const response = await teacherApi.getById(id)
    teacher.value = response.data as FullTeacher
  } catch (e: any) {
    error.value = e.response?.data?.error || 'Failed to load teacher'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.teacher-detail {
  max-width: 700px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.teacher-detail h1 {
  margin-bottom: 1.5rem;
}

.info-section {
  margin-bottom: 1.5rem;
}

.info-section h2 {
  font-size: 1.1rem;
  color: #555;
  margin-bottom: 0.5rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 0.25rem;
}

.info-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.collapsible {
  cursor: pointer;
  user-select: none;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.collapsible:hover {
  color: #333;
}

.toggle-icon {
  font-size: 0.9rem;
}

.info-list li {
  padding: 0.3rem 0;
  font-size: 0.95rem;
}

.error {
  color: #e94560;
}
</style>
