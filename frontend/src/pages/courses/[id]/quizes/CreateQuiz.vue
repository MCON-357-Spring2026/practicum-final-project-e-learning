<template>
  <div class="create-quiz-page">
    <h1>Create Quiz</h1>
    <QuizForm
      :form="form"
      submitLabel="Create Quiz"
      :loading="loading"
      :error="error"
      @submit="handleCreate"
    />
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { quizApi } from '@/api/quizApi'
import QuizForm from '@/components/QuizForm.vue'
import type { QuestionForm } from '@/components/QuizForm.vue'

const props = defineProps<{
  courseId: string
}>()

const router = useRouter()

const form = reactive({
  title: '',
  questions: [] as QuestionForm[]
})

const error = ref('')
const loading = ref(false)

async function handleCreate() {
  loading.value = true
  error.value = ''
  try {
    await quizApi.create({
      courseId: props.courseId,
      title: form.title,
      questions: form.questions
    })
    router.push(`/courses/${props.courseId}`)
  } catch (e: any) {
    error.value = e.response?.data?.error || 'Failed to create quiz'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.create-quiz-page {
  max-width: 700px;
  margin: 2rem auto;
}
</style>
