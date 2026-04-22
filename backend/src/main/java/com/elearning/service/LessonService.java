package com.elearning.service;

import java.util.List;
import java.util.Optional;
import com.elearning.dto.LessonPreviewDTO;
import com.elearning.model.Course;
import com.elearning.model.Lesson;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.LessonRepository;
import org.springframework.stereotype.Service;

/**
 * Service for managing {@link Lesson} entities.
 * Handles CRUD operations and maintains bidirectional references
 * between lessons and their parent courses.
 */
@Service
public class LessonService implements ServiceInterface<Lesson> {

    private LessonRepository repo;
    private CourseRepository courseRepo;

    public LessonService(LessonRepository repo, CourseRepository courseRepo) {
        this.repo = repo;
        this.courseRepo = courseRepo;
    }

    /** {@inheritDoc} */
    public List<Lesson> getAll() {
        return repo.findAll();
    }

    /** {@inheritDoc} */
    public Optional<Lesson> getById(String id) {
        return repo.findById(id);
    }

    /**
     * Creates a lesson and adds its ID to the parent course's lesson list.
     *
     * @param lesson the lesson to create
     * @return the saved lesson
     * @throws IllegalArgumentException if the specified course is not found
     */
    public Lesson create(Lesson lesson) {
        if (lesson.getCourseId() != null) {
            Optional<Course> course = courseRepo.findById(lesson.getCourseId());
            if (course.isEmpty()) {
                throw new IllegalArgumentException("Course not found with id: " + lesson.getCourseId());
            }
            Lesson savedLesson = repo.save(lesson);
            Course c = course.get();
            c.addLessonID(savedLesson.getId());
            courseRepo.save(c);
            return savedLesson;
        }
        return repo.save(lesson);
    }

    /** {@inheritDoc} */
    public Optional<Lesson> update(String id, Lesson lesson) {
        Optional<Lesson> existingLesson = repo.findById(id);
        if (existingLesson.isPresent()) {
            Lesson updatedLesson = existingLesson.get();
            if (lesson.getTitle() != null) {
                updatedLesson.setTitle(lesson.getTitle());
            }
            if (lesson.getDescription() != null) {
                updatedLesson.setDescription(lesson.getDescription());
            }
            if (lesson.getMinutes() != 0) {
                updatedLesson.setMinutes(lesson.getMinutes());
            }
            if (lesson.getResources() != null) {
                updatedLesson.setResources(lesson.getResources());
            }
            return Optional.of(repo.save(updatedLesson));
        } else {
            return Optional.empty();
        }
    }

    /** {@inheritDoc} */
    public Optional<Lesson> replace(String id, Lesson lesson) {
        if (repo.existsById(id)) {
            lesson.setId(id);
            return Optional.of(repo.save(lesson));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Deletes a lesson and removes its ID from the parent course's lesson list.
     *
     * @param id the lesson ID to delete
     * @return true if the lesson was deleted, false if not found
     */
    public boolean delete(String id) {
        Optional<Lesson> lesson = repo.findById(id);
        if (lesson.isPresent()) {
            Lesson l = lesson.get();
            if (l.getCourseId() != null) {
                Optional<Course> course = courseRepo.findById(l.getCourseId());
                if (course.isPresent()) {
                    Course c = course.get();
                    c.removeLessonID(id);
                    courseRepo.save(c);
                }
            }
            repo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public List<LessonPreviewDTO> getPreviewsByCourseId(String courseId) {
        return repo.getByCourseId(courseId).stream()
                .map(LessonPreviewDTO::new)
                .toList();
    }

}
