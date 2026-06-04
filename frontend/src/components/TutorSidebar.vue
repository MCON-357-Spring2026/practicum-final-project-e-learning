<template>
	<aside class="tutor-sidebar">
		<h2>Subjects</h2>

		<div v-if="loadingPreviews" class="sidebar-status">Loading...</div>
		<div v-else-if="previewError" class="sidebar-status sidebar-error">{{ previewError }}</div>

		<ul class="subject-list" v-else>
			<li v-for="dept in DEPARTMENTS" :key="dept" class="subject-item">
				<button class="subject-toggle" @click="toggle(dept)">
					<span class="arrow" :class="{ expanded: expanded[dept] }">&#9654;</span>
					<span>{{ DEPARTMENT_LABELS[dept] }}</span>
				</button>

				<div v-if="expanded[dept]" class="conversation-list">
					<router-link
						v-for="preview in conversationsBySubject[dept]"
						:key="preview.conversationId"
						:to="{ name: 'Tutor', params: { conversationId: preview.conversationId } }"
						class="conversation-link"
						:class="{ active: preview.conversationId === activeConversationId }"
					>
						{{ preview.title }}
					</router-link>

					<p v-if="!conversationsBySubject[dept]?.length" class="no-conversations">No conversations yet</p>

					<button class="new-conversation-btn" @click="startNewConversation(dept)">
						+ New Conversation
					</button>
				</div>
			</li>
		</ul>
	</aside>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive } from 'vue'
import { DEPARTMENTS, DEPARTMENT_LABELS, type Department } from '@/constants/departments'
import { chatApi, type ChatPreview } from '@/api/chatApi'
import { useAuthStore } from '@/store/auth'

const props = defineProps<{ activeConversationId?: string }>()
const emit = defineEmits<{ newConversation: [dept: Department] }>()

const auth = useAuthStore()
const previews = ref<ChatPreview[]>([])
const loadingPreviews = ref(true)
const previewError = ref('')
const expanded = reactive<Record<string, boolean>>({})

const conversationsBySubject = computed(() => {
	const map: Partial<Record<Department, ChatPreview[]>> = {}
	for (const dept of DEPARTMENTS) {
		map[dept] = previews.value.filter(p => p.subject === dept)
	}
	return map
})

function toggle(dept: Department) {
	expanded[dept] = !expanded[dept]
}

function startNewConversation(dept: Department) {
	emit('newConversation', dept)
}

async function loadPreviews() {
	const userId = auth.user?.id
	if (!userId) return

	loadingPreviews.value = true
	previewError.value = ''
	try {
		const { data } = await chatApi.getPreviewsByPersonId(userId)
		previews.value = data
	} catch {
		previewError.value = 'Failed to load conversations.'
	} finally {
		loadingPreviews.value = false
	}
}

onMounted(() => {
	loadPreviews()
})

defineExpose({ loadPreviews })
</script>

<style scoped>
.tutor-sidebar {
	padding: 1rem;
	border-right: 1px solid #2f2f4a;
	background: #17172f;
	overflow-y: auto;
}

.tutor-sidebar h2 {
	margin: 0 0 0.8rem;
	color: white;
	font-size: 1rem;
}

.sidebar-status {
	color: #8b8ba6;
	font-size: 0.85rem;
	padding: 0.5rem 0;
}

.sidebar-error {
	color: #e94560;
}

.subject-list {
	list-style: none;
	margin: 0;
	padding: 0;
	display: flex;
	flex-direction: column;
	gap: 0.25rem;
}

.subject-toggle {
	display: flex;
	align-items: center;
	gap: 0.5rem;
	width: 100%;
	padding: 0.55rem 0.7rem;
	border: none;
	border-radius: 8px;
	background: #1f1f3d;
	color: #f2f2f5;
	font: inherit;
	font-size: 0.9rem;
	font-weight: 600;
	cursor: pointer;
	text-align: left;
}

.subject-toggle:hover {
	background: #2a2a50;
}

.arrow {
	font-size: 0.65rem;
	transition: transform 0.2s;
	display: inline-block;
}

.arrow.expanded {
	transform: rotate(90deg);
}

.conversation-list {
	padding: 0.3rem 0 0.3rem 1.4rem;
	display: flex;
	flex-direction: column;
	gap: 0.2rem;
}

.conversation-link {
	display: block;
	padding: 0.4rem 0.6rem;
	border-radius: 6px;
	color: #c8c8d6;
	text-decoration: none;
	font-size: 0.84rem;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.conversation-link:hover {
	background: #2a2a50;
	color: #f2f2f5;
}

.conversation-link.active {
	background: rgba(233, 69, 96, 0.15);
	color: #e94560;
	border: 1px solid rgba(233, 69, 96, 0.35);
}

.no-conversations {
	color: #6b6b8a;
	font-size: 0.8rem;
	margin: 0.2rem 0;
	padding: 0.2rem 0.6rem;
}

.new-conversation-btn {
	border: 1px dashed #3a3a58;
	border-radius: 6px;
	background: transparent;
	color: #8b8ba6;
	font: inherit;
	font-size: 0.82rem;
	padding: 0.4rem 0.6rem;
	cursor: pointer;
	text-align: left;
	margin-top: 0.15rem;
}

.new-conversation-btn:hover {
	border-color: #e94560;
	color: #e94560;
}
</style>
