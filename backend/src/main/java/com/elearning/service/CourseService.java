package com.elearning.service;

import java.util.List;
import java.util.Optional;
import com.elearning.model.Course;
import com.elearning.model.Person;
import com.elearning.model.Teacher;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.PersonRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class CourseService implements ServiceInterface<Course> {
    
    private final CourseRepository repo;
    private final PersonRepository personRepo;

    public CourseService (CourseRepository repo, PersonRepository personRepo) { 
        this.repo = repo;
        this.personRepo = personRepo;
    }

    public List<Course> getAll() {
        // Implementation here
        return repo.findAll();
    }

    public Optional<Course> getById(String id) {
        // Implementation here
        return repo.findById(id);
    }

    public List<Course> getByInstructorId(String instructorId) {
        return repo.getByInstructorId(instructorId);
    }

    /**
     * Creates a course after verifying the instructorId references an existing Teacher.
     * Also adds the new course's ID to the Teacher's courseIds list.
     */
    public Course create(Course course) throws DuplicateKeyException {
        // Verify instructorId references a valid Teacher
        if (course.getInstructorId() != null) {
            Person person = personRepo.findById(course.getInstructorId())
                    .orElseThrow(() -> new IllegalArgumentException("Instructor not found with id: " + course.getInstructorId()));
            if (!(person instanceof Teacher)) {
                throw new IllegalArgumentException("Person with id " + course.getInstructorId() + " is not a teacher");
            }
        }

        Course savedCourse = repo.save(course);

        // Sync: add the courseId to the Teacher's courseIds list
        if (savedCourse.getInstructorId() != null) {
            personRepo.findById(savedCourse.getInstructorId()).ifPresent(person -> {
                if (person instanceof Teacher) {
                    Teacher teacher = (Teacher) person;
                    if (teacher.getCourseIds() == null) {
                        teacher.setCourseIds(new java.util.ArrayList<>());
                    }
                    if (!teacher.getCourseIds().contains(savedCourse.getId())) {
                        teacher.getCourseIds().add(savedCourse.getId());
                        personRepo.save(teacher);
                    }
                }
            });
        }

        return savedCourse;
    }

    public Optional<Course> update(String id, Course course) throws DuplicateKeyException {
        Optional<Course> existingCourse = repo.findById(id);
        if (existingCourse.isPresent()) {
            Course updatedCourse = existingCourse.get();
            if(course.getTitle() != null) {
                updatedCourse.setTitle(course.getTitle());
            }
            if(course.getDescription() != null) {
                updatedCourse.setDescription(course.getDescription());
            }
            if(course.getInstructorId() != null) {
                updatedCourse.setInstructorId(course.getInstructorId());
            } 
            if(course.getLessonIDs() != null) {
                updatedCourse.setLessonIDs(course.getLessonIDs());
            }
            if(course.getQuizIDs() != null) {
                updatedCourse.setQuizIDs(course.getQuizIDs());
            }
            if(course.getImage() != null) {
                updatedCourse.setImage(course.getImage());
            }
            if(course.getDepartment() != null) {
                updatedCourse.setDepartment(course.getDepartment());
            }
            if(course.getCredits() != 0) {
                updatedCourse.setCredits(course.getCredits());
            }
            if(course.getCourseNum() != 0) {
                updatedCourse.setCourseNum(course.getCourseNum());
            }
            return Optional.of(repo.save(updatedCourse));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Course> replace(String id, Course course) throws DuplicateKeyException {
        if (repo.existsById(id)) {
            course.setId(id);
            return Optional.of(repo.save(course));
        } else {
            return Optional.empty();
        }
    }
    /**
     * Deletes a course and removes its ID from the instructor's courseIds list.
     */
    public boolean delete(String id) {
        Optional<Course> courseOpt = repo.findById(id);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();

            // Sync: remove courseId from the Teacher's courseIds list
            if (course.getInstructorId() != null) {
                personRepo.findById(course.getInstructorId()).ifPresent(person -> {
                    if (person instanceof Teacher) {
                        Teacher teacher = (Teacher) person;
                        if (teacher.getCourseIds() != null) {
                            teacher.getCourseIds().remove(id);
                            personRepo.save(teacher);
                        }
                    }
                });
            }

            repo.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
    
}
