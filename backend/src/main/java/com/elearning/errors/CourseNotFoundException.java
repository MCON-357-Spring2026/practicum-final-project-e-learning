package com.elearning.errors;

/**
 * Thrown when a course cannot be found by the given identifier.
 */
public class CourseNotFoundException extends RuntimeException {
    /**
     * @param message descriptive error message
     */
    public CourseNotFoundException(String message) {
        super(message);
    }

    public CourseNotFoundException() {
        super("Course not found");
    }
}
