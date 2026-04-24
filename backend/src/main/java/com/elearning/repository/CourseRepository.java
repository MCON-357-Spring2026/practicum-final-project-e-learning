package com.elearning.repository;

import com.elearning.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB repository for {@link Course} entities.
 * Provides CRUD operations and custom queries by instructor and department.
 */
@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    /**
     * Finds all courses taught by a given instructor.
     *
     * @param instructorId the instructor's ID
     * @return list of courses for the instructor
     */
    public List<Course> getByInstructorId(String instructorId);

    /**
     * Finds all courses in a given department.
     *
     * @param department the department name
     * @return list of courses in the department
     */
    public List<Course> getByDepartment(String department);
    
}
