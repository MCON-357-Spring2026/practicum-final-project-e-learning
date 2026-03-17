package com.elearning.controller;

import com.elearning.service.CourseService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import com.elearning.model.Course;


@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable String id) {
        return courseService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<Course>> getCourseByInstructorId(@PathVariable String instructorId) {
        List<Course> courses = courseService.getByInstructorId(instructorId);
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/")
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        try {
            return ResponseEntity.ok(courseService.create(course));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "A course with this department and course number already exists."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
}
