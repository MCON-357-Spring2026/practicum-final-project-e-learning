<template>
  <div
    class="message-preview"
    :class="{ unread: !message.read }"
    @click="$router.push(`/messages/${message.id}`)"
  >
    <div class="preview-left">
      <div class="avatar" :class="{ 'avatar-unread': !message.read }">
        {{ initial }}
      </div>
    </div>
    <div class="preview-content">
      <div class="preview-top">
        <span class="preview-name" :class="{ bold: !message.read }">{{ displayName }}</span>
        <span class="preview-date">{{ formattedDate }}</span>
      </div>
      <div class="preview-subject" :class="{ bold: !message.read }">
        {{ message.subject || '(no subject)' }}
      </div>
      <div class="preview-snippet">{{ snippet }}</div>
    </div>
    <div v-if="!message.read" class="unread-dot"></div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

export interface MessagePreviewData {
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

const props = defineProps<{
  message: MessagePreviewData
  mode?: 'inbox' | 'sent'
}>()

const displayName = computed(() => {
  if (props.mode === 'sent') {
    return props.message.receiverName || props.message.receiverEmail || 'Unknown'
  }
  return props.message.senderName || props.message.senderEmail || 'Unknown'
})

const initial = computed(() => {
  return displayName.value.charAt(0).toUpperCase()
})

const snippet = computed(() => {
  const body = props.message.body || ''
  return body.length > 100 ? body.substring(0, 100) + '…' : body
})

const formattedDate = computed(() => {
  if (!props.message.sentAt) return ''
  const d = new Date(props.message.sentAt)
  const now = new Date()
  if (d.toDateString() === now.toDateString()) {
    return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  }
  if (d.getFullYear() === now.getFullYear()) {
    return d.toLocaleDateString([], { month: 'short', day: 'numeric' })
  }
  return d.toLocaleDateString([], { month: 'short', day: 'numeric', year: 'numeric' })
})
</script>

<style scoped>
.message-preview {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.6rem 1rem;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.15s;
}

.message-preview:hover {
  background-color: #f5f5f5;
  box-shadow: inset 3px 0 0 #e94560;
}

.message-preview.unread {
  background-color: #f2f6fc;
}

.preview-left {
  flex-shrink: 0;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #ccc;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.95rem;
  font-weight: 600;
}

.avatar-unread {
  background-color: #e94560;
}

.preview-content {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.preview-top {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.preview-name {
  font-size: 0.9rem;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.preview-date {
  font-size: 0.75rem;
  color: #888;
  white-space: nowrap;
  flex-shrink: 0;
  margin-left: 0.5rem;
}

.preview-subject {
  font-size: 0.85rem;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.preview-snippet {
  font-size: 0.8rem;
  color: #888;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 0.1rem;
}

.bold {
  font-weight: 700;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #e94560;
  flex-shrink: 0;
}
</style>
