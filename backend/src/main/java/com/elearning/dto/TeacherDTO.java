package com.elearning.dto;

import com.elearning.enums.Gender;
import com.elearning.enums.Role;
import com.elearning.model.HomeAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Map;

/**
 * Full data transfer object for a teacher, containing all fields
 * except username and password. Course and enrollment lists are
 * represented as maps for richer client-side display.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherDTO {
    private String id;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private Gender gender;
    private HomeAddress address;
    private String email;
    private Role role;
    private String department;
    /** Map of courseId to course title. */
    private Map<String, String> courses;
    /** Map of enrollmentId to course title. */
    private Map<String, String> enrollments;
}
