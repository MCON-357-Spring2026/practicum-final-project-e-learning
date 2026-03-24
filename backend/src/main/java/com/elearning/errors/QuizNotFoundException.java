package com.elearning.errors;

public class QuizNotFoundException extends RuntimeException {
    public QuizNotFoundException(String message) {
        super(message);
    }

    public QuizNotFoundException() {
        super("Quiz not found");
    }
}
