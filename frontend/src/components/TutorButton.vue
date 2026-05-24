<template>
	<router-link :to="{ name: 'Tutor' }" class="tutor-fab" aria-label="Open Tutor">
		<span class="fab-icon">?</span>
		<span class="fab-label">Tutor</span>
	</router-link>
</template>

<style scoped>
.tutor-fab {
	position: fixed;
	bottom: 2rem;
	right: 2rem;
	display: flex;
	align-items: center;
	gap: 0;
	width: 52px;
	height: 52px;
	border-radius: 26px;
	background: #e94560;
	color: #fff;
	text-decoration: none;
	box-shadow: 0 4px 14px rgba(233, 69, 96, 0.45);
	overflow: hidden;
	transition: width 0.3s ease, gap 0.3s ease, box-shadow 0.2s;
	cursor: pointer;
	z-index: 1000;
}

.tutor-fab:hover {
	width: 130px;
	gap: 0.4rem;
	box-shadow: 0 6px 20px rgba(233, 69, 96, 0.55);
}

.fab-icon {
	display: flex;
	align-items: center;
	justify-content: center;
	min-width: 52px;
	height: 52px;
	font-size: 1.5rem;
	font-weight: 800;
	background: rgba(255, 255, 255, 0.25);
	border-radius: 50%;
}

.fab-label {
	font-weight: 700;
	font-size: 0.95rem;
	white-space: nowrap;
	opacity: 0;
	transition: opacity 0.2s ease 0.1s;
	padding-right: 0.8rem;
}

.tutor-fab:hover .fab-label {
	opacity: 1;
}
</style>
