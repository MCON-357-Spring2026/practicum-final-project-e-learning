import { createRouter, createWebHistory } from 'vue-router'

import Courses from '@/pages/courses/index.vue'
import CourseDetails from '@/pages/courses/[id]/CourseDetails.vue'
import EditCourse from '@/pages/courses/[id]/EditCourse.vue'
import CreateCourse from '@/pages/courses/CreateCourse.vue'
import Login from '@/pages/Login.vue'
import Register from '@/pages/Register.vue'
import CreateLesson from '@/pages/courses/[id]/lessons/CreateLesson.vue'
import CreateQuiz from '@/pages/courses/[id]/quizes/CreateQuiz.vue'
import InstructorDashboard from '@/pages/InstructorDashboard.vue'
import StudentDashboard from '@/pages/StudentDashboard.vue'
import LessonView from '@/pages/courses/[id]/lessons/[id]/LessonView.vue'
import QuizView from '@/pages/courses/[id]/quizes/[id]/QuizView.vue'
import EditQuiz from '@/pages/courses/[id]/quizes/[id]/EditQuiz.vue'
import EditLesson from '@/pages/courses/[id]/lessons/[id]/EditLesson.vue'
import Teachers from '@/pages/teachers/index.vue'
import TeacherDetail from '@/pages/teachers/[id]/index.vue'
import TeacherPreview from '@/pages/teachers/[id]/preview.vue'
import CourseLessons from '@/pages/courses/[id]/quizes/index.vue'
import ResourceNotFound from '@/pages/ResourceNotFound.vue'
import Unauthorized from '@/pages/Unauthorized.vue'
import Profile from '@/pages/Profile.vue'

const routes = [
  { path: '/', redirect: '/courses' },
  { path: '/courses', name: 'Courses', component: Courses },
  { path: '/courses/create', name: 'CreateCourse', component: CreateCourse, meta: { requiresAuth: true } },
  { path: '/courses/:id', name: 'CourseDetails', component: CourseDetails, props: true },
  { path: '/courses/:id/edit', name: 'EditCourse', component: EditCourse, props: true, meta: { requiresAuth: true } },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/courses/:courseId/create-lesson', name: 'CreateLesson', component: CreateLesson, props: true, meta: { requiresAuth: true } },
  { path: '/courses/:courseId/create-quiz', name: 'CreateQuiz', component: CreateQuiz, props: true, meta: { requiresAuth: true } },
  { path: '/instructor', name: 'InstructorDashboard', component: InstructorDashboard, meta: { requiresAuth: true } },
  { path: '/dashboard', name: 'StudentDashboard', component: StudentDashboard, meta: { requiresAuth: true } },
  { path: '/courses/:id/lessons', name: 'CourseLessons', component: CourseLessons, props: true },
  { path: '/courses/:courseId/lessons/:lessonId', name: 'LessonView', component: LessonView, props: true },
  { path: '/courses/:courseId/lessons/:lessonId/edit', name: 'EditLesson', component: EditLesson, props: true, meta: { requiresAuth: true } },
  { path: '/courses/:courseId/quiz/:quizId', name: 'QuizView', component: QuizView, props: true },
  { path: '/courses/:courseId/quiz/:quizId/edit', name: 'EditQuiz', component: EditQuiz, props: true, meta: { requiresAuth: true } },
  { path: '/teachers', name: 'Teachers', component: Teachers },
  { path: '/teachers/:id', name: 'TeacherDetail', component: TeacherDetail, props: true, meta: { requiresAuth: true } },
  { path: '/teachers/:id/preview', name: 'TeacherPreview', component: TeacherPreview, props: true },
  { path: '/profile', name: 'Profile', component: Profile, meta: { requiresAuth: true } },
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
