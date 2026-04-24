package com.elearning.errors;

/**
 * Thrown when an invalid email format is provided.
 */
public class InvalidEmailError extends RuntimeException {
    /**
     * @param message descriptive error message
     */
    public InvalidEmailError(String message) {
        super(message);
    }

    public InvalidEmailError() {
        super("Invalid email format. Please provide a valid email address.");
    }
}
