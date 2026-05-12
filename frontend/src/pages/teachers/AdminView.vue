<template>
  <div class="admin-teachers-page">
    <h1>Manage Teachers</h1>

    <div class="tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
      </button>
    </div>

    <p v-if="loading">Loading...</p>
    <p v-else-if="error" class="error">{{ error }}</p>

    <!-- All tab -->
    <template v-else-if="activeTab === 'all'">
      <section v-if="pending.length" class="group">
        <h2>Pending</h2>
        <div class="card-list">
          <PendingTeacherCard
            v-for="t in pending"
            :key="t.id"
            :teacher="t"
            @approve="handleApprove"
          />
        </div>
      </section>
      <section v-if="teachers.length" class="group">
        <h2>Teachers</h2>
        <div class="card-list">
          <TeacherCard
            v-for="t in teachers"
            :key="t.id"
            :teacher="t"
            showPromote
            @promote="handlePromoteAdmin"
          />
        </div>
      </section>
      <section v-if="admins.length" class="group">
        <h2>Admins</h2>
        <div class="card-list">
          <TeacherCard v-for="t in admins" :key="t.id" :teacher="t" />
        </div>
      </section>
      <p v-if="!pending.length && !teachers.length && !admins.length">No users found.</p>
    </template>

    <!-- Pending tab -->
    <template v-else-if="activeTab === 'pending'">
      <p v-if="!pending.length">No pending teachers.</p>
      <div v-else class="card-list">
        <PendingTeacherCard
          v-for="t in pending"
          :key="t.id"
          :teacher="t"
          @approve="handleApprove"
        />
      </div>
    </template>

    <!-- Teachers tab -->
    <template v-else-if="activeTab === 'teachers'">
      <p v-if="!teachers.length">No teachers.</p>
      <div v-else class="card-list">
        <TeacherCard
          v-for="t in teachers"
          :key="t.id"
          :teacher="t"
          showPromote
          @promote="handlePromoteAdmin"
        />
      </div>
    </template>

    <!-- Admin tab -->
    <template v-else-if="activeTab === 'admin'">
      <p v-if="!admins.length">No admins.</p>
      <div v-else class="card-list">
        <TeacherCard v-for="t in admins" :key="t.id" :teacher="t" />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { teacherApi, type LimitedTeacher } from '@/api/teacherApi'
import TeacherCard from '@/components/TeacherCard.vue'
import PendingTeacherCard from '@/components/PendingTeacherCard.vue'

const tabs = [
  { key: 'all', label: 'All' },
  { key: 'pending', label: 'Pending' },
  { key: 'teachers', label: 'Teachers' },
  { key: 'admin', label: 'Admin' }
] as const

type TabKey = (typeof tabs)[number]['key']

const activeTab = ref<TabKey>('all')
const pending = ref<LimitedTeacher[]>([])
const teachers = ref<LimitedTeacher[]>([])
const admins = ref<LimitedTeacher[]>([])
const loading = ref(true)
const error = ref('')

async function fetchAll() {
  loading.value = true
  error.value = ''
  try {
    const [p, t, a] = await Promise.all([
      teacherApi.getPending(),
      teacherApi.getAll(),
      teacherApi.getAdmins()
    ])
    pending.value = p.data
    teachers.value = t.data
    admins.value = a.data
  } catch {
    error.value = 'Failed to load data.'
  } finally {
    loading.value = false
  }
}

async function handleApprove(id: string) {
  try {
    await teacherApi.promoteToTeacher(id)
    await fetchAll()
  } catch {
    error.value = 'Failed to approve teacher.'
  }
}

async function handlePromoteAdmin(id: string) {
  try {
    await teacherApi.promoteToAdmin(id)
    await fetchAll()
  } catch {
    error.value = 'Failed to promote to admin.'
  }
}

onMounted(fetchAll)
</script>

<style scoped>
.admin-teachers-page {
  max-width: 900px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.tabs {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
  border-bottom: 2px solid #ddd;
  padding-bottom: 0.5rem;
}

.tab {
  padding: 0.5rem 1.2rem;
  border: none;
  background: none;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  border-radius: 6px 6px 0 0;
  color: #888;
  transition: color 0.15s, background-color 0.15s;
}

.tab:hover {
  color: #e94560;
}

.tab.active {
  color: #e94560;
  border-bottom: 2px solid #e94560;
  margin-bottom: -2px;
}

.group {
  margin-bottom: 2rem;
}

.group h2 {
  margin-bottom: 0.75rem;
  font-size: 1.1rem;
}

.card-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.error {
  color: #e94560;
}
</style>
