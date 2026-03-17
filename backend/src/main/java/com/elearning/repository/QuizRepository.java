package com.elearning.repository;

import com.elearning.model.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, String> {

    public List<Quiz> getByCourseId(String courseId);

}
