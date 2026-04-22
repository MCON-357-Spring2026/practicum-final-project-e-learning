package com.elearning.dto;

import com.elearning.model.Quiz;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {
    private String id;
    private String courseId;
    private String title;
    private ArrayList<QuestionDTO> questions;

    public QuizDTO(Quiz quiz) {
        this.id = quiz.getId();
        this.courseId = quiz.getCourseId();
        this.title = quiz.getTitle();
        this.questions = quiz.getQuestions() == null
                ? new ArrayList<>()
                : quiz.getQuestions().stream()
                        .map(QuestionDTO::new)
                        .collect(Collectors.toCollection(ArrayList::new));
    }
}
