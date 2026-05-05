<template>
	<div class="messages-layout">
		<MessagingSidebar />

		<div class="blasts-page">
			<div class="blasts-header">
				<h1>Sent Blasts</h1>
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
	</div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import MessagingSidebar from '@/components/MessagingSidebar.vue'
import MessageBlastPreviewCard from '@/components/MessageBlastPreviewCard.vue'
import { messageBlastApi } from '@/api/messageBlastApi'
import type { MessageBlastPreviewData } from '@/api/messageBlastApi'
import { useAuthStore } from '@/store/auth'

const authStore = useAuthStore()
const router = useRouter()

const blasts = ref<MessageBlastPreviewData[]>([])
const loading = ref(true)
const error = ref('')

const isTeacherOrAdmin = computed(() => {
	const role = authStore.user?.role
	return role === 'TEACHER' || role === 'ADMIN'
})

onMounted(async () => {
	if (!authStore.user) {
		router.push('/login')
		return
	}

	if (!isTeacherOrAdmin.value) {
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
.messages-layout {
	display: flex;
}

.blasts-page {
	flex: 1;
	max-width: 900px;
	margin: 2rem auto;
	padding: 0 1rem;
}

.blasts-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 1rem;
}

.blasts-header h1 {
	font-size: 1.5rem;
	font-weight: 400;
	color: #202124;
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
