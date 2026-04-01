package com.elearning.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a student's enrollment in a course. Tracks lesson and quiz
 * completion progress. Uniquely identified by the combination of studentId
 * and courseId. Stored in the "enrollments" MongoDB collection.
 */
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
    private Map<String, Grade> completedQuizzes; // Quiz ID -> Grade

    /**
     * Constructs a new Enrollment with default "Not Started" progress.
     *
     * @param studentId the ID of the enrolled student
     * @param courseId  the ID of the course
     */
    public Enrollment(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.progress = "Not Started";
        this.completedLessons = new ArrayList<>();
        this.completedQuizzes = new HashMap<>();
    }

    /**
     * Checks whether a specific lesson has been completed.
     *
     * @param lessonId the lesson ID to check
     * @return true if the lesson is completed
     */
    public boolean isLessonCompleted(String lessonId) {
        return completedLessons.contains(lessonId);
    }

    /**
     * Checks whether a specific quiz has been completed.
     *
     * @param quizId the quiz ID to check
     * @return true if the quiz is completed
     */
    public boolean isQuizCompleted(String quizId) {
        return completedQuizzes.containsKey(quizId);
    }

    /**
     * Checks whether all lessons and quizzes in the course have been completed.
     *
     * @param course the course to check completion against
     * @return true if all lessons and quizzes are completed
     */
    public boolean isCourseCompleted(Course course) {
        if (course.getLessonIDs() == null || course.getQuizIDs() == null) {
            return false;
        }
        return completedLessons.containsAll(course.getLessonIDs()) &&
               completedQuizzes.keySet().containsAll(course.getQuizIDs());
    }

    /**
     * Updates the progress status based on current completion state.
     *
     * @param course the course used to determine overall completion
     */
    public void updateProgress(Course course) {
        if (isCourseCompleted(course)) {
            this.progress = "Completed";
        } else if (!completedLessons.isEmpty() || !completedQuizzes.isEmpty()) {
            this.progress = "In Progress";
        } else {
            this.progress = "Not Started";
        }
    }

    /**
     * Marks a lesson as completed and updates progress.
     *
     * @param lessonId the lesson ID to mark as completed
     * @param course   the course the lesson belongs to
     * @throws IllegalArgumentException if the lesson does not belong to this course
     */
    public void markLessonAsCompleted(String lessonId, Course course) {
        if (!course.getLessonIDs().contains(lessonId)) {
            throw new IllegalArgumentException("Lesson does not belong to this course");
        }
        if (!completedLessons.contains(lessonId)) {
            completedLessons.add(lessonId);
            updateProgress(course);
        }
    }

    /**
     * Marks a quiz as completed with a grade and updates progress.
     *
     * @param quizId the quiz ID to mark as completed
     * @param course the course the quiz belongs to
     * @param grade  the grade received on the quiz
     * @throws IllegalArgumentException if the quiz does not belong to this course
     */
    public void markQuizAsCompleted(String quizId, Course course, Grade grade) {
        if (!course.getQuizIDs().contains(quizId)) {
            throw new IllegalArgumentException("Quiz does not belong to this course");
        }
        if (!completedQuizzes.containsKey(quizId)) {
            completedQuizzes.put(quizId, grade);
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