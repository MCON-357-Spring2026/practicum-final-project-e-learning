<template>
  <div class="course-details">
    <p v-if="loading">Loading course...</p>
    <p v-else-if="courseStore.error" class="error">{{ courseStore.error }}</p>
    <template v-else-if="course">
      <!-- Header: title + image -->
      <div class="course-header">
        <h1>{{ course.title }}</h1>
        <img v-if="course.image" :src="course.image" :alt="course.title" class="course-image" />
      </div>

      <!-- Info band -->
      <div class="info-band">
        <span v-if="instructorName">{{ instructorName }}</span>
        <span>{{ course.department }} {{ course.courseNum }}</span>
        <span>{{ course.credits }} credits</span>
      </div>

      <!-- Description -->
      <p class="description">{{ course.description }}</p>

      <!-- Two-column: Lessons & Quizzes -->
      <div class="content-columns">
        <div class="column">
          <h2>Lessons</h2>
          <p v-if="lessons.length === 0">No lessons yet.</p>
          <ul v-else>
            <li v-for="lesson in lessons" :key="lesson.id">
              <router-link :to="`/courses/${id}/lessons/${lesson.id}`">
                {{ lesson.title }}
              </router-link>
            </li>
          </ul>
        </div>
        <div class="column">
          <h2>Quizzes</h2>
          <p v-if="quizzes.length === 0">No quizzes yet.</p>
          <ul v-else>
            <li v-for="quiz in quizzes" :key="quiz.id">
              <router-link :to="`/courses/${id}/quiz/${quiz.id}`">
                {{ quiz.title }}
              </router-link>
            </li>
          </ul>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useCourseStore } from '@/store/course'
import { lessonApi, type Lesson } from '@/api/lessonApi'
import { quizApi, type Quiz } from '@/api/quizApi'
import axiosClient from '@/api/axiosClient'

const props = defineProps<{
  id: string
}>()

const courseStore = useCourseStore()
const instructorName = ref('')
const lessons = ref<Lesson[]>([])
const quizzes = ref<Quiz[]>([])
const loading = ref(true)

const course = computed(() => courseStore.currentCourse)

onMounted(async () => {
  await courseStore.fetchById(props.id)
  const c = courseStore.currentCourse
  if (!c) {
    loading.value = false
    return
  }

  const promises: Promise<void>[] = []

  // Fetch instructor name
  if (c.instructorId) {
    promises.push(
      axiosClient.get(`/teachers/${c.instructorId}/preview`).then(res => {
        instructorName.value = `${res.data.firstName} ${res.data.lastName}`
      }).catch(() => {})
    )
  }

  // Fetch lessons
  if (c.lessonIDs?.length) {
    promises.push(
      Promise.all(c.lessonIDs.map(lid => lessonApi.getById(lid))).then(results => {
        lessons.value = results.map(r => r.data)
      }).catch(() => {})
    )
  }

  // Fetch quizzes
  if (c.quizIDs?.length) {
    promises.push(
      Promise.all(c.quizIDs.map(qid => quizApi.getById(qid))).then(results => {
        quizzes.value = results.map(r => r.data)
      }).catch(() => {})
    )
  }

  await Promise.all(promises)
  loading.value = false
})
</script>

<style scoped>
.course-details {
  max-width: 900px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.course-header {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.course-header h1 {
  flex: 1;
  margin: 0;
}

.course-image {
  width: 200px;
  height: 140px;
  object-fit: cover;
  border-radius: 8px;
}

.info-band {
  display: flex;
  gap: 1.5rem;
  padding: 0.75rem 0;
  margin-top: 0.75rem;
  border-top: 1px solid #ddd;
  border-bottom: 1px solid #ddd;
  color: #555;
  font-size: 0.95rem;
}

.description {
  margin: 1.25rem 0;
  line-height: 1.7;
}

.content-columns {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-top: 1rem;
}

.column h2 {
  margin-bottom: 0.75rem;
}

.column ul {
  padding-left: 1.25rem;
}

.column li {
  margin-bottom: 0.5rem;
}

.error {
  color: #e94560;
}
</style>
