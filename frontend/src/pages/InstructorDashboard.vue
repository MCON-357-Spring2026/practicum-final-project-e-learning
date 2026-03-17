<template>
  <div class="instructor-dashboard">
    <h1>Instructor Dashboard</h1>
    <div class="actions">
      <router-link to="/create-course" class="btn">+ Create Course</router-link>
    </div>
    <p v-if="loading">Loading your courses...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <div v-else-if="courses.length" class="course-list">
      <div v-for="course in courses" :key="course.id" class="course-item">
        <h3>{{ course.title }}</h3>
        <p>{{ course.department }} {{ course.courseNum }} · {{ course.credits }} credits</p>
        <div class="course-actions">
          <router-link :to="`/courses/${course.id}`">View</router-link>
          <router-link :to="`/courses/${course.id}/create-lesson`">+ Lesson</router-link>
          <router-link :to="`/courses/${course.id}/create-quiz`">+ Quiz</router-link>
        </div>
      </div>
    </div>
    <p v-else>You haven't created any courses yet.</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { courseApi, type Course } from '../api/courseApi'

const courses = ref<Course[]>([])
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    // TODO: replace with actual instructor ID from auth store
    const response = await courseApi.getAll()
    courses.value = response.data
  } catch (e: any) {
    error.value = 'Failed to load courses'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.instructor-dashboard h1 {
  margin-bottom: 1rem;
}

.actions {
  margin-bottom: 1.5rem;
}

.btn {
  display: inline-block;
  padding: 0.5rem 1rem;
  background-color: #e94560;
  color: #fff;
  text-decoration: none;
  border-radius: 4px;
}

.course-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.course-item {
  padding: 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.course-item h3 {
  margin: 0 0 0.25rem;
}

.course-item p {
  color: #666;
  margin: 0 0 0.5rem;
}

.course-actions {
  display: flex;
  gap: 1rem;
}

.course-actions a {
  color: #e94560;
  text-decoration: none;
}

.error {
  color: #e94560;
}
</style>
