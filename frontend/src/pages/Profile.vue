<template>
  <div class="profile-page">
    <h1>My Profile</h1>

    <p v-if="loading">Loading profile...</p>
    <p v-else-if="error" class="error">{{ error }}</p>

    <div v-else-if="profile" class="profile-card">
      <div class="field">
        <label>Name</label>
        <span>{{ profile.firstName }} {{ profile.lastName }}</span>
      </div>
      <div class="field">
        <label>Username</label>
        <span>{{ profile.username }}</span>
      </div>
      <div class="field">
        <label>Email</label>
        <span>{{ profile.email || '—' }}</span>
      </div>
      <div class="field">
        <label>Role</label>
        <span class="role-badge">{{ profile.role }}</span>
      </div>
      <div class="field">
        <label>Gender</label>
        <span>{{ profile.gender || '—' }}</span>
      </div>
      <div class="field">
        <label>Date of Birth</label>
        <span>{{ formattedDob }}</span>
      </div>
      <div v-if="profile.address" class="field">
        <label>Address</label>
        <span>
          {{ profile.address.street }}, {{ profile.address.city }},
          {{ profile.address.state }} {{ profile.address.zipCode }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import { userApi } from '@/api/userApi'
import type { UserProfile } from '@/api/userApi'

const authStore = useAuthStore()
const router = useRouter()

const profile = ref<UserProfile | null>(null)
const loading = ref(true)
const error = ref('')

const formattedDob = computed(() => {
  if (!profile.value?.dateOfBirth) return '—'
  return new Date(profile.value.dateOfBirth).toLocaleDateString()
})

onMounted(async () => {
  if (!authStore.user) {
    router.push('/login')
    return
  }
  try {
    const { data } = await userApi.getProfile(authStore.user.id)
    profile.value = data
  } catch {
    error.value = 'Failed to load profile'
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.profile-page {
  max-width: 600px;
  margin: 2rem auto;
}

.profile-card {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 1.5rem;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.field label {
  font-size: 0.8rem;
  font-weight: 600;
  color: #888;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.field span {
  font-size: 1rem;
  color: #222;
}

.role-badge {
  display: inline-block;
  padding: 0.15rem 0.5rem;
  border-radius: 4px;
  background-color: #e94560;
  color: #fff !important;
  font-size: 0.85rem !important;
  width: fit-content;
}

.error {
  color: #e94560;
}
</style>