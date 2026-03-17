package com.elearning.controller;

import com.elearning.errors.CourseNotFoundException;
import com.elearning.errors.StudentNotFoundException;
import com.elearning.service.EnrollmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import com.elearning.model.Enrollment;

@RestController
@RequestMapping("/api/enrollments")

public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAll());
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(enrollmentService.getByStudentId(studentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(enrollmentService.getByCourseId(courseId));
    }

    @GetMapping({"/student/{studentId}/course/{courseId}", "/course/{courseId}/student/{studentId}"})
    public ResponseEntity<Enrollment> getEnrollmentByStudentIdAndCourseId(@PathVariable String studentId, @PathVariable String courseId) {
        return enrollmentService.getByStudentIdAndCourseId(studentId, courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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

    @DeleteMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable String studentId, @PathVariable String courseId) {
        return enrollmentService.getByStudentIdAndCourseId(studentId, courseId)
                .map(enrollment -> {
                    enrollmentService.delete(enrollment.getId());
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }



}
