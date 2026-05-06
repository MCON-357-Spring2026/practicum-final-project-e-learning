<template>
  <div class="inbox-view">
    <div class="inbox-header">
      <h2>Inbox</h2>
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
import { onMounted, ref } from 'vue'
import { messageApi } from '@/api/messageApi'
import MessagePreviewCard from './MessagePreviewCard.vue'
import type { MessagePreviewData } from './MessagePreviewCard.vue'

const props = defineProps<{
  userId: string
}>()

const messages = ref<MessagePreviewData[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  if (!props.userId) {
    error.value = 'Missing user context for inbox.'
    loading.value = false
    return
  }

  try {
    const { data } = await messageApi.getByReceiverId(props.userId)
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
.inbox-header {
  margin-bottom: 1rem;
}

.inbox-header h2 {
  font-size: 1.2rem;
  font-weight: 500;
  color: #202124;
  margin: 0;
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
