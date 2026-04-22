<template>
  <div class="teacher-preview">
    <p v-if="loading">Loading teacher...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <template v-else-if="teacher">
      <h1>{{ teacher.firstName }} {{ teacher.lastName }}</h1>

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
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { teacherApi } from '@/api/teacherApi'
import type { LimitedTeacher } from '@/api/teacherApi'

const route = useRoute()

const teacher = ref<LimitedTeacher | null>(null)
const loading = ref(true)
const error = ref('')

const courseCount = computed(() => Object.keys(teacher.value?.courses ?? {}).length)

onMounted(async () => {
  const id = route.params.id as string
  try {
    const response = await teacherApi.getById(id)
    teacher.value = response.data as LimitedTeacher
  } catch (e: any) {
    error.value = e.response?.data?.error || 'Failed to load teacher'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.teacher-preview {
  max-width: 700px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.teacher-preview h1 {
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

.info-list li {
  padding: 0.3rem 0;
  font-size: 0.95rem;
}

.error {
  color: #e94560;
}
</style>
