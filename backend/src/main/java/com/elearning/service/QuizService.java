package com.elearning.service;

import java.util.List;
import java.util.Optional;
import com.elearning.model.Course;
import com.elearning.model.Quiz;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.QuizRepository;
import org.springframework.stereotype.Service;

/**
 * Service for managing {@link Quiz} entities.
 * Handles CRUD operations and maintains bidirectional references
 * between quizzes and their parent courses.
 */
@Service
public class QuizService implements ServiceInterface<Quiz> {

    private QuizRepository repo;
    private CourseRepository courseRepo;

    public QuizService(QuizRepository repo, CourseRepository courseRepo) {
        this.repo = repo;
        this.courseRepo = courseRepo;
    }

    /** {@inheritDoc} */
    public List<Quiz> getAll() {
        return repo.findAll();
    }

    /** {@inheritDoc} */
    public Optional<Quiz> getById(String id) {
        return repo.findById(id);
    }

    /**
     * Finds all quizzes belonging to a given course.
     *
     * @param courseId the course ID
     * @return list of quizzes for the course
     */
    public List<Quiz> getByCourseId(String courseId) {
        return repo.getByCourseId(courseId);
    }

    /**
     * Creates a quiz and adds its ID to the parent course's quiz list.
     *
     * @param quiz the quiz to create
     * @return the saved quiz
     * @throws IllegalArgumentException if the specified course is not found
     */
    public Quiz create(Quiz quiz) {
        if (quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("A quiz must have at least one question.");
        }
        for (int i = 0; i < quiz.getQuestions().size(); i++) {
            var q = quiz.getQuestions().get(i);
            if (q.getOptions() == null || q.getOptions().length < 1) {
                throw new IllegalArgumentException("Question " + (i + 1) + " must have at least one choice.");
            }
            if (q.getCorrectOptionIndex() < 0 || q.getCorrectOptionIndex() >= q.getOptions().length) {
                throw new IllegalArgumentException("Question " + (i + 1) + " must have a valid correct answer selected.");
            }
        }
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    public Optional<Quiz> replace(String id, Quiz quiz) {
        if (repo.existsById(id)) {
            quiz.setId(id);
            return Optional.of(repo.save(quiz));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Deletes a quiz and removes its ID from the parent course's quiz list.
     *
     * @param id the quiz ID to delete
     * @return true if the quiz was deleted, false if not found
     */
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