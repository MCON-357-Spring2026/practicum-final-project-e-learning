<template>
  <div class="sent-view">
    <div class="sent-header">
      <h2>Sent</h2>
      <router-link v-if="isTeacherOrAdmin" to="/messages/blasts" class="btn-blasts">View Sent Blasts</router-link>
    </div>

    <p v-if="loading" class="status-text">Loading messages...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <p v-else-if="messages.length === 0" class="status-text">No sent messages</p>

    <div v-else class="message-list">
      <MessagePreviewCard
        v-for="msg in messages"
        :key="msg.id"
        :message="msg"
        mode="sent"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useAuthStore } from '@/store/auth'
import { messageApi } from '@/api/messageApi'
import MessagePreviewCard from './MessagePreviewCard.vue'
import type { MessagePreviewData } from './MessagePreviewCard.vue'

const props = defineProps<{
  userId: string
}>()

const authStore = useAuthStore()
const messages = ref<MessagePreviewData[]>([])
const loading = ref(true)
const error = ref('')

const isTeacherOrAdmin = computed(() => {
  const role = authStore.user?.role
  return role === 'TEACHER' || role === 'ADMIN'
})

onMounted(async () => {
  if (!props.userId) {
    error.value = 'Missing user context for sent messages.'
    loading.value = false
    return
  }

  try {
    const { data } = await messageApi.getBySenderIdDirect(props.userId)
    messages.value = data.sort(
      (a: MessagePreviewData, b: MessagePreviewData) =>
        new Date(b.sentAt).getTime() - new Date(a.sentAt).getTime()
    )
  } catch {
    error.value = 'Failed to load messages'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.sent-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.sent-header h2 {
  font-size: 1.2rem;
  font-weight: 500;
  color: #202124;
  margin: 0;
}

.btn-blasts {
  color: #e94560;
  font-size: 0.9rem;
  text-decoration: none;
  padding: 0.45rem 0.8rem;
  border: 1px solid #e94560;
  border-radius: 4px;
}

.btn-blasts:hover {
  background-color: #e94560;
  color: #fff;
}

.message-list {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}

.status-text {
  color: #888;
  text-align: center;
  padding: 3rem;
}

.error {
  color: #e94560;
  text-align: center;
  padding: 2rem;
}
</style>
