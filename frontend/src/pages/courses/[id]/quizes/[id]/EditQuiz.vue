<template>
  <div class="edit-quiz-page">
    <h1>Edit Quiz</h1>
    <p v-if="pageLoading">Loading quiz...</p>
    <p v-else-if="pageError" class="error">{{ pageError }}</p>
    <QuizForm
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
import { quizApi } from '@/api/quizApi'
import QuizForm from '@/components/QuizForm.vue'
import type { QuestionForm } from '@/components/QuizForm.vue'

const props = defineProps<{
  courseId: string
  quizId: string
}>()

const router = useRouter()

const form = reactive({
  title: '',
  questions: [] as QuestionForm[]
})

const pageLoading = ref(true)
const pageError = ref('')
const saving = ref(false)
const saveError = ref('')

onMounted(async () => {
  try {
    const { data } = await quizApi.getForEdit(props.quizId)
    form.title = data.title
    form.questions = data.questions.map((q: any) => ({
      questionText: q.questionText,
      options: [...q.options],
      correctOptionIndex: q.correctOptionIndex
    }))
  } catch {
    pageError.value = 'Failed to load quiz'
  } finally {
    pageLoading.value = false
  }
})

async function handleSave() {
  saving.value = true
  saveError.value = ''
  try {
    await quizApi.update(props.quizId, {
      courseId: props.courseId,
      title: form.title,
      questions: form.questions
    })
    router.push(`/courses/${props.courseId}/quiz/${props.quizId}`)
  } catch (e: any) {
    saveError.value = e.response?.data?.error || 'Failed to save changes'
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.edit-quiz-page {
  max-width: 700px;
  margin: 2rem auto;
}

.error {
  color: #e94560;
}
</style>
