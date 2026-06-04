<template>
  <div class="create-lesson-page">
    <h1>Create Lesson</h1>
    <LessonForm
      :form="form"
      submitLabel="Create Lesson"
      :loading="loading"
      :error="error"
      @submit="handleCreate"
    />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { lessonApi } from '@/api/lessonApi'
import LessonForm from '@/components/LessonForm.vue'

const props = defineProps<{
  courseId: string
}>()

const router = useRouter()

const form = reactive({
  title: '',
  description: '',
  minutes: 0,
  text: '',
  media: [] as string[],
  resources: [] as string[]
})

const error = ref('')
const loading = ref(false)

async function handleCreate() {
  loading.value = true
  error.value = ''
  try {
    const payload = {
      courseId: props.courseId,
      ...form,
      resources: form.resources.filter(r => r.trim() !== '')
    }
    await lessonApi.create(payload)
    router.push(`/courses/${props.courseId}`)
  } catch (e: any) {
    error.value = e.response?.data?.error || 'Failed to create lesson'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.create-lesson-page {
  max-width: 600px;
  margin: 2rem auto;
}
</style>
