package com.elearning.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.elearning.enums.Gender;
import com.elearning.enums.Role;
import com.elearning.repository.CourseRepository;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a teacher (instructor) in the e-learning platform.
 * Extends {@link User} with a department and a list of course IDs they teach.
 */
@Getter @Setter
@NoArgsConstructor
public class Teacher extends User {

    private String department;
    private ArrayList<String> courseIds;

    public Teacher(String firstName, String lastName, Date dateOfBirth, Gender gender, HomeAddress address,
                   String username, String password, String email, Role role, 
                   String department, ArrayList<String> courseIds) {
        super(firstName, lastName, dateOfBirth, gender, address, 
            username, password, email, role);
        this.department = department;
        this.courseIds = courseIds;
    }

    public Teacher(String firstName, String lastName, Date dateOfBirth, Gender gender, HomeAddress address,
                   String username, String password, String email, Role role,
                   String department, String[] courseIds) {
         super(firstName, lastName, dateOfBirth, gender, address, 
            username, password, email, role);
        this.department = department;
        this.courseIds = new ArrayList<>(Arrays.asList(courseIds));
    }

    public Teacher(String firstName, String lastName, Date dateOfBirth, Gender gender, HomeAddress address,
                   String username, String password, String email, Role role,
                   String department) {
        super(firstName, lastName, dateOfBirth, gender, address, 
            username, password, email, role);
        this.department = department;
        this.courseIds = new ArrayList<>();
    }

    public Teacher(String firstName, String lastName, Date dateOfBirth, Gender gender, HomeAddress address,
                   String username, String password, String email, Role role) {
        super(firstName, lastName, dateOfBirth, gender, address,
            username, password, email, role);
        this.department = null;
        this.courseIds = new ArrayList<>();
    }

    /**
     * Adds a courseId after verifying the course's instructorId matches this teacher's ID.
     *
     * @param courseId   the course ID to add
     * @param courseRepo repository used to verify course ownership
     * @throws IllegalArgumentException if the course is not found or doesn't belong to this teacher
     */
    public void addCourseId(String courseId, CourseRepository courseRepo) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        if (!this.getId().equals(course.getInstructorId())) {
            throw new IllegalArgumentException("Course instructorId does not match this teacher's ID");
        }
        if (!courseIds.contains(courseId)) {
            courseIds.add(courseId);
        }
    }

    /**
     * Removes a course ID from this teacher's course list.
     *
     * @param courseId the course ID to remove
     */
    public void removeCourseId(String courseId) {
        courseIds.remove(courseId);
    }

}
