# E-Learning Platform

A full-stack e-learning platform built with **Spring Boot** and **Vue.js**, featuring course management, real-time AI tutoring powered by OpenAI, quiz auto-grading, direct and blast messaging, and role-based access control — all backed by MongoDB and secured with JWT authentication.

## Tech Stack

### Backend
- **Java 21** with **Spring Boot 3.3.2**
- **MongoDB** (Spring Data MongoDB)
- **Spring Security** with **JWT** authentication (HMAC-SHA256)
- **OpenAI GPT-4o-mini** integration for AI tutoring
- **Bean Validation** (`@NotNull`, `@Email`, etc.)
- **Lombok** for reduced boilerplate
- **Maven** build system

### Frontend
- **Vue 3** with **TypeScript**
- **Vue Router 4** for client-side routing with auth guards
- **Pinia** for state management
- **Axios** for HTTP requests with JWT interceptor
- **Marked** + **DOMPurify** for safe Markdown rendering
- **KaTeX** for math equation rendering
- **Mammoth** for Word document support
- **Vite** for development and build tooling

## Project Structure

```
e-learning-platform/
├── backend/
│   └── src/main/java/com/elearning/
│       ├── config/          # Database seeder, CORS, security config
│       ├── controller/      # 11 REST controllers
│       ├── dto/             # 18 data transfer objects
│       ├── enums/           # Role, Gender, Department enums
│       ├── errors/          # Custom exceptions
│       ├── model/           # Domain models (Person, User, Teacher, Course, etc.)
│       ├── repository/      # MongoDB repositories
│       ├── security/        # JWT filter, auth entry point, authenticated user record
│       └── service/         # 11 services (including AI chat, email, auto-grading)
├── frontend/
│   └── src/
│       ├── api/             # 10 Axios API clients
│       ├── components/      # Reusable Vue components (navbar, forms, tutor, messaging)
│       ├── constants/       # Department definitions
│       ├── pages/           # 20+ route-level views
│       ├── router/          # Vue Router config with auth & role guards
│       └── store/           # Pinia stores (auth, course)
└── package.json             # Root scripts (concurrent dev)
```

## Features

### Course Management
- Instructors create and manage courses with lessons, quizzes, and media
- Courses organized by department with unique course numbers
- Students browse, enroll, and track progress through courses
- Lesson completion tracking and quiz auto-grading with score feedback

### AI Tutor
- Conversational AI tutor powered by **OpenAI GPT-4o-mini**
- Subject-scoped conversations across 13 academic departments
- Auto-generated conversation titles
- Full conversation context maintained across messages
- Markdown and **KaTeX** math rendering in responses (inline `$...$` and block `$$...$$`)

### Messaging
- **Direct messaging** between any two users with read/unread tracking
- **Blast messaging** — teachers and admins can send to "teachers", "admin", "teachers and admin", or all students enrolled in a specific course
- Bidirectional conversation view, date-based queries
- Full messaging center with inbox, sent, compose, and blasts views

### Authentication & Authorization
- JWT-based stateless authentication with 24-hour token expiry
- BCrypt password hashing
- Role-based access control at both route and method levels (`@PreAuthorize`)
- Frontend auth guards with login redirect and role-based routing
- Email validation via EasyEmail API during registration

## Domain Models

| Model | Description |
|---|---|
| **Person** | Base entity — first/last name, date of birth, gender, home address |
| **User** | Extends Person — username (unique), email, password, role, enrollment IDs |
| **Teacher** | Extends User — department, course IDs |
| **Course** | Title, description, instructor, department, credits, course number, lesson/quiz IDs, image |
| **Lesson** | Title, description, text content, duration, resources, media URLs |
| **Quiz** | Title, course, and list of `Question` objects (text, options, correct index) |
| **Enrollment** | Student–course link with progress status, completed lessons, and quiz grades (score, responses, feedback) |
| **Message** | Sender, receiver, subject, body, read status, blast flag |
| **MessageBlast** | Sender, message IDs, subject, timestamp |
| **ChatConversation** | Person, title, department (subject), list of `ChatMessage` entries (message, sender role, time) |

