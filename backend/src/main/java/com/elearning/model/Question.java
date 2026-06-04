package com.elearning.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a single multiple-choice question within a {@link Quiz}.
 * Contains the question text, answer options, and the index of the correct option.
 */
@Getter @Setter 
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    private String questionText;
    private String[] options;
    private int correctOptionIndex;
    
}
