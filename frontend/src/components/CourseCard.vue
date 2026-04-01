<template>
  <div class="course-card" @click="$router.push(`/courses/${course.id}`)">
    <img v-if="course.image" :src="course.image" :alt="course.title" class="course-image" />
    <div class="course-image placeholder" v-else>📚</div>
    <div class="course-info">
      <h3>{{ course.title }}</h3>
      <p class="department">{{ course.department }} {{ course.courseNum }}</p>
      <p class="credits">{{ course.credits }} credits</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Course } from '../api/courseApi'

defineProps<{
  course: Course
}>()
</script>

<style scoped>
.course-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.course-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.course-image {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.course-image.placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 3rem;
  background-color: #f0f0f0;
}

.course-info {
  padding: 0.75rem;
}

.course-info h3 {
  margin: 0 0 0.25rem;
  font-size: 1.1rem;
}

.department {
  color: #666;
  font-size: 0.9rem;
  margin: 0 0 0.25rem;
}

.credits {
  color: #888;
  font-size: 0.85rem;
  margin: 0;
}
</style>
