<template>
  <div class="courses-page">
    <h1>Courses</h1>
    <p v-if="courseStore.loading">Loading courses...</p>
    <p v-else-if="courseStore.error" class="error">{{ courseStore.error }}</p>
    <div v-else class="course-grid">
      <CourseCard
        v-for="course in courseStore.courses"
        :key="course.id"
        :course="course"
      />
    </div>
    <p v-if="!courseStore.loading && courseStore.courses.length === 0">No courses available yet.</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useCourseStore } from '../../store/course'
import CourseCard from '../../components/CourseCard.vue'

const courseStore = useCourseStore()

onMounted(() => {
  courseStore.fetchAll()
})
</script>

<style scoped>
.courses-page h1 {
  margin-bottom: 1rem;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.error {
  color: #e94560;
}
</style>
