<template>
  <div class="sidebar" :class="{ collapsed }">
    <button class="toggle-btn" @click="collapsed = !collapsed" :title="collapsed ? 'Expand' : 'Collapse'">
      <span v-if="collapsed">&#9654;</span>
      <span v-else>&#9664;</span>
    </button>

    <nav v-show="!collapsed" class="sidebar-nav">
      <router-link to="/messages" class="sidebar-link" :class="{ active: currentRoute === '/messages' }">
        <span class="icon">&#128229;</span>
        <span>Inbox</span>
      </router-link>
      <router-link to="/messages/sent" class="sidebar-link" :class="{ active: currentRoute === '/messages/sent' }">
        <span class="icon">&#128228;</span>
        <span>Sent</span>
      </router-link>
      <router-link to="/messages/compose" class="sidebar-link compose" :class="{ active: currentRoute === '/messages/compose' }">
        <span class="icon">&#9998;</span>
        <span>Compose</span>
      </router-link>
      <router-link v-if="isTeacherOrAdmin" to="/messages/blasts" class="sidebar-link" :class="{ active: currentRoute === '/messages/blasts' }">
        <span class="icon">&#128227;</span>
        <span>Blasts</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/store/auth'

const route = useRoute()
const authStore = useAuthStore()
const collapsed = ref(false)

const currentRoute = computed(() => route.path)
const isTeacherOrAdmin = computed(() => {
  const role = authStore.user?.role
  return role === 'TEACHER' || role === 'ADMIN'
})
</script>

<style scoped>
.sidebar {
  width: 200px;
  min-height: calc(100vh - 60px);
  background: #f8f8fa;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  transition: width 0.2s ease;
  flex-shrink: 0;
}

.sidebar.collapsed {
  width: 48px;
}

.toggle-btn {
  background: none;
  border: none;
  padding: 0.75rem;
  cursor: pointer;
  font-size: 0.85rem;
  color: #555;
  text-align: center;
  border-bottom: 1px solid #e0e0e0;
}

.toggle-btn:hover {
  background: #eee;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  padding: 0.5rem 0;
}

.sidebar-link {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  padding: 0.65rem 1rem;
  color: #333;
  text-decoration: none;
  font-size: 0.95rem;
  border-radius: 0 20px 20px 0;
  margin-right: 0.5rem;
  transition: background 0.15s;
}

.sidebar-link:hover {
  background: #eaeaea;
}

.sidebar-link.active {
  background: #fce4ec;
  color: #e94560;
  font-weight: 500;
}

.sidebar-link.compose {
  margin-top: 0.25rem;
}

.icon {
  font-size: 1.1rem;
  width: 1.3rem;
  text-align: center;
}
</style>
