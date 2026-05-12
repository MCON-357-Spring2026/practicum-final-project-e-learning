<template>
  <div class="pending-card">
    <div class="pending-info">
      <h3>{{ teacher.firstName }} {{ teacher.lastName }}</h3>
      <p class="department">{{ teacher.department ?? 'No department' }}</p>
    </div>
    <button class="approve-btn" @click="$emit('approve', teacher.id)">Approve</button>
  </div>
</template>

<script setup lang="ts">
import type { LimitedTeacher } from '@/api/teacherApi'

defineProps<{
  teacher: LimitedTeacher
}>()

defineEmits<{
  approve: [id: string]
}>()
</script>

<style scoped>
.pending-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: box-shadow 0.2s;
}

.pending-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.pending-info h3 {
  margin: 0 0 0.25rem;
  font-size: 1.1rem;
}

.department {
  color: #666;
  font-size: 0.9rem;
  margin: 0;
}

.approve-btn {
  padding: 0.45rem 1rem;
  background-color: #2ecc71;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
}

.approve-btn:hover {
  background-color: #27ae60;
}
</style>
