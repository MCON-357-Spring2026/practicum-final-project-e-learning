package com.elearning.controller;

import com.elearning.dto.LessonPreviewDTO;
import com.elearning.service.LessonService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import com.elearning.model.Lesson;

/**
 * REST controller for lesson management endpoints at {@code /api/lessons}.
 * Provides CRUD operations with role-based write access (TEACHER/ADMIN).
 */
@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    /**
     * Retrieves all lessons.
     *
     * @return 200 with list of all lessons
     */
    @GetMapping("/")
    public ResponseEntity<List<Lesson>> getAllLessons() {
        return ResponseEntity.ok(lessonService.getAll());
    }

    /**
     * Retrieves a lesson by its ID.
     *
     * @param id the lesson ID
     * @return 200 with the lesson, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable String id) {
        return lessonService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new lesson.
     *
     * @param lesson the lesson to create
     * @return 200 with the created lesson
     */
    @PostMapping("/")
    public ResponseEntity<Lesson> createLesson(@RequestBody Lesson lesson) {
        return ResponseEntity.ok(lessonService.create(lesson));
    }

    /**
     * Partially updates an existing lesson.
     *
     * @param id     the lesson ID
     * @param lesson the fields to update
     * @return 200 with the updated lesson, or 404 if not found
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable String id, @RequestBody Lesson lesson) {
        return lessonService.update(id, lesson)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Fully replaces an existing lesson.
     *
     * @param id     the lesson ID
     * @param lesson the replacement lesson data
     * @return 200 with the replaced lesson, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Lesson> replaceLesson(@PathVariable String id, @RequestBody Lesson lesson) {
        return lessonService.replace(id, lesson)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a lesson by its ID.
     *
     * @param id the lesson ID
     * @return 204 no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable String id) {
        lessonService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{courseId}/previews")
    public ResponseEntity<List<LessonPreviewDTO>> getPreviewsByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(lessonService.getPreviewsByCourseId(courseId));
    }

}
