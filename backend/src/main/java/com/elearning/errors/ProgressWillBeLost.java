package com.elearning.errors;

public class ProgressWillBeLost extends RuntimeException {
    
    public ProgressWillBeLost(String message) {
        super(message);
    }

    public ProgressWillBeLost() {
        super("This action will cause some progress to be lost. Are you sure you want to proceed?");
    }

}
