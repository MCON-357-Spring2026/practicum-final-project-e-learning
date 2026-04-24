package com.elearning.dto;

import com.elearning.enums.Gender;
import com.elearning.model.HomeAddress;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

/**
 * Data transfer object for user registration requests.
 * Contains account credentials, personal information, and account type.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private Gender gender;
    private HomeAddress address;
    private String email;
    private String accountType; // "student" or "teacher"
}
