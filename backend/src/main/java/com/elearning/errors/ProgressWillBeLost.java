package com.elearning.errors;

/**
 * Thrown to warn that an action will cause existing progress to be lost.
 */
public class ProgressWillBeLost extends RuntimeException {
    
    /**
     * @param message descriptive error message
     */
    public ProgressWillBeLost(String message) {
        super(message);
    }

    public ProgressWillBeLost() {
        super("This action will cause some progress to be lost. Are you sure you want to proceed?");
    }

}
