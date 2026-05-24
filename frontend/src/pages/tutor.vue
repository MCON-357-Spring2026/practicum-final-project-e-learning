<template>
	<section class="tutor-page">
		<header class="tutor-header">
			<div>
				<h1>Tutor Chat</h1>
				<p class="subtitle" v-if="conversation">{{ conversation.title }}</p>
				<p class="subtitle" v-else-if="pendingSubject">New {{ DEPARTMENT_LABELS[pendingSubject] }} conversation</p>
				<p class="subtitle" v-else>Ask questions and get guided help.</p>
			</div>
		</header>

		<div class="chat-shell">
			<TutorSidebar
				ref="sidebarRef"
				:activeConversationId="conversationId"
				@newConversation="handleNewConversation"
			/>

			<main class="chat-main">
				<div v-if="loading" class="status-state">Loading conversation...</div>
				<div v-else-if="error" class="status-state error-state">{{ error }}</div>

				<div class="messages" ref="messagesContainer">
					<article
						v-for="(msg, i) in visibleMessages"
						:key="i"
						:class="['message', msg.from === 'USER' ? 'message-user' : 'message-tutor']"
					>
						<p class="author">{{ msg.from === 'USER' ? 'You' : 'Tutor' }}</p>
						<p v-if="msg.from === 'USER'">{{ msg.message }}</p>
						<div v-else class="markdown-body" v-html="renderMarkdown(msg.message)"></div>
						<time class="timestamp">{{ formatTime(msg.time) }}</time>
					</article>
					<div v-if="sending" class="typing-indicator">Tutor is typing...</div>
					<p v-if="!loading && visibleMessages.length === 0 && !pendingSubject && !conversationId" class="empty-prompt">Select a subject to get started</p>
					<p v-else-if="!loading && visibleMessages.length === 0 && !pendingSubject" class="status-state">No messages yet.</p>
					<p v-if="!loading && pendingSubject && visibleMessages.length === 0" class="status-state">Send a message to start the conversation.</p>
				</div>

				<form class="chat-input" @submit.prevent="handleSend">
					<textarea
						v-model="newMessage"
						rows="3"
						placeholder="Type your message..."
						aria-label="Type your message"
						@keydown.enter.exact.prevent="handleSend"
					></textarea>
					<div class="actions">
						<span v-if="sendError" class="send-error">{{ sendError }}</span>
						<button type="submit" :disabled="!newMessage.trim() || sending">{{ sending ? 'Sending...' : 'Send' }}</button>
					</div>
				</form>
			</main>
		</div>
	</section>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { chatApi, type ChatConversation, type ChatMessage } from '@/api/chatApi'
import { useAuthStore } from '@/store/auth'
import { DEPARTMENT_LABELS, type Department } from '@/constants/departments'
import TutorSidebar from '@/components/TutorSidebar.vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import katex from 'katex'
import 'katex/dist/katex.min.css'

const props = defineProps<{ conversationId?: string }>()

const router = useRouter()
const auth = useAuthStore()

const conversation = ref<ChatConversation | null>(null)
const loading = ref(false)
const error = ref('')
const newMessage = ref('')
const sending = ref(false)
const sendError = ref('')
const messagesContainer = ref<HTMLElement | null>(null)
const sidebarRef = ref<InstanceType<typeof TutorSidebar> | null>(null)
const pendingSubject = ref<Department | null>(null)

const visibleMessages = computed(() =>
	(conversation.value?.messages ?? []).filter((m: ChatMessage) => m.from !== 'SYSTEM')
)

function formatTime(time: string): string {
	const date = new Date(time)
	return date.toLocaleString(undefined, {
		month: 'short', day: 'numeric',
		hour: 'numeric', minute: '2-digit'
	})
}

function renderMath(text: string): string {
	// Block math: $$...$$
	text = text.replace(/\$\$([\s\S]+?)\$\$/g, (_, expr) => {
		try {
			return katex.renderToString(expr.trim(), { displayMode: true, throwOnError: false })
		} catch {
			return `<code>${expr}</code>`
		}
	})
	// Inline math: $...$  (but not $$)
	text = text.replace(/\$([^$\n]+?)\$/g, (_, expr) => {
		try {
			return katex.renderToString(expr.trim(), { displayMode: false, throwOnError: false })
		} catch {
			return `<code>${expr}</code>`
		}
	})
	// \( ... \)  inline
	text = text.replace(/\\\(([\s\S]+?)\\\)/g, (_, expr) => {
		try {
			return katex.renderToString(expr.trim(), { displayMode: false, throwOnError: false })
		} catch {
			return `<code>${expr}</code>`
		}
	})
	// \[ ... \]  block
	text = text.replace(/\\\[([\s\S]+?)\\\]/g, (_, expr) => {
		try {
			return katex.renderToString(expr.trim(), { displayMode: true, throwOnError: false })
		} catch {
			return `<code>${expr}</code>`
		}
	})
	return text
}

function renderMarkdown(text: string): string {
	const withMath = renderMath(text)
	return DOMPurify.sanitize(marked.parse(withMath) as string, {
		ADD_TAGS: ['span', 'math', 'semantics', 'mrow', 'mi', 'mo', 'mn', 'msup', 'msub', 'mfrac', 'mover', 'munder', 'munderover', 'mtable', 'mtr', 'mtd', 'annotation'],
		ADD_ATTR: ['aria-hidden', 'encoding', 'mathvariant', 'stretchy', 'fence', 'separator', 'accent', 'accentunder', 'columnalign', 'rowalign', 'columnspacing', 'rowspacing', 'columnlines', 'rowlines', 'frame', 'framespacing', 'displaystyle', 'scriptlevel', 'xmlns', 'style', 'class', 'height', 'width', 'viewBox', 'preserveAspectRatio', 'd']
	})
}

