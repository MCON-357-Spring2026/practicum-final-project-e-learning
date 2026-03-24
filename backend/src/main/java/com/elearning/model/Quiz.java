package com.elearning.model;

import com.elearning.errors.CourseNotFoundException;
import com.elearning.repository.CourseRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;

@Getter @Setter 
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "quizzes")
public class Quiz {

    @Id
    private String id;
    private String courseId;
    private String title;
    private ArrayList<Question> questions;
    
    public Quiz(String courseId, String title) {
        this.courseId = courseId;
        this.title = title;
        this.questions = new ArrayList<>();
    }

    public Quiz(String courseId, String title, ArrayList<Question> questions) {
        this.courseId = courseId;
        this.title = title;
        this.questions = questions;
    }

    public Quiz(String courseId, String title, Question[] questions) {
        this.courseId = courseId;
        this.title = title;
        setQuestions(questions);
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public void setQuestions(Question[] questions) {
        this.questions = new ArrayList<>();
        for (Question q : questions) {
            this.questions.add(q);
        }
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public void addQuestion(String questionText, String[] options, int correctOptionIndex) {
        Question question = new Question(questionText, options, correctOptionIndex);
        this.questions.add(question);
    }

    public void removeQuestion(Question question) {
        this.questions.remove(question);
    }

    public void removeQuestion(int index) {
        if (index >= 0 && index < questions.size()) {
            this.questions.remove(index);
        }
    }

    public void removeQuestion(String questionText) {
        this.questions.removeIf(q -> q.getQuestionText().equals(questionText));
    }

    public void setCourseId(String courseId, CourseRepository courseRepo) {
        if (!courseRepo.existsById(courseId)) {
            throw new CourseNotFoundException("Course not found with id: " + courseId);
        }
        this.courseId = courseId;
    }

    public double calculateScore(ArrayList<Integer> responses) {
        if (responses.size() != questions.size()) {
            throw new IllegalArgumentException("Number of responses must match number of questions");
        }
        int correctCount = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getCorrectOptionIndex() == responses.get(i)) {
                correctCount++;
            }
        }
        return (double) correctCount / questions.size() * 100;
    }

    // Getters and setters can be added here
}
