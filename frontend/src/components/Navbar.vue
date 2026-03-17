<template>
  <nav class="navbar">
    <router-link to="/courses" class="navbar-brand">E-Learning</router-link>
    <div class="navbar-links">
      <router-link to="/courses">Courses</router-link>
      <template v-if="authStore.isAuthenticated">
        <router-link to="/instructor">Dashboard</router-link>
        <button @click="handleLogout" class="btn-link">Logout</button>
      </template>
      <template v-else>
        <router-link to="/login">Login</router-link>
        <router-link to="/register">Register</router-link>
      </template>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { useAuthStore } from '../store/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1.5rem;
  background-color: #1a1a2e;
  color: #fff;
}

.navbar-brand {
  font-size: 1.25rem;
  font-weight: bold;
  color: #e94560;
  text-decoration: none;
}

.navbar-links {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.navbar-links a {
  color: #eee;
  text-decoration: none;
}

.navbar-links a:hover,
.navbar-links a.router-link-active {
  color: #e94560;
}

.btn-link {
  background: none;
  border: none;
  color: #eee;
  cursor: pointer;
  font-size: 1rem;
}

.btn-link:hover {
  color: #e94560;
}
</style>
