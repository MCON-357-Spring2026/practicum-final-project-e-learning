package com.elearning.model;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the result of a quiz submission. Contains the quiz ID, student
 * responses, calculated score (0–100), and auto-generated feedback text.
 */
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Grade {

    private String quizId;
    private ArrayList<Integer> responses;
    private double score;
    private String feedback;

    /**
     * Constructs a Grade with a quiz ID, responses, and score.
     * Feedback is automatically generated based on the score.
     *
     * @param quizId    the ID of the quiz
     * @param responses the student's selected answer indices
     * @param score     the calculated score percentage (0–100)
     */
    public Grade(String quizId, ArrayList<Integer> responses, double score) {
        this.quizId = quizId;
        this.responses = responses;
        this.score = score;
        this.feedback = generateFeedback();
    }

    /**
     * Generates feedback text based on the score.
     *
     * @return a feedback string corresponding to the score range
     */
    private String generateFeedback() {
        if (score >= 90) {
            return "Excellent work!";
        } else if (score >= 75) {
            return "Good job, but there's room for improvement.";
        } else if (score >= 50) {
            return "Fair effort, but you should review the material.";
        } else {
            return "Needs improvement. Consider revisiting the course content.";
        }
    }
}
