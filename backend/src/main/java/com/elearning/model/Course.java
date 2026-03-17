package com.elearning.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.elearning.repository.LessonRepository;
import com.elearning.repository.QuizRepository;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Getter @Setter 
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "courses")
@CompoundIndex(name = "department_courseNum_idx", def = "{'department': 1, 'courseNum': 1}", unique = true)
public class Course {
    
    @Id
    private String id;
    private String title;
    private String description;
    private String instructorId;
    private String department;
    private int credits;
    private int courseNum;
    private ArrayList<String> lessonIDs;
    private ArrayList<String> quizIDs;
    private String image;


    public Course(String title, String instructorId, String department, int credits, int courseNum) {
        this.title = title;
        this.instructorId = instructorId;
        this.department = department;
        this.credits = credits;
        this.courseNum = courseNum;
        this.lessonIDs = new ArrayList<>();
        this.quizIDs = new ArrayList<>();
    }

    public void setLessonIDs(String[] lessonIDs) {
        this.lessonIDs = new ArrayList<>();
        for (String id : lessonIDs) {
            this.lessonIDs.add(id);
        }
    }

    public void setLessonIDs(ArrayList<String> lessonIDs) {
        this.lessonIDs = lessonIDs;
    }

    public void addLessonID(String lessonID) {
        this.lessonIDs.add(lessonID);
    }

    public void addLessonID(String lessonID, LessonRepository lessonRepo) {
        if (!lessonRepo.existsById(lessonID)) {
            throw new IllegalArgumentException("Lesson not found with id: " + lessonID);
        }
        this.lessonIDs.add(lessonID);
    }

    public void removeLessonID(String lessonID) {
        this.lessonIDs.remove(lessonID);
    }

    public void setQuizIDs(String[] quizIDs) {
        this.quizIDs = new ArrayList<>();
        for (String id : quizIDs) {
            this.quizIDs.add(id);
        }
    }

    public void setQuizIDs(ArrayList<String> quizIDs) {
        this.quizIDs = quizIDs;
    }

    public void addQuizID(String quizID) {
        this.quizIDs.add(quizID);
    }

    public void addQuizID(String quizID, QuizRepository quizRepo) {
        if (!quizRepo.existsById(quizID)) {
            throw new IllegalArgumentException("Quiz not found with id: " + quizID);
        }
        this.quizIDs.add(quizID);
    }

    public void removeQuizID(String quizID) {
        this.quizIDs.remove(quizID);
    }

    // Getters and setters can be added here

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return id != null && id.equals(course.id);
    }

}