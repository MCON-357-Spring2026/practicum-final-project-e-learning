<template>
  <article class="blast-card">
    <div class="blast-card__avatar">
      {{ initial }}
    </div>

    <div class="blast-card__content">
      <div class="blast-card__top">
        <div>
          <p class="blast-card__label">Sender</p>
          <p class="blast-card__sender">{{ senderDisplay }}</p>
        </div>
        <p class="blast-card__date">{{ formattedDate }}</p>
      </div>

      <div class="blast-card__meta">
        <span>{{ recipientCountLabel }}</span>
      </div>

      <h3 class="blast-card__subject">{{ blast.subject || '(no subject)' }}</h3>
      <p class="blast-card__body">{{ snippet }}</p>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { MessageBlastPreviewData } from '@/api/messageBlastApi'

const props = defineProps<{
  blast: MessageBlastPreviewData
}>()

const senderDisplay = computed(() => {
  const sender = props.blast.sender
  if (!sender) return 'Unknown sender'
  return sender.email ? `${sender.name} (${sender.email})` : sender.name
})

const initial = computed(() => senderDisplay.value.charAt(0).toUpperCase())

const recipientCountLabel = computed(() => {
  const count = props.blast.recipients?.length ?? 0
  return `${count} recipient${count === 1 ? '' : 's'}`
})

const snippet = computed(() => {
  const body = props.blast.body || ''
  return body.length > 30 ? `${body.slice(0, 30)}...` : body
})

const formattedDate = computed(() => {
  if (!props.blast.sentAt) return ''
  const date = new Date(props.blast.sentAt)
  const now = new Date()

  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  }

  if (date.getFullYear() === now.getFullYear()) {
    return date.toLocaleDateString([], { month: 'short', day: 'numeric' })
  }

  return date.toLocaleDateString([], { month: 'short', day: 'numeric', year: 'numeric' })
})
</script>

<style scoped>
.blast-card {
  display: flex;
  gap: 0.9rem;
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
  background: #fff;
}

.blast-card:hover {
  background: #fafafa;
}

.blast-card__avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: #e94560;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex-shrink: 0;
}

.blast-card__content {
  flex: 1;
  min-width: 0;
}

.blast-card__top {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
  align-items: flex-start;
}

.blast-card__label {
  margin: 0;
  font-size: 0.72rem;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: #888;
}

.blast-card__sender {
  margin: 0.15rem 0 0;
  font-size: 0.95rem;
  color: #202124;
  font-weight: 600;
}

.blast-card__date {
  margin: 0;
  font-size: 0.8rem;
  color: #888;
  white-space: nowrap;
}

.blast-card__meta {
  margin-top: 0.45rem;
  font-size: 0.82rem;
  color: #666;
}

.blast-card__subject {
  margin: 0.45rem 0 0;
  font-size: 0.95rem;
  font-weight: 600;
  color: #202124;
}

.blast-card__body {
  margin: 0.3rem 0 0;
  font-size: 0.84rem;
  color: #777;
}
</style>
