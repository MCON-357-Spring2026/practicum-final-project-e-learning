<template>
  <div class="teachers-page">
    <h1>Teachers</h1>
    <p v-if="loading">Loading teachers...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <div v-else class="teacher-list">
      <TeacherCard v-for="teacher in teachers" :key="teacher.id" :teacher="teacher" />
      <p v-if="teachers.length === 0">No teachers found.</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { teacherApi } from '@/api/teacherApi'
import type { LimitedTeacher } from '@/api/teacherApi'
import TeacherCard from '@/components/TeacherCard.vue'

const teachers = ref<LimitedTeacher[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const response = await teacherApi.getAll()
    teachers.value = response.data
  } catch (e: any) {
    error.value = e.response?.data?.error || 'Failed to load teachers'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.teachers-page {
  max-width: 800px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.teachers-page h1 {
  margin-bottom: 1.5rem;
}

.teacher-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.error {
  color: #e94560;
}
</style>
