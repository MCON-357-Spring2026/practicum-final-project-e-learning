package com.elearning.errors;

public class EnrollmentNotFoundException extends RuntimeException {
    public EnrollmentNotFoundException(String message) {
        super(message);
    }

    public EnrollmentNotFoundException() {
        super("Enrollment not found");
    }
}
