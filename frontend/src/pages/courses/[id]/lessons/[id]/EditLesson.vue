<template>
  <div class="edit-lesson-page">
    <h1>Edit Lesson</h1>
    <p v-if="pageLoading">Loading lesson...</p>
    <p v-else-if="pageError" class="error">{{ pageError }}</p>
    <LessonForm
      v-else
      :form="form"
      submitLabel="Save Changes"
      :loading="saving"
      :error="saveError"
      @submit="handleSave"
    />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { lessonApi } from '@/api/lessonApi'
import LessonForm from '@/components/LessonForm.vue'

const props = defineProps<{
  courseId: string
  lessonId: string
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

const pageLoading = ref(true)
const pageError = ref('')
const saving = ref(false)
const saveError = ref('')

onMounted(async () => {
  try {
    const { data } = await lessonApi.getById(props.lessonId)
    form.title = data.title
    form.description = data.description
    form.minutes = data.minutes
    form.text = data.text ?? ''
    form.media = data.media ?? []
    form.resources = data.resources ?? []
  } catch {
    pageError.value = 'Failed to load lesson'
  } finally {
    pageLoading.value = false
  }
})

async function handleSave() {
  saving.value = true
  saveError.value = ''
  try {
    await lessonApi.update(props.lessonId, {
      title: form.title,
      description: form.description,
      minutes: form.minutes,
      text: form.text,
      media: form.media,
      resources: form.resources.filter(r => r.trim() !== '')
    })
    router.push(`/courses/${props.courseId}/lessons/${props.lessonId}`)
  } catch (e: any) {
    saveError.value = e.response?.data?.error || 'Failed to save changes'
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.edit-lesson-page {
  max-width: 600px;
  margin: 2rem auto;
}

.error {
  color: #e94560;
}
</style>
