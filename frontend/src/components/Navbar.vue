<template>
  <nav class="navbar">
    <router-link to="/courses" class="navbar-brand">
      <img src="@/assets/logo.png" alt="Logo" class="navbar-logo" />
      E-Learning
    </router-link>
    <div class="navbar-links">
      <router-link to="/courses">Courses</router-link>
      <template v-if="authStore.isAuthenticated">
        <router-link to="/teachers">Teachers</router-link>
        <router-link :to="dashboardRoute">Dashboard</router-link>
        <router-link to="/messages">Messaging</router-link>
        <router-link to="/profile">Profile</router-link>
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
import { computed } from 'vue'
import { useAuthStore } from '@/store/auth'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const dashboardRoute = computed(() => {
  return authStore.user?.role === 'TEACHER' || authStore.user?.role === 'ADMIN' ? '/instructor' : '/dashboard'
})

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
  position: sticky;
  top: 0;
  z-index: 1000;
  min-height: 86px;
  padding: 1.1rem 2rem;
  background: linear-gradient(90deg, #101023 0%, #1d1d43 58%, #343474 100%);
  color: #fff;
  border-bottom: 1px solid #2f2f4a;
  box-shadow: 0 8px 20px rgba(26, 26, 46, 0.18);
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 0.7rem;
  font-family: 'Lucida Calligraphy', 'Lucida Handwriting', 'Segoe Script', cursive;
  font-size: 1.65rem;
  font-weight: 700;
  letter-spacing: 0.02em;
  color: #e94560;
  text-decoration: none;
  text-shadow: 0 1px 0 rgba(0, 0, 0, 0.15);
}

.navbar-logo {
  height: 54px;
  width: 54px;
  object-fit: contain;
}

.navbar-links {
  display: flex;
  gap: 0.8rem;
  align-items: center;
}

.navbar-links a,
.btn-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 2.4rem;
  padding: 0 0.9rem;
  border-radius: 999px;
  color: #eee;
  font-family: 'Inter', 'Segoe UI', Roboto, Arial, sans-serif;
  font-size: 0.95rem;
  font-weight: 600;
  letter-spacing: 0.01em;
  text-decoration: none;
  transition: background-color 0.18s ease, color 0.18s ease;
}

.navbar-links a:hover,
.navbar-links a.router-link-active,
.btn-link:hover {
  background-color: #2f2f4a;
  color: #e94560;
}

.btn-link {
  background: none;
  border: none;
  cursor: pointer;
}
</style>
