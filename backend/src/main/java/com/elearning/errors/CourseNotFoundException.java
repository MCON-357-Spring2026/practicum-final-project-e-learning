package com.elearning.errors;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(String message) {
        super(message);
    }

    public CourseNotFoundException() {
        super("Course not found");
    }
}
