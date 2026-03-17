package com.elearning.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter 
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "enrollments")
@CompoundIndex(name = "student_course_idx", def = "{'studentId': 1, 'courseId': 1}", unique = true)
public class Enrollment {

    @Id
    private String id;
    private String studentId;
    private String courseId;
    private String progress; // e.g., "Not Started", "In Progress", "Completed"
    private ArrayList<String> completedLessons; // List of completed lesson IDs
    private Map<String, Double> completedQuizzes; // List of completed quiz IDs

    public Enrollment(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.progress = "Not Started";
        this.completedLessons = new ArrayList<>();
        this.completedQuizzes = Map.of();
    }

    public boolean isLessonCompleted(String lessonId) {
        return completedLessons.contains(lessonId);
    }

    public boolean isQuizCompleted(String quizId) {
        return completedQuizzes.containsKey(quizId);
    }

    public boolean isCourseCompleted(Course course) {
        if (course.getLessonIDs() == null || course.getQuizIDs() == null) {
            return false;
        }
        return completedLessons.containsAll(course.getLessonIDs()) &&
               completedQuizzes.keySet().containsAll(course.getQuizIDs());
    }

    public void updateProgress(Course course) {
        if (isCourseCompleted(course)) {
            this.progress = "Completed";
        } else if (!completedLessons.isEmpty() || !completedQuizzes.isEmpty()) {
            this.progress = "In Progress";
        } else {
            this.progress = "Not Started";
        }
    }

    public void markLessonAsCompleted(String lessonId, Course course) {
        if (!course.getLessonIDs().contains(lessonId)) {
            throw new IllegalArgumentException("Lesson does not belong to this course");
        }
        if (!completedLessons.contains(lessonId)) {
            completedLessons.add(lessonId);
            updateProgress(course);
        }
    }

    public void markQuizAsCompleted(String quizId, Course course, double score) {
        if (!course.getQuizIDs().contains(quizId)) {
            throw new IllegalArgumentException("Quiz does not belong to this course");
        }
        if (!completedQuizzes.containsKey(quizId)) {
            completedQuizzes.put(quizId, score);
            updateProgress(course);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Enrollment enrollment = (Enrollment) obj;
        if (studentId == null || courseId == null) return false;
        return studentId.equals(enrollment.studentId) && courseId.equals(enrollment.courseId);
    }
    
}