function scrollToBottom() {
	nextTick(() => {
		if (messagesContainer.value) {
			messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
		}
	})
}

function handleNewConversation(dept: Department) {
	pendingSubject.value = dept
	conversation.value = null
	error.value = ''
	sendError.value = ''
	newMessage.value = ''
	if (props.conversationId) {
		router.replace({ name: 'Tutor' })
	}
}

async function loadConversation() {
	if (!props.conversationId) return

	pendingSubject.value = null
	loading.value = true
	error.value = ''
	try {
		const { data } = await chatApi.getById(props.conversationId)
		conversation.value = data
		scrollToBottom()
	} catch {
		error.value = 'Failed to load conversation.'
	} finally {
		loading.value = false
	}
}

async function startConversation(body: string) {
	const userId = auth.user?.id
	if (!userId || !pendingSubject.value) return

	sending.value = true
	sendError.value = ''
	newMessage.value = ''
	try {
		const { data } = await chatApi.start(userId, {
			subject: pendingSubject.value,
			userMessage: body
		})
		conversation.value = data
		pendingSubject.value = null
		sidebarRef.value?.loadPreviews()
		router.replace({ name: 'Tutor', params: { conversationId: data.conversationId } })
		scrollToBottom()
	} catch {
		sendError.value = 'Failed to start conversation.'
	} finally {
		sending.value = false
	}
}

async function sendMessage(body: string) {
	if (!props.conversationId) return

	sending.value = true
	sendError.value = ''
	newMessage.value = ''
	try {
		const { data } = await chatApi.sendMessage(props.conversationId, body)
		conversation.value = data
		scrollToBottom()
	} catch {
		sendError.value = 'Failed to send message.'
	} finally {
		sending.value = false
	}
}

function handleSend() {
	const body = newMessage.value.trim()
	if (!body || sending.value) return

	if (pendingSubject.value) {
		startConversation(body)
	} else {
		sendMessage(body)
	}
}

watch(() => props.conversationId, (newId) => {
	if (newId) {
		loadConversation()
	}
})

onMounted(() => {
	if (props.conversationId) {
		loadConversation()
	}
})
</script>

<style scoped>
.tutor-page {
	max-width: 1100px;
	margin: 2rem auto;
	padding: 0 1rem;
}

.tutor-header {
	margin-bottom: 1rem;
}

h1 {
	margin: 0;
}

.subtitle {
	margin: 0.35rem 0 0;
	color: #8b8ba6;
}

.chat-shell {
	display: grid;
	grid-template-columns: 260px 1fr;
	border: 1px solid #2f2f4a;
	border-radius: 12px;
	overflow: hidden;
	min-height: 560px;
}

.chat-main {
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	background: #101023;
	min-height: 560px;
}

.messages {
	padding: 1rem;
	display: flex;
	flex-direction: column;
	gap: 0.75rem;
	overflow-y: auto;
	flex: 1;
}

.message {
	max-width: min(75%, 620px);
	padding: 0.8rem 0.9rem;
	border-radius: 12px;
}

.message-tutor {
	align-self: flex-start;
	background: #e94560;
	color: #ffffff;
}

.message-user {
	align-self: flex-end;
	background: #ffffff;
	color: #0a0a23;
}

.author {
	margin: 0 0 0.3rem;
	font-size: 0.8rem;
	font-weight: 700;
	color: #e94560;
}

.message-tutor .author {
	color: #0a0a23;
}

.message p {
	margin: 0;
}

.markdown-body :first-child {
	margin-top: 0;
}

.markdown-body :last-child {
	margin-bottom: 0;
}

.markdown-body code {
	background: rgba(0, 0, 0, 0.15);
	padding: 0.15em 0.35em;
	border-radius: 4px;
	font-size: 0.9em;
}

.markdown-body pre {
	background: rgba(0, 0, 0, 0.2);
	padding: 0.75rem;
	border-radius: 6px;
	overflow-x: auto;
}

.markdown-body pre code {
	background: none;
	padding: 0;
}

.markdown-body ul,
.markdown-body ol {
	padding-left: 1.5rem;
}

.timestamp {
	display: block;
	margin-top: 0.35rem;
	font-size: 0.72rem;
	color: #6b6b8a;
}

.chat-input {
	border-top: 1px solid #2f2f4a;
	padding: 1rem;
}

.chat-input textarea {
	width: 100%;
	resize: vertical;
	min-height: 72px;
	border: 1px solid #3a3a58;
	background: #15152d;
	color: #f2f2f5;
	border-radius: 10px;
	padding: 0.75rem;
	font: inherit;
}

.actions {
	margin-top: 0.65rem;
	display: flex;
	justify-content: flex-end;
	align-items: center;
	gap: 0.8rem;
}

.actions button {
	border: none;
	border-radius: 8px;
	padding: 0.55rem 1rem;
	font-weight: 600;
	background: #e94560;
	color: #fff;
	cursor: pointer;
}

.actions button:disabled {
	background: #2f2f4a;
	color: #c8c8d6;
	cursor: not-allowed;
}

.send-error {
	color: #e94560;
	font-size: 0.84rem;
}

.status-state {
	text-align: center;
	color: #8b8ba6;
	padding: 2rem;
}

.error-state {
	color: #e94560;
}

.empty-prompt {
	text-align: center;
	color: #ffffff;
	font-size: 1.1rem;
	font-weight: 600;
	padding: 3rem 2rem;
}

.typing-indicator {
	align-self: flex-start;
	color: #8b8ba6;
	font-size: 0.85rem;
	padding: 0.4rem 0.8rem;
	font-style: italic;
}

@media (max-width: 900px) {
	.chat-shell {
		grid-template-columns: 1fr;
	}
}
</style>