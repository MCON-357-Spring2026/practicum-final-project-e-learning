<template>
  <div class="lesson-view">
    <p v-if="loading">Loading lesson...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <LessonPlayer v-else-if="lesson" :lesson="lesson" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { lessonApi, type Lesson } from '../api/lessonApi'
import LessonPlayer from '../components/LessonPlayer.vue'

const props = defineProps<{
  courseId: string
  lessonId: string
}>()

const lesson = ref<Lesson | null>(null)
const loading = ref(false)
const error = ref('')

onMounted(async () => {
  loading.value = true
  try {
    const response = await lessonApi.getById(props.lessonId)
    lesson.value = response.data
  } catch (e: any) {
    error.value = 'Lesson not found'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.lesson-view {
  max-width: 800px;
  margin: 2rem auto;
}

.error {
  color: #e94560;
}
</style>
