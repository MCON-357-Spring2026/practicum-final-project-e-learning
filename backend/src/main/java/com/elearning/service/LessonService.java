package com.elearning.service;

import java.util.List;
import java.util.Optional;
import com.elearning.model.Course;
import com.elearning.model.Lesson;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.LessonRepository;
import org.springframework.stereotype.Service;

@Service
public class LessonService implements ServiceInterface<Lesson> {

    private LessonRepository repo;
    private CourseRepository courseRepo;

    public LessonService(LessonRepository repo, CourseRepository courseRepo) {
        this.repo = repo;
        this.courseRepo = courseRepo;
    }

    public List<Lesson> getAll() {
        return repo.findAll();
    }

    public Optional<Lesson> getById(String id) {
        return repo.findById(id);
    }

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

    public Optional<Lesson> replace(String id, Lesson lesson) {
        if (repo.existsById(id)) {
            lesson.setId(id);
            return Optional.of(repo.save(lesson));
        } else {
            return Optional.empty();
        }
    }

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

}
