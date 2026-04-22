package com.elearning.dto;

import com.elearning.model.Grade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class GradeResultDTO {
    private double score;
    private int total;
    private String feedback;
    private ArrayList<Integer> responses;
    private ArrayList<Integer> correctAnswers;

    public GradeResultDTO(Grade grade, int total, ArrayList<Integer> correctAnswers) {
        this.score = grade.getScore();
        this.total = total;
        this.feedback = grade.getFeedback();
        this.responses = grade.getResponses();
        this.correctAnswers = correctAnswers;
    }
}
