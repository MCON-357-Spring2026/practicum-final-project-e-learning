<template>
  <div class="message-page">
    <div class="message-toolbar">
      <button class="btn-back" @click="$router.push('/messages')">
        &larr; Back to Inbox
      </button>
      <div class="toolbar-right">
        <button class="btn-unread" @click="markUnread" v-if="message?.read">Mark as unread</button>
        <button class="btn-delete" @click="deleteMessage">Delete</button>
      </div>
    </div>

    <p v-if="loading" class="loading-text">Loading message...</p>
    <p v-else-if="error" class="error">{{ error }}</p>

    <div v-else-if="message" class="message-container">
      <div class="message-header">
        <h1 class="message-subject">{{ message.subject || '(no subject)' }}</h1>
      </div>

      <div class="message-meta">
        <div class="meta-row">
          <div class="sender-info">
            <div class="avatar">{{ senderInitial }}</div>
            <div>
              <span class="sender-name">{{ message.senderName || 'Unknown' }}</span>
              <span class="sender-email">&lt;{{ message.senderEmail || '—' }}&gt;</span>
            </div>
          </div>
          <span class="message-date">{{ formattedDate }}</span>
        </div>
        <div class="recipient-row">
          to <span class="recipient-name">{{ message.receiverName || 'Unknown' }}</span>
          <span class="recipient-email">&lt;{{ message.receiverEmail || '—' }}&gt;</span>
        </div>
      </div>

      <div class="message-body">
        {{ message.body }}
      </div>

      <div class="message-actions">
        <button class="btn-secondary" @click="$router.push('/messages')">
          View other messages with this user
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { messageApi } from '@/api/messageApi'

interface MessageDTO {
  id: string
  senderId: string
  senderName: string
  senderEmail: string
  receiverId: string
  receiverName: string
  receiverEmail: string
  subject: string
  body: string
  read: boolean
  sentAt: string
}

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const message = ref<MessageDTO | null>(null)
const loading = ref(true)
const error = ref('')

const senderInitial = computed(() => {
  if (!message.value?.senderName) return '?'
  return message.value.senderName.charAt(0).toUpperCase()
})

const formattedDate = computed(() => {
  if (!message.value?.sentAt) return ''
  const d = new Date(message.value.sentAt)
  const now = new Date()
  if (d.toDateString() === now.toDateString()) {
    return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  }
  return d.toLocaleDateString([], { month: 'short', day: 'numeric', year: 'numeric' }) +
    ' ' + d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
})

async function markUnread() {
  if (!message.value) return
  try {
    await messageApi.markAsUnread(message.value.id)
    message.value.read = false
  } catch {
    error.value = 'Failed to mark as unread'
  }
}

async function deleteMessage() {
  if (!message.value) return
  try {
    await messageApi.delete(message.value.id)
    router.push('/messages')
  } catch {
    error.value = 'Failed to delete message'
  }
}

onMounted(async () => {
  if (!authStore.user) {
    router.push('/login')
    return
  }
  try {
    const id = route.params.id as string
    const { data } = await messageApi.getById(id)
    message.value = data
    if (!data.read) {
      await messageApi.markAsRead(id)
    }
  } catch {
    error.value = 'Message not found'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.message-page {
  max-width: 800px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.message-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e0e0e0;
}

.toolbar-right {
  display: flex;
  gap: 0.5rem;
}

.btn-back {
  background: none;
  border: none;
  color: #555;
  font-size: 0.9rem;
  cursor: pointer;
  padding: 0.4rem 0.6rem;
  border-radius: 4px;
}

.btn-back:hover {
  background-color: #f0f0f0;
}

.btn-delete {
  background: none;
  border: 1px solid #d93025;
  color: #d93025;
  font-size: 0.85rem;
  cursor: pointer;
  padding: 0.35rem 0.8rem;
  border-radius: 4px;
}

.btn-delete:hover {
  background-color: #fce8e6;
}

.btn-unread {
  background: none;
  border: 1px solid #888;
  color: #555;
  font-size: 0.85rem;
  cursor: pointer;
  padding: 0.35rem 0.8rem;
  border-radius: 4px;
}

.btn-unread:hover {
  background-color: #f0f0f0;
}

.loading-text {
  color: #888;
  text-align: center;
  padding: 2rem;
}

.message-container {
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
}

.message-header {
  padding: 1.25rem 1.5rem 0;
}

.message-subject {
  font-size: 1.4rem;
  font-weight: 400;
  color: #202124;
  margin: 0;
}

.message-meta {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #f0f0f0;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sender-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #e94560;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.1rem;
  font-weight: 600;
  flex-shrink: 0;
}

.sender-name {
  font-weight: 600;
  color: #202124;
  margin-right: 0.4rem;
}

.sender-email {
  color: #888;
  font-size: 0.85rem;
}

.message-date {
  color: #888;
  font-size: 0.8rem;
  white-space: nowrap;
}

.recipient-row {
  margin-top: 0.3rem;
  padding-left: 3.25rem;
  color: #888;
  font-size: 0.85rem;
}

.recipient-name {
  font-weight: 500;
  color: #555;
  margin-right: 0.25rem;
}

.recipient-email {
  color: #888;
  font-size: 0.85rem;
}

.message-body {
  padding: 1.5rem;
  font-size: 0.95rem;
  line-height: 1.7;
  color: #333;
  white-space: pre-wrap;
  min-height: 120px;
}

.message-actions {
  padding: 1rem 1.5rem;
  border-top: 1px solid #f0f0f0;
}

.btn-secondary {
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  color: #333;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
}

.btn-secondary:hover {
  background-color: #e4e4e4;
}

.error {
  color: #e94560;
  text-align: center;
  padding: 2rem;
}
</style>
