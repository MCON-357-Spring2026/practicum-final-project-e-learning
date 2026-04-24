package com.elearning.controller;

import com.elearning.dto.GradeResultDTO;
import com.elearning.dto.QuizSubmissionDTO;
import com.elearning.errors.CourseNotFoundException;
import com.elearning.errors.EnrollmentNotFoundException;
import com.elearning.errors.QuizNotFoundException;
import com.elearning.errors.StudentNotFoundException;
import com.elearning.model.Grade;
import com.elearning.model.Quiz;
import com.elearning.service.EnrollmentService;
import com.elearning.service.QuizService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.elearning.model.Enrollment;

/**
 * REST controller for enrollment management endpoints at {@code /api/enrollments}.
 * Handles student enrollment in courses and quiz submissions.
 */
@RestController
@RequestMapping("/api/enrollments")

public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final QuizService quizService;

    public EnrollmentController(EnrollmentService enrollmentService, QuizService quizService) {
        this.enrollmentService = enrollmentService;
        this.quizService = quizService;
    }

    /**
     * Retrieves all enrollments.
     *
     * @return 200 with list of all enrollments
     */
    @GetMapping("/")
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAll());
    }
    
    /**
     * Retrieves all enrollments for a given student.
     * Requires the requesting user to be the student, a TEACHER, or an ADMIN.
     *
     * @param studentId the student's ID
     * @return 200 with list of enrollments
     */
    @PreAuthorize("#studentId == authentication.principal.id or hasAnyRole('TEACHER', 'ADMIN')")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(enrollmentService.getByStudentId(studentId));
    }

    /**
     * Retrieves all enrollments for a given course.
     *
     * @param courseId the course ID
     * @return 200 with list of enrollments
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(enrollmentService.getByCourseId(courseId));
    }

    /**
     * Retrieves an enrollment by student ID and course ID.
     *
     * @param studentId the student's ID
     * @param courseId  the course ID
     * @return 200 with the enrollment, or 404 if not found
     */
    @GetMapping({"/student/{studentId}/course/{courseId}", "/course/{courseId}/student/{studentId}"})
    public ResponseEntity<Enrollment> getEnrollmentByStudentIdAndCourseId(@PathVariable String studentId, @PathVariable String courseId) {
        return enrollmentService.getByStudentIdAndCourseId(studentId, courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates an enrollment for a student. Requires the requesting user to be
     * the student or an ADMIN.
     *
     * @param studentId the student's ID
     * @param courseId  the course ID (in request body)
     * @return 200 with the created enrollment, or 400 on validation error
     */
    @PreAuthorize("#studentId == authentication.principal.id or hasRole('ADMIN')")
    @PostMapping("/student/{studentId}")
    public ResponseEntity<?> createEnrollment(@PathVariable String studentId, @RequestBody String courseId) {
        try {
            Enrollment enrollment = new Enrollment(studentId, courseId);
            return ResponseEntity.ok(enrollmentService.create(enrollment));
        } catch (CourseNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (StudentNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Creates an enrollment by course ID with student ID in the body.
     *
     * @param courseId  the course ID
     * @param studentId the student's ID (in request body)
     * @return 200 with the created enrollment, or 400 on validation error
     */
    @PostMapping("/course/{courseId}")
    public ResponseEntity<?> createEnrollmentByCourseId(@PathVariable String courseId, @RequestBody String studentId) {
        try {
            Enrollment enrollment = new Enrollment(studentId, courseId);
            return ResponseEntity.ok(enrollmentService.create(enrollment));
        } catch (CourseNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (StudentNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Deletes an enrollment for a student in a course. Requires the requesting
     * user to be the student or an ADMIN.
     *
     * @param studentId the student's ID
     * @param courseId  the course ID
     * @return 204 if deleted, or 404 if not found
     */
    @PreAuthorize("#studentId == authentication.principal.id or hasRole('ADMIN')")
    @DeleteMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable String studentId, @PathVariable String courseId) {
        return enrollmentService.getByStudentIdAndCourseId(studentId, courseId)
                .map(enrollment -> {
                    enrollmentService.delete(enrollment.getId());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Submits quiz answers for an enrollment. Grades the quiz, stores the result,
     * and returns the grade.
     *
     * @param quizId     the quiz ID
     * @param submission the submission containing enrollmentId and answers
     * @return 200 with the Grade, 404 if enrollment/quiz not found, or 400 on invalid answers
     */
    @PatchMapping("/quiz/{quizId}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable String quizId, @RequestBody QuizSubmissionDTO submission) {
        try {
            Grade grade = enrollmentService.submitQuiz(submission.getEnrollmentId(), quizId, submission.getAnswers());
            Quiz quiz = quizService.getById(quizId).orElse(null);
            ArrayList<Integer> correctAnswers = new ArrayList<>();
            int total = 0;
            if (quiz != null && quiz.getQuestions() != null) {
                total = quiz.getQuestions().size();
                for (var q : quiz.getQuestions()) {
                    correctAnswers.add(q.getCorrectOptionIndex());
                }
            }
            return ResponseEntity.ok(new GradeResultDTO(grade, total, correctAnswers));
        } catch (EnrollmentNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (QuizNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