## API Endpoints

### Auth — `/api/auth`
| Method | Path | Description |
|---|---|---|
| `POST` | `/login` | Authenticate and receive JWT |
| `POST` | `/register` | Register a new account and receive JWT |

### Courses — `/api/courses`
| Method | Path | Description |
|---|---|---|
| `GET` | `/` | List all courses |
| `GET` | `/{id}` | Get course by ID |
| `GET` | `/instructor/{instructorId}` | Get courses by instructor |
| `POST` | `/` | Create a course |
| `PATCH` | `/{id}` | Partially update a course |
| `PUT` | `/{id}` | Replace a course |
| `DELETE` | `/{id}` | Delete a course |

### Lessons — `/api/lessons`
| Method | Path | Description |
|---|---|---|
| `GET` | `/` | List all lessons |
| `GET` | `/{id}` | Get lesson by ID |
| `GET` | `/course/{courseId}/previews` | Get lesson previews for a course |
| `POST` | `/` | Create a lesson |
| `PATCH` | `/{id}` | Partially update a lesson |
| `PUT` | `/{id}` | Replace a lesson |
| `DELETE` | `/{id}` | Delete a lesson |

### Quizzes — `/api/quizzes`
| Method | Path | Description |
|---|---|---|
| `GET` | `/` | List all quizzes |
| `GET` | `/{id}` | Get quiz by ID |
| `GET` | `/{id}/edit` | Get quiz for editing |
| `GET` | `/preview` | Get all quiz previews |
| `GET` | `/course/{courseId}` | Get quizzes by course |
| `GET` | `/course/{courseId}/preview` | Get quiz previews by course |
| `POST` | `/` | Create a quiz |
| `PATCH` | `/{id}` | Partially update a quiz |
| `PUT` | `/{id}` | Replace a quiz |
| `DELETE` | `/{id}` | Delete a quiz |

### Users — `/api/users`
| Method | Path | Description |
|---|---|---|
| `GET` | `/` | List all users |
| `GET` | `/{id}` | Get user by ID (owner or admin) |
| `GET` | `/username/{username}` | Get user by username |
| `GET` | `/role/{role}` | Get users by role |
| `GET` | `/{id}/profile` | Get user profile DTO |
| `POST` | `/` | Create a user |
| `PATCH` | `/{id}` | Partially update a user |
| `PUT` | `/{id}` | Replace a user |
| `DELETE` | `/{id}` | Delete a user (admin only) |

### Teachers — `/api/teachers`
| Method | Path | Description |
|---|---|---|
| `GET` | `/` | List all teachers |
| `GET` | `/admin` | List admins (admin only) |
| `GET` | `/pending` | List pending teachers (admin only) |
| `GET` | `/{id}` | Get teacher by ID |
| `GET` | `/{id}/preview` | Get limited teacher preview |
| `GET` | `/department/{dept}` | Get teachers by department |
| `POST` | `/` | Create a teacher |
| `PATCH` | `/{id}` | Partially update a teacher |
| `PATCH` | `/{id}/department` | Update department |
| `PATCH` | `/{id}/role` | Update role (admin only) |
| `PATCH` | `/{id}/promote/teacher` | Promote to teacher |
| `PATCH` | `/{id}/promote/admin` | Promote to admin |
| `PUT` | `/{id}` | Replace a teacher |
| `DELETE` | `/{id}` | Delete a teacher |

