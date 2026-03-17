package com.elearning.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.elearning.repository.CourseRepository;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Teacher extends User {

    private String department;
    private ArrayList<String> courseIds;

    public Teacher(String fname, String lname, Date dob, Gender gender, HomeAddress address,
                   String username, String password, String email, String role, 
                   String department, ArrayList<String> courseIds) {
        super(fname, lname, dob, gender, address, 
            username, password, email, role);
        this.department = department;
        this.courseIds = courseIds;
    }

    public Teacher(String fname, String lname, Date dob, Gender gender, HomeAddress address,
                   String username, String password, String email, String role,
                   String department, String[] courseIds) {
         super(fname, lname, dob, gender, address, 
            username, password, email, role);
        this.department = department;
        this.courseIds = new ArrayList<>(Arrays.asList(courseIds));
    }

    public Teacher(String fname, String lname, Date dob, Gender gender, HomeAddress address,
                   String username, String password, String email, String role,
                   String department) {
        super(fname, lname, dob, gender, address, 
            username, password, email, role);
        this.department = department;
        this.courseIds = new ArrayList<>();
    }

    public Teacher(String fname, String lname, Date dob, Gender gender, HomeAddress address,
                   String username, String password, String email, String role) {
        super(fname, lname, dob, gender, address,
            username, password, email, role);
        this.department = null;
        this.courseIds = new ArrayList<>();
    }

    /**
     * Adds a courseId after verifying the course's instructorId matches this teacher's ID.
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

    public void removeCourseId(String courseId) {
        courseIds.remove(courseId);
    }

}
