<template>
  <div class="course-details">
    <p v-if="courseStore.loading">Loading course...</p>
    <p v-else-if="courseStore.error" class="error">{{ courseStore.error }}</p>
    <template v-else-if="courseStore.currentCourse">
      <h1>{{ courseStore.currentCourse.title }}</h1>
      <p class="meta">
        {{ courseStore.currentCourse.department }} {{ courseStore.currentCourse.courseNum }}
        · {{ courseStore.currentCourse.credits }} credits
        · Instructor: {{ courseStore.currentCourse.instructor }}
      </p>
      <p>{{ courseStore.currentCourse.description }}</p>

      <h2>Lessons</h2>
      <p v-if="!courseStore.currentCourse.lessonIDs || courseStore.currentCourse.lessonIDs.length === 0">
        No lessons yet.
      </p>
      <ul v-else>
        <li v-for="lessonId in courseStore.currentCourse.lessonIDs" :key="lessonId">
          <router-link :to="`/courses/${id}/lessons/${lessonId}`">
            Lesson {{ lessonId }}
          </router-link>
        </li>
      </ul>
    </template>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useCourseStore } from '../store/course'

const props = defineProps<{
  id: string
}>()

const courseStore = useCourseStore()

onMounted(() => {
  courseStore.fetchById(props.id)
})
</script>

<style scoped>
.course-details h1 {
  margin-bottom: 0.25rem;
}

.meta {
  color: #666;
  margin-bottom: 1rem;
}

.error {
  color: #e94560;
}

ul {
  padding-left: 1.25rem;
}

li {
  margin-bottom: 0.5rem;
}
</style>
