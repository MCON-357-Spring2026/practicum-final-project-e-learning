package com.elearning.errors;

/**
 * Thrown when an enrollment cannot be found by the given identifier.
 */
public class EnrollmentNotFoundException extends RuntimeException {
    /**
     * @param message descriptive error message
     */
    public EnrollmentNotFoundException(String message) {
        super(message);
    }

    public EnrollmentNotFoundException() {
        super("Enrollment not found");
    }
}
