package com.elearning.dto;

import com.elearning.model.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private String questionText;
    private String[] options;

    public QuestionDTO(Question question) {
        this.questionText = question.getQuestionText();
        this.options = question.getOptions();
    }
}
