<template>
  <div class="lesson-player">
    <!-- Header -->
    <h2 class="title">{{ lesson.title }}</h2>
    <p class="description">{{ lesson.description }}</p>
    <p class="duration">⏱ Approximately {{ lesson.minutes }} minutes</p>

    <!-- Lesson text -->
    <section v-if="lesson.text" class="section">
      <h3>Lesson Content</h3>
      <div class="text-content">{{ lesson.text }}</div>
    </section>

    <!-- Media -->
    <section v-if="lesson.media && lesson.media.length" class="section">
      <h3>Media</h3>
      <div class="media-grid">
        <div v-for="(url, index) in lesson.media" :key="index" class="media-item">
          <video v-if="isVideo(url)" controls class="media-video">
            <source :src="url" />
            Your browser does not support the video tag.
          </video>
          <img v-else :src="url" :alt="`Media ${index + 1}`" class="media-image" />
        </div>
      </div>
    </section>

    <!-- Resources -->
    <section v-if="lesson.resources && lesson.resources.length" class="section">
      <h3>Additional Resources</h3>
      <ul class="resource-list">
        <li v-for="(resource, index) in lesson.resources" :key="index">
          <a :href="resource" target="_blank" rel="noopener noreferrer">{{ resource }}</a>
        </li>
      </ul>
    </section>

    <!-- Mark as Complete -->
    <div v-if="showComplete" class="complete-section">
      <button v-if="completed" class="btn-complete done" disabled>✓ Lesson Completed</button>
      <button v-else-if="completing" class="btn-complete" disabled>Marking...</button>
      <button v-else class="btn-complete" @click="$emit('complete')">Mark as Complete</button>
      <p v-if="completeError" class="error">{{ completeError }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Lesson } from '@/api/lessonApi'

defineProps<{
  lesson: Lesson
  showComplete?: boolean
  completed?: boolean
  completing?: boolean
  completeError?: string
}>()

defineEmits<{
  (e: 'complete'): void
}>()

const videoExtensions = ['.mp4', '.webm', '.ogg', '.mov']

function isVideo(url: string): boolean {
  const lower = url.toLowerCase()
  return videoExtensions.some(ext => lower.includes(ext))
}
</script>

<style scoped>
.lesson-player {
  padding: 1.5rem;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.title {
  margin: 0 0 0.5rem;
}

.description {
  color: #555;
  margin: 0 0 0.25rem;
}

.duration {
  color: #888;
  font-size: 0.9rem;
  margin: 0 0 1rem;
}

.section {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

.section h3 {
  margin: 0 0 0.75rem;
}

.text-content {
  line-height: 1.7;
  white-space: pre-wrap;
  color: #333;
}

.media-grid {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.media-image {
  max-width: 100%;
  border-radius: 6px;
}

.media-video {
  max-width: 100%;
  border-radius: 6px;
}

.resource-list {
  padding-left: 1.25rem;
}

.resource-list li + li {
  margin-top: 0.4rem;
}

.resource-list a {
  color: #e94560;
  word-break: break-all;
}

.complete-section {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

.btn-complete {
  padding: 0.6rem 1.5rem;
  background-color: #e94560;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  cursor: pointer;
}

.btn-complete:hover:not(:disabled) {
  background-color: #c73a52;
}

.btn-complete:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-complete.done {
  background-color: #28a745;
  opacity: 1;
}

.error {
  color: #e94560;
  margin-top: 0.5rem;
}
</style>
