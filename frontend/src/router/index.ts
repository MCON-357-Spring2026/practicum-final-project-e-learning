import { createRouter, createWebHistory } from 'vue-router'

import Courses from '../pages/Courses.vue'
import CourseDetails from '../pages/CourseDetails.vue'
import Login from '../pages/Login.vue'
import Register from '../pages/Register.vue'
import CreateCourse from '../pages/CreateCourse.vue'
import CreateLesson from '../pages/CreateLesson.vue'
import CreateQuiz from '../pages/CreateQuiz.vue'
import InstructorDashboard from '../pages/InstructorDashboard.vue'
import LessonView from '../pages/LessonView.vue'
import QuizView from '../pages/QuizView.vue'
import ResourceNotFound from '../pages/ResourceNotFound.vue'
import Unauthorized from '../pages/Unauthorized.vue'

const routes = [
  { path: '/', redirect: '/courses' },
  { path: '/courses', name: 'Courses', component: Courses },
  { path: '/courses/:id', name: 'CourseDetails', component: CourseDetails, props: true },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/create-course', name: 'CreateCourse', component: CreateCourse, meta: { requiresAuth: true } },
  { path: '/courses/:courseId/create-lesson', name: 'CreateLesson', component: CreateLesson, props: true, meta: { requiresAuth: true } },
  { path: '/courses/:courseId/create-quiz', name: 'CreateQuiz', component: CreateQuiz, props: true, meta: { requiresAuth: true } },
  { path: '/instructor', name: 'InstructorDashboard', component: InstructorDashboard, meta: { requiresAuth: true } },
  { path: '/courses/:courseId/lessons/:lessonId', name: 'LessonView', component: LessonView, props: true },
  { path: '/courses/:courseId/quiz/:quizId', name: 'QuizView', component: QuizView, props: true },
  { path: '/unauthorized', name: 'Unauthorized', component: Unauthorized },
  { path: '/:pathMatch(.*)*', name: 'NotFound', component: ResourceNotFound }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard for auth-protected routes
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
