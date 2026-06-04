<template>
  <div class="blasts-view">
    <div class="blasts-header">
      <h2>Sent Blasts</h2>
    </div>

    <p v-if="loading" class="status-text">Loading blasts...</p>
    <p v-else-if="error" class="error">{{ error }}</p>
    <p v-else-if="blasts.length === 0" class="status-text">No blasts sent yet</p>

    <div v-else class="blast-list">
      <MessageBlastPreviewCard
        v-for="blast in blasts"
        :key="blast.id"
        :blast="blast"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import MessageBlastPreviewCard from './MessageBlastPreviewCard.vue'
import { messageBlastApi } from '@/api/messageBlastApi'
import type { MessageBlastPreviewData } from '@/api/messageBlastApi'

const props = defineProps<{
  privileged: boolean
}>()

const router = useRouter()
const blasts = ref<MessageBlastPreviewData[]>([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  if (!props.privileged) {
    router.replace('/unauthorized')
    return
  }

  try {
    const { data } = await messageBlastApi.getMine()
    blasts.value = [...data].sort(
      (a, b) => new Date(b.sentAt).getTime() - new Date(a.sentAt).getTime()
    )
  } catch {
    error.value = 'Failed to load blasts'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.blasts-header {
  margin-bottom: 1rem;
}

.blasts-header h2 {
  font-size: 1.2rem;
  font-weight: 500;
  color: #202124;
  margin: 0;
}

.blast-list {
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
