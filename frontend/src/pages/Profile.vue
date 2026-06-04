<template>
  <div class="profile-page">
    <h1>My Profile</h1>

    <p v-if="loading" class="status-text">Loading profile...</p>
    <p v-else-if="error" class="error status-text">{{ error }}</p>

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
      <div v-if="profile.address" class="field full-width">
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
  max-width: 760px;
  margin: 2rem auto;
  padding: 0 0.75rem;
}

.profile-page h1 {
  margin-bottom: 1rem;
}

.profile-card {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0.9rem;
  border: 1px solid #e3e6ef;
  border-radius: 14px;
  padding: 1.25rem;
  background: #fff;
  box-shadow: 0 10px 28px rgba(22, 30, 55, 0.08);
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  padding: 0.75rem 0.85rem;
  border: 1px solid #edf0f6;
  border-radius: 10px;
  background-color: #fafbff;
}

.field label {
  font-size: 0.8rem;
  font-weight: 600;
  color: #6b7386;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.field span {
  font-size: 1.02rem;
  color: #1e2433;
  line-height: 1.4;
}

.field.full-width {
  grid-column: 1 / -1;
}

.role-badge {
  display: inline-block;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
  background-color: #e94560;
  color: #fff !important;
  font-size: 0.85rem !important;
  width: fit-content;
}

.status-text {
  background: #fff;
  border: 1px solid #e3e6ef;
  border-radius: 10px;
  padding: 0.75rem 0.9rem;
  color: #4b556b;
}

.error {
  color: #e94560;
}

@media (max-width: 700px) {
  .profile-card {
    grid-template-columns: 1fr;
  }

  .field.full-width {
    grid-column: auto;
  }
}
</style>