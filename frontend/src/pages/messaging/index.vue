<template>
  <div class="messages-layout">
    <MessagingSidebar />

    <div class="messages-page">
      <div class="messages-page-title">
        <h1>Messaging</h1>
      </div>

      <component
        :is="activeComponent"
        :user-id="effectiveUserId"
        :message-id="effectiveMessageId"
        :privileged="isTeacherOrAdmin"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/store/auth'
import MessagingSidebar from './components/MessagingSidebar.vue'
import InboxView from './components/InboxView.vue'
import SentView from './components/SentView.vue'
import ComposeView from './components/ComposeView.vue'
import BlastsView from './components/BlastsView.vue'
import MessageDetailView from './components/MessageDetailView.vue'

type MessagingView = 'inbox' | 'sent' | 'compose' | 'blasts' | 'detail'

const props = withDefaults(defineProps<{
  view?: MessagingView
  userId?: string
  messageId?: string
}>(), {
  view: 'inbox',
  userId: undefined,
  messageId: undefined
})

const authStore = useAuthStore()

const isTeacherOrAdmin = computed(() => {
  const role = authStore.user?.role
  return role === 'TEACHER' || role === 'ADMIN'
})

const effectiveUserId = computed(() => props.userId ?? authStore.user?.id ?? '')
const effectiveMessageId = computed(() => props.messageId ?? '')

const activeComponent = computed(() => {
  if (props.view === 'sent') return SentView
  if (props.view === 'compose') return ComposeView
  if (props.view === 'blasts') return BlastsView
  if (props.view === 'detail') return MessageDetailView
  return InboxView
})
</script>

<style scoped>
.messages-layout {
  display: flex;
}

.messages-page {
  flex: 1;
  max-width: 900px;
  margin: 0 auto 2rem;
  padding: 0 1rem;
}

.messages-page-title {
  min-height: 42px;
  display: flex;
  align-items: center;
  margin-bottom: 0.85rem;
}

.messages-page-title h1 {
  margin: 0;
}
</style>
