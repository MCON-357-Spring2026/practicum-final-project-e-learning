package com.elearning.model;

import java.util.ArrayList;
import java.util.Date;
import com.elearning.enums.Gender;
import com.elearning.enums.Role;
import com.elearning.repository.EnrollmentRepository;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User extends Person{

    @Indexed(unique = true)
    private String username;
    private String password;
    private String email;
    private Role role;
    private ArrayList<String> enrollmentIds;
    

    public User(String fname, String lname, Date dob, Gender gender, HomeAddress address,
                String username, String password, String email, Role role) {
        super(fname, lname, dob, gender, address);
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.enrollmentIds = new ArrayList<>();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Adds an enrollment ID after verifying it exists in the enrollments collection.
     */
    public void addEnrollmentId(String enrollmentId, EnrollmentRepository enrollmentRepo) {
        if (!enrollmentRepo.existsById(enrollmentId)) {
            throw new IllegalArgumentException("Enrollment not found with id: " + enrollmentId);
        }
        if (!enrollmentIds.contains(enrollmentId)) {
            enrollmentIds.add(enrollmentId);
        }
    }

    public void addEnrollmentId(String enrollmentId) {
        if (!enrollmentIds.contains(enrollmentId)) {
            enrollmentIds.add(enrollmentId);
        }
    }

    public void removeEnrollmentId(String enrollmentId) {
        enrollmentIds.remove(enrollmentId);
    }
}