<template>
  <div class="progress-bar-container">
    <div class="progress-bar-fill" :style="{ width: percentage + '%' }"></div>
    <span class="progress-text">{{ percentage }}%</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  percentage: number
}>()
</script>

<style scoped>
.progress-bar-container {
  position: relative;
  width: 100%;
  height: 24px;
  background-color: #e0e0e0;
  border-radius: 12px;
  overflow: hidden;
}

.progress-bar-fill {
  height: 100%;
  background-color: #e94560;
  border-radius: 12px;
  transition: width 0.3s ease;
}

.progress-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 0.75rem;
  font-weight: bold;
  color: #333;
}
</style>
