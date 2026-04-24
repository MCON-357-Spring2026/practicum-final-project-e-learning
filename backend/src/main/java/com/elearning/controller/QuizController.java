package com.elearning.controller;

import com.elearning.dto.QuizDTO;
import com.elearning.dto.QuizPreviewDTO;
import com.elearning.model.Course;
import com.elearning.model.Quiz;
import com.elearning.repository.CourseRepository;
import com.elearning.security.AuthenticatedUser;
import com.elearning.service.QuizService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

/**
 * REST controller for quiz management endpoints at {@code /api/quizzes}.
 * Provides CRUD operations with role-based write access (TEACHER/ADMIN).
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private final QuizService quizService;
    private final CourseRepository courseRepository;

    public QuizController(QuizService quizService, CourseRepository courseRepository) {
        this.quizService = quizService;
        this.courseRepository = courseRepository;
    }

    /**
     * Retrieves all quizzes.
     *
     * @return 200 with list of all quizzes
     */
    @GetMapping("/")
    public ResponseEntity<List<QuizDTO>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAll().stream().map(QuizDTO::new).toList());
    }

    /**
     * Retrieves a quiz by its ID.
     *
     * @param id the quiz ID
     * @return 200 with the quiz, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable String id) {
        return quizService.getById(id)
                .map(q -> ResponseEntity.ok(new QuizDTO(q)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/edit")
    public ResponseEntity<?> getQuizForEdit(@PathVariable String id, @AuthenticationPrincipal AuthenticatedUser principal) {
        return quizService.getById(id)
                .map(quiz -> {
                    if (quiz.getCourseId() != null) {
                        Course course = courseRepository.findById(quiz.getCourseId()).orElse(null);
                        if (course == null || principal == null || !course.getInstructorId().equals(principal.id())) {
                            return ResponseEntity.status(403).body((Object) java.util.Map.of("error", "Only the course creator can edit this quiz"));
                        }
                    }
                    return ResponseEntity.ok((Object) quiz);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves preview information for all quizzes.
     *
     * @return 200 with list of quiz previews
     */
    @GetMapping("/preview")
    public ResponseEntity<List<QuizPreviewDTO>> getAllQuizPreviews() {
        return ResponseEntity.ok(quizService.getAll().stream().map(QuizPreviewDTO::new).toList());
    }

    /**
     * Retrieves all quizzes for a given course.
     *
     * @param courseId the course ID
     * @return 200 with list of quizzes
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(quizService.getByCourseId(courseId).stream().map(QuizDTO::new).toList());
    }

    /**
     * Retrieves preview information for all quizzes in a given course.
     *
     * @param courseId the course ID
     * @return 200 with list of quiz previews
     */
    @GetMapping("/course/{courseId}/preview")
    public ResponseEntity<List<QuizPreviewDTO>> getQuizPreviewsByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(quizService.getByCourseId(courseId).stream().map(QuizPreviewDTO::new).toList());
    }

    /**
     * Creates a new quiz.
     *
     * @param quiz the quiz to create
     * @return 200 with the created quiz, or 400 on validation error
     */
    @PostMapping("/")
    public ResponseEntity<?> createQuiz(@RequestBody Quiz quiz, @AuthenticationPrincipal AuthenticatedUser principal) {
        try {
            if (quiz.getCourseId() != null) {
                Course course = courseRepository.findById(quiz.getCourseId()).orElse(null);
                if (course == null) {
                    return ResponseEntity.badRequest().body(java.util.Map.of("error", "Course not found"));
                }
                if (principal == null || !course.getInstructorId().equals(principal.id())) {
                    return ResponseEntity.status(403).body(java.util.Map.of("error", "Only the course creator can add quizzes"));
                }
            }
            return ResponseEntity.ok(quizService.create(quiz));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    /**
     * Partially updates an existing quiz.
     *
     * @param id   the quiz ID
     * @param quiz the fields to update
     * @return 200 with the updated quiz, or 404 if not found
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable String id, @RequestBody Quiz quiz) {
        return quizService.update(id, quiz)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Fully replaces an existing quiz.
     *
     * @param id   the quiz ID
     * @param quiz the replacement quiz data
     * @return 200 with the replaced quiz, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Quiz> replaceQuiz(@PathVariable String id, @RequestBody Quiz quiz) {
        return quizService.replace(id, quiz)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a quiz by its ID.
     *
     * @param id the quiz ID
     * @return 204 no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable String id) {
        quizService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
