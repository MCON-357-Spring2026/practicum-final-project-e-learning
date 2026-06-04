<template>
  <div class="lesson-preview-card">
    <div class="lesson-info">
      <h3>{{ lesson.title }}</h3>
      <p class="description">{{ lesson.description }}</p>
    </div>
    <span class="duration">{{ lesson.minutes }} min</span>
  </div>
</template>

<script setup lang="ts">
import type { LessonPreview } from '@/api/lessonApi'

defineProps<{
  lesson: LessonPreview
}>()
</script>

<style scoped>
.lesson-preview-card {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  transition: box-shadow 0.2s;
}

.lesson-preview-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.lesson-info {
  flex: 1;
  min-width: 0;
}

.lesson-info h3 {
  margin: 0 0 0.25rem;
  font-size: 1.1rem;
}

.description {
  color: #666;
  font-size: 0.9rem;
  margin: 0;
}

.duration {
  flex-shrink: 0;
  margin-left: 1rem;
  padding: 0.25rem 0.5rem;
  background-color: #f0f0f0;
  border-radius: 4px;
  font-size: 0.85rem;
  color: #333;
  white-space: nowrap;
}
</style>
