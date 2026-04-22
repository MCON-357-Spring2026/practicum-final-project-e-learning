package com.elearning.repository;

import com.elearning.model.Enrollment;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for {@link Enrollment} entities.
 * Provides CRUD operations and custom queries by student, course, or both.
 */
@Repository
public interface EnrollmentRepository extends MongoRepository<Enrollment, String> {

    /**
     * Finds an enrollment by student ID and course ID.
     *
     * @param studentId the student's ID
     * @param courseId  the course ID
     * @return the matching enrollment, or null if not found
     */
    Enrollment findByStudentIdAndCourseId(String studentId, String courseId);

    /**
     * Finds all enrollments for a given student.
     *
     * @param studentId the student's ID
     * @return list of enrollments for the student
     */
    List<Enrollment> findByStudentId(String studentId);

    /**
     * Finds all enrollments for a given course.
     *
     * @param courseId the course ID
     * @return list of enrollments for the course
     */
    List<Enrollment> findByCourseId(String courseId);

}