### Enrollments — `/api/enrollments`
| Method | Path | Description |
|---|---|---|
| `GET` | `/` | List all enrollments |
| `GET` | `/student/{studentId}` | Get enrollments by student |
| `GET` | `/course/{courseId}` | Get enrollments by course |
| `GET` | `/course/{courseId}/students` | Get enrolled students |
| `GET` | `/student/{sid}/course/{cid}` | Get specific enrollment |
| `POST` | `/student/{studentId}` | Enroll student |
| `POST` | `/course/{courseId}` | Enroll in course |
| `DELETE` | `/student/{sid}/course/{cid}` | Unenroll student |
| `PATCH` | `/quiz/{quizId}/submit` | Submit and auto-grade a quiz |
| `PATCH` | `/course/{cid}/lesson/{lid}/complete` | Mark lesson as completed |

### Messages — `/api/messages`
| Method | Path | Description |
|---|---|---|
| `GET` | `/` | List all messages |
| `GET` | `/{id}` | Get message by ID |
| `POST` | `/` | Send a message |
| `PATCH` | `/{id}` | Update message (receiver only) |
| `PUT` | `/{id}` | Replace a message |
| `DELETE` | `/{id}` | Delete a message |
| `GET` | `/user/{userId}` | Get messages for a user |
| `GET` | `/sender/{senderId}` | Get messages by sender |
| `GET` | `/receiver/{receiverId}` | Get messages by receiver |
| `GET` | `/conversation` | Get conversation (sender + receiver) |
| `GET` | `/conversation/between` | Get bidirectional conversation |

### Message Blasts — `/api/message-blasts`
| Method | Path | Description |
|---|---|---|
| `POST` | `/` | Create a blast (teacher/admin) |
| `GET` | `/` | Get my blasts |
| `GET` | `/{id}` | Get blast by ID |
| `GET` | `/{id}/messages` | Get blast messages |
| `DELETE` | `/{id}` | Delete a blast |

### AI Chat — `/api/chats`
| Method | Path | Description |
|---|---|---|
| `GET` | `/` | List all conversations |
| `GET` | `/{id}` | Get conversation by ID |
| `POST` | `/` | Create a conversation |
| `DELETE` | `/{id}` | Delete a conversation |
| `GET` | `/person/{personId}` | Get conversations by person |
| `GET` | `/person/{personId}/subject` | Filter by department |
| `GET` | `/person/{personId}/previews` | Get conversation previews |
| `POST` | `/person/{personId}/start` | Start a new AI conversation |
| `PATCH` | `/{id}/rename` | Rename a conversation |
| `PATCH` | `/{conversationId}/message` | Send a message to AI tutor |

### Email Validation — `/api/validate-email`
| Method | Path | Description |
|---|---|---|
| `GET` | `/{email}` | Validate email (MX records, disposable check) |

## Roles & Permissions

| Role | Permissions |
|---|---|
| `STUDENT` | Browse courses, enroll, view lessons, take quizzes, use AI tutor, send/receive messages |
| `TEACHER` | Everything students can do, plus create/manage courses, lessons, quizzes, and send blast messages |
| `ADMIN` | Full access — manage teachers, approve pending accounts, promote roles, send blasts |
| `PENDING` | Limited access while awaiting teacher approval |

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
JWT_SECRET=your_jwt_secret_key_at_least_32_bytes
EASY_EMAIL_API_KEY=your_easyemail_api_key
OPEN_AI_API_KEY=your_openai_api_key
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

### Database Seeding

On first startup with an empty database, the backend automatically seeds sample data including:
- 2 students, 3 teachers (CS and Math departments), and 1 admin
- 5 courses (Java, Web Dev, DB Systems, Calculus I, Physics I)
- 14 lessons and 4 quizzes with multiple-choice questions
- Sample enrollments with progress tracking

### Running Tests

```bash
cd backend
mvn test
```

The test suite includes **19 test classes** across four categories:

| Category | Coverage |
|---|---|
| **Controller Tests** (6) | Course, Lesson, MessageBlast, Quiz, Teacher, User |
| **Service Tests** (5) | Course, Lesson, MessageBlast, Quiz, User |
| **Repository Tests** (5) | Course, Lesson, MessageBlast, Person, Quiz |
| **Security Tests** (3) | JWT utility, JWT filter, custom auth entry point |
