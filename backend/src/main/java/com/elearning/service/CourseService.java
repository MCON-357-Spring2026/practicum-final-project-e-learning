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

/**
 * Service for managing {@link Course} entities.
 * Handles CRUD operations and maintains bidirectional references
 * between courses and their instructors.
 */
@Service
public class CourseService implements ServiceInterface<Course> {
    
    private final CourseRepository repo;
    private final PersonRepository personRepo;

    public CourseService (CourseRepository repo, PersonRepository personRepo) { 
        this.repo = repo;
        this.personRepo = personRepo;
    }

    /** {@inheritDoc} */
    public List<Course> getAll() {
        return repo.findAll();
    }

    /** {@inheritDoc} */
    public Optional<Course> getById(String id) {
        return repo.findById(id);
    }

    /**
     * Finds all courses taught by a given instructor.
     *
     * @param instructorId the instructor's ID
     * @return list of courses for the instructor
     */
    public List<Course> getByInstructorId(String instructorId) {
        return repo.getByInstructorId(instructorId);
    }

    /**
     * Creates a course after verifying the instructorId references an existing Teacher.
     * Also adds the new course's ID to the Teacher's courseIds list.
     *
     * @param course the course to create
     * @return the saved course
     * @throws DuplicateKeyException     if a course with the same department and number exists
     * @throws IllegalArgumentException if the instructor is not found or is not a teacher
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

    /**
     * Partially updates an existing course's fields.
     *
     * @param id     the course ID
     * @param course the course containing updated fields
     * @return an Optional containing the updated course if found
     * @throws DuplicateKeyException if the update causes a duplicate key violation
     */
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

    /**
     * Fully replaces an existing course.
     *
     * @param id     the course ID
     * @param course the replacement course
     * @return an Optional containing the replaced course if found
     * @throws DuplicateKeyException if the replacement causes a duplicate key violation
     */
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
     *
     * @param id the course ID to delete
     * @return true if the course was deleted, false if not found
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
