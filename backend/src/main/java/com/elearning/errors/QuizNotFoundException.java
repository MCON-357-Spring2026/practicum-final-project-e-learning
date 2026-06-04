package com.elearning.errors;

/**
 * Thrown when a quiz cannot be found by the given identifier.
 */
public class QuizNotFoundException extends RuntimeException {
    /**
     * @param message descriptive error message
     */
    public QuizNotFoundException(String message) {
        super(message);
    }

    public QuizNotFoundException() {
        super("Quiz not found");
    }
}
