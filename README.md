# E-Learning Platform

A full-stack e-learning platform built with **Spring Boot** and **Vue.js**, enabling instructors to create and manage courses, lessons, and quizzes, while students can browse courses, enroll, track progress, and complete assessments.

## Tech Stack

### Backend
- **Java 21** with **Spring Boot 3.3**
- **MongoDB** (Spring Data MongoDB)
- **Spring Security** with **JWT** authentication
- **SendGrid** for email/messaging
- **Lombok** for reduced boilerplate
- **Maven** build system

### Frontend
- **Vue 3** with **TypeScript**
- **Vue Router** for client-side routing with auth guards
- **Pinia** for state management
- **Axios** for HTTP requests
- **Vite** for development and build tooling

## Project Structure

```
e-learning-platform/
├── backend/
│   └── src/main/java/com/elearning/
│       ├── config/          # Database seeder
│       ├── controller/      # REST controllers
│       ├── dto/             # Data transfer objects
│       ├── enums/           # Role, Gender enums
│       ├── errors/          # Custom exceptions
│       ├── model/           # Domain models (Person, User, Teacher, Course, etc.)
│       ├── repository/      # MongoDB repositories
│       ├── security/        # JWT filter, auth entry point
│       └── service/         # Business logic layer
├── frontend/
│   └── src/
│       ├── api/             # Axios API clients
│       ├── components/      # Reusable Vue components
│       ├── pages/           # Route-level views
│       ├── router/          # Vue Router config
│       └── store/           # Pinia stores
└── package.json             # Root scripts (concurrent dev)
```

## Domain Models

| Model | Description |
|---|---|
| **Person** | Base entity with name, date of birth, gender, and address |
| **User** | Extends Person — adds username, email, password, role, and enrollments |
| **Teacher** | Extends User — adds department and course associations |
| **Course** | Title, instructor, department, credits, lessons, and quizzes |
| **Lesson** | Title, description, content, duration, and resources |
| **Quiz** | Collection of questions tied to a course |
| **Enrollment** | Links a student to a course with progress tracking |
| **Message** | Messaging between users (including blast messages) |

## API Endpoints

### Courses — `/api/courses`
`GET /` | `GET /{id}` | `GET /instructor/{instructorId}` | `POST /` | `PATCH /{id}` | `PUT /{id}` | `DELETE /{id}`

### Lessons — `/api/lessons`
`GET /` | `GET /{id}` | `POST /` | `PATCH /{id}` | `PUT /{id}` | `DELETE /{id}`

### Quizzes — `/api/quizzes`
`GET /` | `GET /{id}` | `GET /course/{courseId}` | `POST /` | `PATCH /{id}` | `PUT /{id}` | `DELETE /{id}`

### Users — `/api/users`
`GET /` | `GET /{id}` | `GET /username/{username}` | `GET /email/{email}` | `GET /role/{role}` | `POST /` | `PATCH /{id}` | `PUT /{id}` | `DELETE /{id}`

### Teachers — `/api/teachers`
`GET /` | `GET /{id}` | `GET /admin` | `GET /pending` | `GET /department/{department}` | `POST /` | `PATCH /{id}` | `PATCH /{id}/role` | `PUT /{id}` | `DELETE /{id}`

### Enrollments — `/api/enrollments`
`GET /` | `GET /student/{studentId}` | `GET /course/{courseId}` | `GET /student/{studentId}/course/{courseId}` | `POST /student/{studentId}` | `POST /course/{courseId}` | `DELETE /student/{studentId}/course/{courseId}`

## Getting Started

### Prerequisites
- **Java 21**
- **Maven**
- **Node.js** (v18+)
- **MongoDB** instance (local or Atlas)

### Environment Variables

Create a `.env` file in the `backend/` directory:

```env
MONGODB_URI=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/<dbname>
SENDGRID_API_KEY=your_sendgrid_api_key
```

### Running the Application

**Both frontend and backend concurrently:**
```bash
npm run dev
```

**Backend only:**
```bash
npm run dev:backend
# or
cd backend && mvn spring-boot:run
```

**Frontend only:**
```bash
npm run dev:frontend
# or
cd frontend && npm run dev
```

The backend runs on `http://localhost:8080` and the frontend on `http://localhost:5173` with API requests proxied to the backend.

### Running Tests

```bash
cd backend
mvn test
```

The test suite includes **165 unit tests** covering all controllers, services, and repositories using JUnit 5 and Mockito.

## Roles

| Role | Description |
|---|---|
| `STUDENT` | Can browse courses, enroll, view lessons, and take quizzes |
| `TEACHER` | Can create and manage courses, lessons, and quizzes |
| `ADMIN` | Administrative access |
| `PENDING` | Awaiting role approval |
