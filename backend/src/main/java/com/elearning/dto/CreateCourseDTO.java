package com.elearning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.elearning.model.Course;

/**
 * Data transfer object for creating a new course.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseDTO {

    private String title;
    private String description;
    private String department;
    private int credits;
    private int courseNum;
    private String image;

    public Course toCourse(String instructorId) {
        Course course = new Course(this.title, instructorId, this.department, this.credits, this.courseNum, this.description);
        course.setImage(this.image);
        return course;
    }
}
