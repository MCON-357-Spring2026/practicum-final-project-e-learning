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
 * Data transfer object for a user, containing all fields except
 * username and password. Enrollments are represented as a map of
 * enrollmentId to course title.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private Gender gender;
    private HomeAddress address;
    private String email;
    private Role role;
    /** Map of enrollmentId to course title. */
    private Map<String, String> enrollments;
}
