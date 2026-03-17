package com.elearning.repository;

import com.elearning.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    public List<Course> getByInstructorId(String instructorId);

    public List<Course> getByDepartment(String department);
    
}
