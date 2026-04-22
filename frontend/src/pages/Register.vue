<template>
  <div class="register-page">
    <h1>Create an Account</h1>
    <form @submit.prevent="handleRegister" class="register-form">
      <!-- Left side: Account info -->
      <div class="form-left">
        <h2>Account</h2>

        <div class="form-group">
          <label for="username">Username <span class="required">*</span></label>
          <input id="username" v-model="form.username" type="text" required autocomplete="username" />
        </div>

        <div class="form-group">
          <label for="password">Password <span class="required">*</span></label>
          <input id="password" v-model="form.password" type="password" required autocomplete="new-password" />
          <ul class="password-rules" :class="{ valid: passwordValid }">
            <li :class="{ met: rules.length }">At least 8 characters</li>
            <li :class="{ met: rules.uppercase }">One uppercase letter</li>
            <li :class="{ met: rules.lowercase }">One lowercase letter</li>
            <li :class="{ met: rules.digit }">One digit</li>
            <li :class="{ met: rules.special }">One special character</li>
          </ul>
        </div>

        <div class="form-group">
          <label for="confirmPassword">Confirm Password <span class="required">*</span></label>
          <input id="confirmPassword" v-model="confirmPassword" type="password" required autocomplete="new-password" />
          <p v-if="confirmPassword && form.password !== confirmPassword" class="field-error">Passwords do not match</p>
        </div>

        <div class="form-group">
          <label>Account Type <span class="required">*</span></label>
          <div class="radio-group">
            <label class="radio-label">
              <input type="radio" v-model="form.accountType" value="student" />
              Student
            </label>
            <label class="radio-label">
              <input type="radio" v-model="form.accountType" value="teacher" />
              Teacher
            </label>
          </div>
        </div>

      </div>

      <!-- Divider -->
      <div class="divider"></div>

      <!-- Right side: Personal info -->
      <div class="form-right">
        <h2>Personal Information</h2>

        <div class="form-group">
          <label for="firstName">First Name <span class="required">*</span></label>
          <input id="firstName" v-model="form.firstName" type="text" required />
        </div>

        <div class="form-group">
          <label for="lastName">Last Name <span class="required">*</span></label>
          <input id="lastName" v-model="form.lastName" type="text" required />
        </div>

        <div class="form-group">
          <label for="email">Email</label>
          <input id="email" v-model="form.email" type="email" />
        </div>

        <div class="form-group">
          <label for="dob">Date of Birth</label>
          <input id="dob" v-model="dobString" type="date" />
        </div>

        <div class="form-group">
          <label for="gender">Gender</label>
          <select id="gender" v-model="form.gender">
            <option value="">-- Select --</option>
            <option value="MALE">Male</option>
            <option value="FEMALE">Female</option>
            <option value="PREFER_NOT_TO_SAY">Prefer not to say</option>
          </select>
        </div>

        <fieldset class="address-fieldset">
          <legend>Address</legend>
          <div class="form-group">
            <label for="street">Street</label>
            <input id="street" v-model="form.address.street" type="text" />
          </div>
          <div class="address-row">
            <div class="form-group">
              <label for="city">City</label>
              <input id="city" v-model="form.address.city" type="text" />
            </div>
            <div class="form-group">
              <label for="state">State</label>
              <input id="state" v-model="form.address.state" type="text" maxlength="2" />
            </div>
            <div class="form-group">
              <label for="zip">Zip</label>
              <input id="zip" v-model="form.address.zipCode" type="text" maxlength="10" />
            </div>
          </div>
        </fieldset>
      </div>

      <!-- Full-width bottom -->
      <div class="form-bottom">
        <p v-if="error" class="error">{{ error }}</p>
        <button type="submit" :disabled="loading || !canSubmit">{{ loading ? 'Registering...' : 'Register' }}</button>
        <p class="alt-link">Already have an account? <router-link to="/login">Login</router-link></p>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/auth'
