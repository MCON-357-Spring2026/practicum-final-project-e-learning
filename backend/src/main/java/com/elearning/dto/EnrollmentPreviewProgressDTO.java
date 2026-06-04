package com.elearning.dto;

import com.elearning.model.Enrollment;
import com.elearning.model.Grade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentPreviewProgressDTO {
    private String enrollmentId;
    private String userId;
    private String courseId;
    private String progress;
    private ArrayList<String> completedLessons;
    private Map<String, Grade> completedQuizzes;

    public EnrollmentPreviewProgressDTO(Enrollment enrollment) {
        this.enrollmentId = enrollment.getId();
        this.userId = enrollment.getStudentId();
        this.courseId = enrollment.getCourseId();
        this.progress = enrollment.getProgress();
        this.completedLessons = enrollment.getCompletedLessons();
        this.completedQuizzes = enrollment.getCompletedQuizzes();
    }
}
