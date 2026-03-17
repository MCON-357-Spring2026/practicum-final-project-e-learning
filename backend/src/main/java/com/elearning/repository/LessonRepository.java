package com.elearning.repository;

import com.elearning.model.Lesson;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {


    public List<Lesson> getByCourseId(String courseId);

}
