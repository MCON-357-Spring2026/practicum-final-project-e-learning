<template>
  <div class="teacher-card">
    <div class="teacher-info">
      <h3>{{ teacher.firstName }} {{ teacher.lastName }}</h3>
      <p class="department">{{ DEPARTMENT_LABELS[teacher.department] ?? 'No department' }}</p>
      <p class="courses">{{ courseCount }} {{ courseCount === 1 ? 'course' : 'courses' }}</p>
    </div>
    <div class="card-actions">
      <button v-if="showPromote" class="promote-btn" @click.prevent="$emit('promote', teacher.id)">Promote to Admin</button>
      <router-link :to="detailLink" class="view-btn">View Details</router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/store/auth'
import { DEPARTMENT_LABELS } from '@/constants/departments'
import type { LimitedTeacher } from '@/api/teacherApi'

const props = withDefaults(defineProps<{
  teacher: LimitedTeacher
  showPromote?: boolean
}>(), {
  showPromote: false
})

defineEmits<{
  promote: [id: string]
}>()

const authStore = useAuthStore()

const courseCount = computed(() => Object.keys(props.teacher.courses ?? {}).length)

const detailLink = computed(() => {
  const user = authStore.user
  const isPrivileged = user && (user.role === 'ADMIN' || user.id === props.teacher.id)
  return isPrivileged
    ? `/teachers/${props.teacher.id}`
    : `/teachers/${props.teacher.id}/preview`
})
</script>

<style scoped>
.teacher-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: box-shadow 0.2s;
}

.teacher-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.teacher-info h3 {
  margin: 0 0 0.25rem;
  font-size: 1.1rem;
}

.department {
  color: #666;
  font-size: 0.9rem;
  margin: 0 0 0.2rem;
}

.courses {
  color: #888;
  font-size: 0.85rem;
  margin: 0;
}

.card-actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.promote-btn {
  padding: 0.4rem 0.8rem;
  background-color: #2ecc71;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 0.85rem;
  cursor: pointer;
  white-space: nowrap;
}

.promote-btn:hover {
  background-color: #27ae60;
}

.view-btn {
  padding: 0.4rem 1rem;
  background-color: #e94560;
  color: #fff;
  border-radius: 4px;
  font-size: 0.9rem;
  text-decoration: none;
  white-space: nowrap;
}

.view-btn:hover {
  background-color: #c73550;
  text-decoration: none;
}
</style>
