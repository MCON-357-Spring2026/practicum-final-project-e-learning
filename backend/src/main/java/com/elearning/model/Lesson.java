package com.elearning.model;

import com.elearning.errors.CourseNotFoundException;
import com.elearning.repository.CourseRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Getter @Setter 
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "lessons")
public class Lesson {

    @Id
    private String id;
    private String courseId;
    private String title;
    private String description;
    private int minutes;
    private ArrayList<String> resources;
    private ArrayList<String> media; // URLs to images and videos (e.g., S3, Cloudinary, or GridFS references)
    private String text;
    

    public Lesson(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void setCourseId(String courseId, CourseRepository courseRepo) {
        if (!courseRepo.existsById(courseId)) {
            throw new CourseNotFoundException("Course not found with id: " + courseId);
        }
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Lesson lesson = (Lesson) obj;
        return id != null && id.equals(lesson.id);
    }
}
