<template>
  <div class="course-lessons-page">
    <h1>Lessons</h1>
    <p v-if="loading">Loading lessons...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <template v-else>
      <p v-if="lessons.length === 0">No lessons for this course yet.</p>
      <div v-else class="lesson-list">
        <router-link
          v-for="lesson in lessons"
          :key="lesson.id"
          :to="`/courses/${props.id}/lessons/${lesson.id}`"
          class="lesson-link"
        >
          <LessonPreviewCard :lesson="lesson" />
        </router-link>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { lessonApi, type LessonPreview } from '@/api/lessonApi'
import LessonPreviewCard from '@/components/LessonPreviewCard.vue'

const props = defineProps<{
  id: string
}>()

const lessons = ref<LessonPreview[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    const { data } = await lessonApi.getPreviewsByCourseId(props.id)
    lessons.value = data
  } catch {
    error.value = 'Failed to load lessons'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.course-lessons-page {
  max-width: 700px;
  margin: 2rem auto;
}

.lesson-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.lesson-link {
  text-decoration: none;
  color: inherit;
}

.error {
  color: #e94560;
}
</style>