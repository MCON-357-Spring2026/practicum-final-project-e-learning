package com.elearning.repository;

import com.elearning.model.Lesson;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for {@link Lesson} entities.
 * Provides CRUD operations and custom queries by course ID.
 */
@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {

    /**
     * Finds all lessons belonging to a given course.
     *
     * @param courseId the course ID
     * @return list of lessons for the course
     */
    public List<Lesson> getByCourseId(String courseId);

}
