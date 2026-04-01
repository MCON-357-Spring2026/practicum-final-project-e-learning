package com.elearning.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizSubmissionDTO {
    private String enrollmentId;
    private ArrayList<Integer> answers;
}
