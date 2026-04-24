<template>
  <div class="courses-page">
    <h1>Courses</h1>

    <div class="filters">
      <select v-model="filterDepartment">
        <option value="">All Departments</option>
        <option v-for="dept in departments" :key="dept" :value="dept">{{ dept }}</option>
      </select>

      <select v-model="filterTeacher">
        <option value="">All Teachers</option>
        <option v-for="(name, id) in teacherNames" :key="id" :value="id">{{ name }}</option>
      </select>

      <select v-model="filterCredits">
        <option value="">Any Credits</option>
        <option v-for="c in creditOptions" :key="c" :value="c">{{ c }} credit{{ c !== 1 ? 's' : '' }}</option>
      </select>

      <label v-if="authStore.isAuthenticated" class="toggle-label">
        <input type="checkbox" v-model="hideEnrolled" />
        Hide enrolled
      </label>
    </div>

    <p v-if="courseStore.loading">Loading courses...</p>
    <p v-else-if="courseStore.error" class="error">{{ courseStore.error }}</p>
    <div v-else-if="filteredCourses.length" class="course-grid">
      <CourseCard
        v-for="course in filteredCourses"
        :key="course.id"
        :course="course"
        :enrolled="enrolledCourseIds.has(course.id)"
      />
    </div>
    <p v-else>No courses match your filters.</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useCourseStore } from '@/store/course'
import { useAuthStore } from '@/store/auth'
import { teacherApi } from '@/api/teacherApi'
import axiosClient from '@/api/axiosClient'
import CourseCard from '@/components/CourseCard.vue'

const courseStore = useCourseStore()
const authStore = useAuthStore()

const filterDepartment = ref('')
const filterTeacher = ref('')
const filterCredits = ref<number | ''>('')
const hideEnrolled = ref(false)

const teacherNames = ref<Record<string, string>>({})
const enrolledCourseIds = ref<Set<string>>(new Set())

const departments = computed(() => {
  const depts = new Set(courseStore.courses.map(c => c.department))
  return [...depts].sort()
})

const creditOptions = computed(() => {
  const vals = new Set(courseStore.courses.map(c => c.credits))
  return [...vals].sort((a, b) => a - b)
})

const filteredCourses = computed(() => {
  return courseStore.courses.filter(course => {
    if (filterDepartment.value && course.department !== filterDepartment.value) return false
    if (filterTeacher.value && course.instructorId !== filterTeacher.value) return false
    if (filterCredits.value !== '' && course.credits !== filterCredits.value) return false
    if (hideEnrolled.value && enrolledCourseIds.value.has(course.id)) return false
    return true
  })
})

onMounted(async () => {
  await courseStore.fetchAll()

  // Load teacher names for filter dropdown
  try {
    const res = await teacherApi.getAll()
    const map: Record<string, string> = {}
    for (const t of res.data) {
      map[t.id] = `${t.firstName} ${t.lastName}`
    }
    teacherNames.value = map
  } catch {
    // non-critical – filter just won't have teacher names
  }

  // Load enrolled course IDs if logged in
  if (authStore.isAuthenticated && authStore.user) {
    try {
      const res = await axiosClient.get<{ courseId: string }[]>(
        `/enrollments/student/${authStore.user.id}`
      )
      enrolledCourseIds.value = new Set(res.data.map(e => e.courseId))
    } catch {
      // non-critical
    }
  }
})
</script>

<style scoped>
.courses-page h1 {
  margin-bottom: 1rem;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  align-items: center;
  margin-bottom: 1.5rem;
}

.filters select {
  padding: 0.4rem 0.6rem;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 0.9rem;
  background: #fff;
}

.toggle-label {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  font-size: 0.9rem;
  cursor: pointer;
  user-select: none;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.error {
  color: #e94560;
}
</style>
