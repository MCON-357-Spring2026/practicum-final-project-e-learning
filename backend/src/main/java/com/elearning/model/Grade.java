package com.elearning.model;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Grade {

    private String quizId;
    private ArrayList<Integer> responses;
    private double score;
    private String feedback;

    public Grade(String quizId, ArrayList<Integer> responses, double score) {
        this.quizId = quizId;
        this.responses = responses;
        this.score = score;
        this.feedback = generateFeedback();
    }

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
