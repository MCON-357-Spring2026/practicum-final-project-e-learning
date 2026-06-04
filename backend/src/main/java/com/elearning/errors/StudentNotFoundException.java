package com.elearning.errors;

/**
 * Thrown when a student cannot be found by the given identifier.
 */
public class StudentNotFoundException extends RuntimeException {
    /**
     * @param message descriptive error message
     */
    public StudentNotFoundException(String message) {
        super(message);
    }

    public StudentNotFoundException() {
        super("Student not found");
    }
}
