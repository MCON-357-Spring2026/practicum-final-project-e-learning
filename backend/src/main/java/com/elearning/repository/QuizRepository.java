package com.elearning.repository;

import com.elearning.model.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB repository for {@link Quiz} entities.
 * Provides CRUD operations and custom queries by course ID.
 */
@Repository
public interface QuizRepository extends MongoRepository<Quiz, String> {

    /**
     * Finds all quizzes belonging to a given course.
     *
     * @param courseId the course ID
     * @return list of quizzes for the course
     */
    public List<Quiz> getByCourseId(String courseId);

}
