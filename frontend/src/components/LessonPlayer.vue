<template>
  <div class="lesson-player">
    <h2>{{ lesson.title }}</h2>
    <p class="description">{{ lesson.description }}</p>
    <p class="duration">⏱ {{ lesson.minutes }} minutes</p>
    <div v-if="lesson.resources && lesson.resources.length" class="resources">
      <h4>Resources</h4>
      <ul>
        <li v-for="(resource, index) in lesson.resources" :key="index">
          <a :href="resource" target="_blank">{{ resource }}</a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Lesson } from '../api/lessonApi'

defineProps<{
  lesson: Lesson
}>()
</script>

<style scoped>
.lesson-player {
  padding: 1.5rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.description {
  color: #555;
  margin: 0.5rem 0;
}

.duration {
  color: #888;
  font-size: 0.9rem;
}

.resources h4 {
  margin: 1rem 0 0.5rem;
}

.resources ul {
  padding-left: 1.25rem;
}

.resources a {
  color: #e94560;
}
</style>
