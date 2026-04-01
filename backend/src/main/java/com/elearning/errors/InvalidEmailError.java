package com.elearning.errors;

public class InvalidEmailError extends RuntimeException {
    public InvalidEmailError(String message) {
        super(message);
    }

    public InvalidEmailError() {
        super("Invalid email format. Please provide a valid email address.");
    }
}
