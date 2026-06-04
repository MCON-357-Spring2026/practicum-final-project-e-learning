package com.elearning.controller;

import com.elearning.dto.CreateCourseDTO;
import com.elearning.security.AuthenticatedUser;
import com.elearning.service.CourseService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import com.elearning.model.Course;


/**
 * REST controller for course management endpoints at {@code /api/courses}.
 * Provides CRUD operations with role-based write access (TEACHER/ADMIN).
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Retrieves all courses.
     *
     * @return 200 with list of all courses
     */
    @GetMapping("/")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAll());
    }

    /**
     * Retrieves a course by its ID.
     *
     * @param id the course ID
     * @return 200 with the course, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        return courseService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all courses taught by a given instructor.
     *
     * @param instructorId the instructor's ID
     * @return 200 with list of courses
     */
    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<Course>> getCourseByInstructorId(@PathVariable String instructorId) {
        List<Course> courses = courseService.getByInstructorId(instructorId);
        return ResponseEntity.ok(courses);
    }

    /**
     * Creates a new course. The instructor ID is set from the authenticated user.
     *
     * @param dto       the course data
     * @param principal the authenticated user
     * @return 200 with the created course, 409 on duplicate, or 400 on validation error
     */
    @PostMapping("/")
    public ResponseEntity<?> createCourse(@RequestBody CreateCourseDTO dto, @AuthenticationPrincipal AuthenticatedUser principal) {
        try {
            Course course = dto.toCourse(principal.id());
            return ResponseEntity.ok(courseService.create(course));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "A course with this department and course number already exists."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Partially updates an existing course.
     *
     * @param id     the course ID
     * @param course the fields to update
     * @return 200 with the updated course, 404 if not found, or 409 on duplicate
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable String id, @RequestBody Course course) {
        try {
            return courseService.update(id, course)
                    .map(c -> ResponseEntity.ok((Object) c))
                    .orElse(ResponseEntity.notFound().build());
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "A course with this department and course number already exists."));
        }
    }

    /**
     * Fully replaces an existing course.
     *
     * @param id     the course ID
     * @param course the replacement course data
     * @return 200 with the replaced course, 404 if not found, or 409 on duplicate
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> replaceCourse(@PathVariable String id, @RequestBody Course course) {
        try {
            return courseService.replace(id, course)
                    .map(c -> ResponseEntity.ok((Object) c))
                    .orElse(ResponseEntity.notFound().build());
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "A course with this department and course number already exists."));
        }
    }

    /**
     * Deletes a course by its ID.
     *
     * @param id the course ID
     * @return 204 no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
