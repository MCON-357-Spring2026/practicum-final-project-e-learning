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
      <div class="text-upload-row">
        <label class="upload-text-btn" for="textFile">Import from file</label>
        <input id="textFile" type="file" accept=".txt,.docx,.md,.html,.rtf,.csv" class="file-input" @change="onTextFileSelected" />
        <span v-if="textFileName" class="text-file-name">{{ textFileName }}</span>
      </div>
      <textarea id="text" v-model="form.text" rows="8" placeholder="Write the lesson content here..."></textarea>
    </div>

    <!-- Media upload -->
    <div class="form-group">
      <label>Media (images only)</label>
      <input type="file" accept="image/*" multiple @change="onMediaSelected" />
      <div v-if="mediaPreviews.length" class="preview-grid">
        <div v-for="(item, i) in mediaPreviews" :key="i" class="preview-item">
          <img :src="item.url" class="preview-media" />
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
import mammoth from 'mammoth'

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

const textFileName = ref('')
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

async function onTextFileSelected(e: Event) {
  const input = e.target as HTMLInputElement
  if (!input.files?.length) return
  const file = input.files[0]
  textFileName.value = file.name
  try {
    if (file.name.endsWith('.docx')) {
      const arrayBuffer = await file.arrayBuffer()
      const result = await mammoth.extractRawText({ arrayBuffer })
      props.form.text = result.value
    } else {
      props.form.text = await file.text()
    }
  } catch {
    props.form.text = 'Error reading file.'
  }
  input.value = ''
}

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

.file-input {
  display: none;
}

.text-upload-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.25rem;
}

.upload-text-btn {
  padding: 0.4rem 0.75rem;
  background-color: #e94560;
  color: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  white-space: nowrap;
}

.upload-text-btn:hover {
  background-color: #d63350;
}

.text-file-name {
  font-size: 0.85rem;
  color: #666;
}

.error {
  color: #e94560;
}
</style>
