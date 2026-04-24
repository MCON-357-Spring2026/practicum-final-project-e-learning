<template>
  <div class="inbox-page">
    <div class="inbox-header">
      <h1>Inbox</h1>
      <div class="inbox-actions">
        <router-link to="/messages/sent" class="btn-link">Sent</router-link>
        <router-link to="/messages/compose" class="btn-compose">Compose</router-link>
      </div>
    </div>

    <p v-if="loading" class="status-text">Loading messages...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <p v-else-if="messages.length === 0" class="status-text">Your inbox is empty</p>

    <div v-else class="message-list">
      <MessagePreviewCard
        v-for="msg in messages"
        :key="msg.id"
        :message="msg"
        mode="inbox"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { messageApi } from '@/api/messageApi'
import MessagePreviewCard from '@/components/MessagePreviewCard.vue'
import type { MessagePreviewData } from '@/components/MessagePreviewCard.vue'

const authStore = useAuthStore()
const router = useRouter()

const messages = ref<MessagePreviewData[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  if (!authStore.user) {
    router.push('/login')
    return
  }
  try {
    const { data } = await messageApi.getByReceiverId(authStore.user.id)
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
.inbox-page {
  max-width: 800px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.inbox-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.inbox-header h1 {
  font-size: 1.5rem;
  font-weight: 400;
  color: #202124;
}

.inbox-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.btn-link {
  color: #555;
  font-size: 0.9rem;
  text-decoration: none;
  padding: 0.4rem 0.6rem;
  border-radius: 4px;
}

.btn-link:hover {
  background-color: #f0f0f0;
  text-decoration: none;
}

.btn-compose {
  background-color: #e94560;
  color: #fff;
  padding: 0.45rem 1rem;
  border-radius: 4px;
  font-size: 0.9rem;
  text-decoration: none;
}

.btn-compose:hover {
  background-color: #d63a54;
  text-decoration: none;
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
