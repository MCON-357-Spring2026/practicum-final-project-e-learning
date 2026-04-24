<template>
  <div class="course-card" @click="navigate">
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
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import type { Course } from '@/api/courseApi'

const props = withDefaults(defineProps<{
  course: Course
  enrolled?: boolean
}>(), { enrolled: false })

const router = useRouter()
const authStore = useAuthStore()

function navigate() {
  const user = authStore.user
  const hasAccess =
    props.enrolled ||
    user?.role === 'ADMIN' ||
    (user?.role === 'TEACHER' && user.id === props.course.instructorId)

  if (hasAccess) {
    router.push(`/courses/${props.course.id}`)
  } else {
    router.push(`/courses/${props.course.id}/preview`)
  }
}
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
