<template>
	<section class="tutor-page">
		<header class="tutor-header">
			<div>
				<h1>Tutor Chat</h1>
				<p class="subtitle">Ask questions and get guided help.</p>
			</div>
			<span class="todo-badge">TODO: connect to chat backend</span>
		</header>

		<div class="chat-shell">
			<aside class="chat-sidebar">
				<h2>Conversations</h2>
				<ul>
					<li class="active">Current Course Tutor</li>
					<li>Math Tutor</li>
					<li>Science Tutor</li>
				</ul>
				<p class="todo-text">TODO: load user conversations</p>
			</aside>

			<main class="chat-main">
				<div class="messages">
					<article class="message message-tutor">
						<p class="author">Tutor</p>
						<p>Hi! What topic do you want to review today?</p>
					</article>

					<article class="message message-user">
						<p class="author">You</p>
						<p>Can you explain recursion with a simple example?</p>
					</article>

					<article class="message message-tutor">
						<p class="author">Tutor</p>
						<p>
							Sure — think of recursion as a function calling itself with a smaller version of the same
							problem until it reaches a base case.
						</p>
					</article>
				</div>

				<form class="chat-input" @submit.prevent>
					<textarea
						rows="3"
						placeholder="Type your message..."
						aria-label="Type your message"
					></textarea>
					<div class="actions">
						<span class="todo-text">TODO: wire send message action</span>
						<button type="submit" disabled>Send</button>
					</div>
				</form>
			</main>
		</div>
	</section>
</template>

<script setup lang="ts"></script>

<style scoped>
.tutor-page {
	max-width: 1100px;
	margin: 2rem auto;
	padding: 0 1rem;
}

.tutor-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	gap: 1rem;
	margin-bottom: 1rem;
}

h1 {
	margin: 0;
}

.subtitle {
	margin: 0.35rem 0 0;
	color: #8b8ba6;
}

.todo-badge {
	font-size: 0.82rem;
	font-weight: 700;
	color: #e94560;
	background: rgba(233, 69, 96, 0.12);
	border: 1px solid rgba(233, 69, 96, 0.35);
	border-radius: 999px;
	padding: 0.35rem 0.7rem;
	white-space: nowrap;
}

.chat-shell {
	display: grid;
	grid-template-columns: 260px 1fr;
	border: 1px solid #2f2f4a;
	border-radius: 12px;
	overflow: hidden;
	min-height: 560px;
}

.chat-sidebar {
	padding: 1rem;
	border-right: 1px solid #2f2f4a;
	background: #17172f;
}

.chat-sidebar h2 {
	margin: 0 0 0.8rem;
	font-size: 1rem;
}

.chat-sidebar ul {
	list-style: none;
	margin: 0;
	padding: 0;
	display: flex;
	flex-direction: column;
	gap: 0.45rem;
}

.chat-sidebar li {
	padding: 0.65rem 0.8rem;
	border-radius: 8px;
	background: #1f1f3d;
	color: #f2f2f5;
}

.chat-sidebar li.active {
	border: 1px solid #e94560;
}

.chat-main {
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	background: #101023;
}

.messages {
	padding: 1rem;
	display: flex;
	flex-direction: column;
	gap: 0.75rem;
}

.message {
	max-width: min(75%, 620px);
	padding: 0.8rem 0.9rem;
	border-radius: 12px;
}

.message-tutor {
	align-self: flex-start;
	background: #242447;
}

.message-user {
	align-self: flex-end;
	background: #2f2f4a;
}

.author {
	margin: 0 0 0.3rem;
	font-size: 0.8rem;
	font-weight: 700;
	color: #e94560;
}

.message p {
	margin: 0;
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
	justify-content: space-between;
	align-items: center;
	gap: 0.8rem;
}

.todo-text {
	color: #8b8ba6;
	font-size: 0.84rem;
}

.actions button {
	border: none;
	border-radius: 8px;
	padding: 0.55rem 1rem;
	font-weight: 600;
	background: #2f2f4a;
	color: #c8c8d6;
	cursor: not-allowed;
}

@media (max-width: 900px) {
	.chat-shell {
		grid-template-columns: 1fr;
	}

	.chat-sidebar {
		border-right: none;
		border-bottom: 1px solid #2f2f4a;
	}
}
</style>