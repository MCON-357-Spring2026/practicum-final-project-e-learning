package com.elearning.service;

import java.util.List;
import java.util.Optional;
import com.elearning.model.Course;
import com.elearning.model.Quiz;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.QuizRepository;
import org.springframework.stereotype.Service;

@Service
public class QuizService implements ServiceInterface<Quiz> {

    private QuizRepository repo;
    private CourseRepository courseRepo;

    public QuizService(QuizRepository repo, CourseRepository courseRepo) {
        this.repo = repo;
        this.courseRepo = courseRepo;
    }

    public List<Quiz> getAll() {
        return repo.findAll();
    }

    public Optional<Quiz> getById(String id) {
        return repo.findById(id);
    }

    public List<Quiz> getByCourseId(String courseId) {
        return repo.getByCourseId(courseId);
    }

    public Quiz create(Quiz quiz) {
        if (quiz.getCourseId() != null) {
            Optional<Course> course = courseRepo.findById(quiz.getCourseId());
            if (course.isEmpty()) {
                throw new IllegalArgumentException("Course not found with id: " + quiz.getCourseId());
            }
            Quiz savedQuiz = repo.save(quiz);
            Course c = course.get();
            c.addQuizID(savedQuiz.getId());
            courseRepo.save(c);
            return savedQuiz;
        }
        return repo.save(quiz);
    }

    public Optional<Quiz> update(String id, Quiz quiz) {
        Optional<Quiz> existingQuiz = repo.findById(id);
        if (existingQuiz.isPresent()) {
            Quiz updatedQuiz = existingQuiz.get();
            if (quiz.getTitle() != null) {
                updatedQuiz.setTitle(quiz.getTitle());
            }
            if (quiz.getQuestions() != null) {
                updatedQuiz.setQuestions(quiz.getQuestions());
            }
            return Optional.of(repo.save(updatedQuiz));
        } else {
            return Optional.empty();
        }
    }

    public Optional<Quiz> replace(String id, Quiz quiz) {
        if (repo.existsById(id)) {
            quiz.setId(id);
            return Optional.of(repo.save(quiz));
        } else {
            return Optional.empty();
        }
    }

    public boolean delete(String id) {
        Optional<Quiz> quiz = repo.findById(id);
        if (quiz.isPresent()) {
            Quiz q = quiz.get();
            if (q.getCourseId() != null) {
                Optional<Course> course = courseRepo.findById(q.getCourseId());
                if (course.isPresent()) {
                    Course c = course.get();
                    c.removeQuizID(id);
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