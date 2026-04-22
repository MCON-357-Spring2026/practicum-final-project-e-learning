<template>
  <form @submit.prevent="$emit('submit')" class="lesson-form">
    <div class="form-group">
      <label for="title">Title</label>
      <input id="title" v-model="form.title" type="text" required />
    </div>
    <div class="form-group">
      <label for="description">Description</label>
      <textarea id="description" v-model="form.description" rows="3"></textarea>
    </div>
    <div class="form-group">
      <label for="minutes">Duration (minutes)</label>
      <input id="minutes" v-model.number="form.minutes" type="number" required />
    </div>
    <div class="form-group">
      <label for="text">Lesson Text</label>
      <textarea id="text" v-model="form.text" rows="8" placeholder="Write the lesson content here..."></textarea>
    </div>

    <!-- Media upload -->
    <div class="form-group">
      <label>Media (images &amp; videos)</label>
      <input type="file" accept="image/*,video/*" multiple @change="onMediaSelected" />
      <div v-if="mediaPreviews.length" class="preview-grid">
        <div v-for="(item, i) in mediaPreviews" :key="i" class="preview-item">
          <video v-if="item.type === 'video'" :src="item.url" controls class="preview-media" />
          <img v-else :src="item.url" class="preview-media" />
          <button type="button" class="remove-btn media-remove" @click="removeMedia(i)">&times;</button>
        </div>
      </div>
    </div>

    <!-- Resources -->
    <div class="form-group">
      <label>Additional Resources</label>
      <div v-for="(_, i) in form.resources" :key="i" class="resource-row">
        <input v-model="form.resources[i]" type="url" placeholder="https://..." />
        <button type="button" class="remove-btn resource-remove" @click="form.resources.splice(i, 1)">&times;</button>
      </div>
      <button type="button" class="add-btn" @click="form.resources.push('')">+ Add Resource</button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>
    <button type="submit" :disabled="loading">{{ loading ? 'Saving...' : submitLabel }}</button>
  </form>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

export interface LessonFormData {
  title: string
  description: string
  minutes: number
  text: string
  media: string[]
  resources: string[]
}

const props = defineProps<{
  form: LessonFormData
  submitLabel: string
  loading: boolean
  error: string
}>()

defineEmits<{
  (e: 'submit'): void
}>()

const videoExtensions = ['.mp4', '.webm', '.ogg', '.mov']

function isVideo(url: string): boolean {
  const lower = url.toLowerCase()
  return lower.startsWith('data:video') || videoExtensions.some(ext => lower.includes(ext))
}

const mediaPreviews = ref<{ url: string; type: 'image' | 'video' }[]>([])

// Build previews from existing media on mount (for edit mode)
onMounted(() => {
  if (props.form.media && props.form.media.length) {
    mediaPreviews.value = props.form.media.map(url => ({
      url,
      type: isVideo(url) ? 'video' : 'image'
    }))
  }
})

function onMediaSelected(e: Event) {
  const input = e.target as HTMLInputElement
  if (!input.files) return
  for (const file of Array.from(input.files)) {
    const reader = new FileReader()
    const type = file.type.startsWith('video') ? 'video' : 'image'
    reader.onload = () => {
      const dataUrl = reader.result as string
      props.form.media.push(dataUrl)
      mediaPreviews.value.push({ url: dataUrl, type })
    }
    reader.readAsDataURL(file)
  }
  input.value = ''
}

function removeMedia(index: number) {
  props.form.media.splice(index, 1)
  mediaPreviews.value.splice(index, 1)
}
</script>

<style scoped>
.lesson-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.form-group input,
.form-group textarea {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
}

.preview-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.preview-item {
  position: relative;
  width: 140px;
}

.preview-media {
  width: 100%;
  height: 100px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.remove-btn {
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  line-height: 1;
  padding: 0;
}

.media-remove {
  position: absolute;
  top: 2px;
  right: 2px;
  width: 22px;
  height: 22px;
  font-size: 14px;
}

.resource-row {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.resource-row input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
}

.resource-remove {
  position: static;
  width: 28px;
  height: 28px;
  font-size: 16px;
  background: #e94560;
  flex-shrink: 0;
}

.add-btn {
  align-self: flex-start;
  background: none;
  color: #e94560;
  border: 1px dashed #e94560;
  border-radius: 4px;
  padding: 0.4rem 0.75rem;
  cursor: pointer;
  font-size: 0.9rem;
}

button[type="submit"] {
  padding: 0.6rem;
  background-color: #e94560;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
}

button[type="submit"]:disabled {
  opacity: 0.6;
}

.error {
  color: #e94560;
}
</style>
