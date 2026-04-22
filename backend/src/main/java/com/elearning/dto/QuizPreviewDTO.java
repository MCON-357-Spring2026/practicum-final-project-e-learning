package com.elearning.dto;

import com.elearning.model.Quiz;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizPreviewDTO {
    private String id;
    private String title;
    private int numberOfQuestions;

    public QuizPreviewDTO(Quiz quiz) {
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.numberOfQuestions = quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;
    }
}