import type { RegisterPayload } from '@/api/authApi'

const authStore = useAuthStore()
const router = useRouter()

const form = reactive({
  username: '',
  password: '',
  firstName: '',
  lastName: '',
  email: '',
  gender: '',
  accountType: 'student' as 'student' | 'teacher',
  address: {
    street: '',
    city: '',
    state: '',
    zipCode: ''
  }
})

const confirmPassword = ref('')
const dobString = ref('')
const error = ref('')
const loading = ref(false)

const rules = computed(() => ({
  length: form.password.length >= 8,
  uppercase: /[A-Z]/.test(form.password),
  lowercase: /[a-z]/.test(form.password),
  digit: /\d/.test(form.password),
  special: /[^A-Za-z0-9]/.test(form.password)
}))

const passwordValid = computed(() =>
  rules.value.length && rules.value.uppercase && rules.value.lowercase && rules.value.digit && rules.value.special
)

const canSubmit = computed(() =>
  form.username.trim() !== '' &&
  form.firstName.trim() !== '' &&
  form.lastName.trim() !== '' &&
  passwordValid.value &&
  form.password === confirmPassword.value
)

async function handleRegister() {
  loading.value = true
  error.value = ''
  try {
    const hasAddress = form.address.street || form.address.city || form.address.state || form.address.zipCode

    const payload: RegisterPayload = {
      username: form.username,
      password: form.password,
      firstName: form.firstName,
      lastName: form.lastName,
      dateOfBirth: dobString.value ? new Date(dobString.value).toISOString() : null,
      gender: form.gender || null,
      address: hasAddress ? { ...form.address } : null,
      email: form.email || null,
      accountType: form.accountType
    }

    await authStore.register(payload)
    router.push('/courses')
  } catch (e: any) {
    error.value = e.response?.data?.error || e.response?.data?.message || 'Registration failed'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  max-width: 820px;
  margin: 2rem auto;
  padding: 0 1rem;
}

.register-page h1 {
  text-align: center;
  margin-bottom: 1.5rem;
}

.register-form {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 2rem;
}

.form-left,
.form-right {
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
}

.form-left h2,
.form-right h2 {
  font-size: 1.1rem;
  margin-bottom: 0.25rem;
  color: #555;
}

.divider {
  width: 1px;
  background-color: #ddd;
}

.form-bottom {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.form-group label {
  font-size: 0.85rem;
  font-weight: 600;
}

.required {
  color: #e94560;
}

.form-group input,
.form-group select {
  padding: 0.45rem 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 0.95rem;
}

.radio-group {
  display: flex;
  gap: 1.5rem;
  padding-top: 0.2rem;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  font-weight: normal;
  cursor: pointer;
}

.password-rules {
  list-style: none;
  font-size: 0.78rem;
  color: #999;
  padding: 0;
  margin: 0.2rem 0 0 0;
}

.password-rules li::before {
  content: '✕ ';
  color: #ccc;
}

.password-rules li.met::before {
  content: '✓ ';
  color: #2ecc71;
}

.password-rules li.met {
  color: #2ecc71;
}

.field-error {
  color: #e94560;
  font-size: 0.8rem;
  margin: 0;
}

.address-fieldset {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 0.75rem;
}

.address-fieldset legend {
  font-size: 0.85rem;
  font-weight: 600;
  padding: 0 0.3rem;
}

.address-row {
  display: grid;
  grid-template-columns: 1fr 60px 80px;
  gap: 0.5rem;
  margin-top: 0.5rem;
}

button {
  padding: 0.6rem 2rem;
  background-color: #e94560;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error {
  color: #e94560;
  text-align: center;
}

.alt-link {
  text-align: center;
}

@media (max-width: 640px) {
  .register-form {
    grid-template-columns: 1fr;
  }
  .divider {
    width: 100%;
    height: 1px;
  }
}
</style>
