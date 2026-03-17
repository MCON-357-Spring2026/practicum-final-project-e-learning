package com.elearning.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;
import com.elearning.errors.InvalidEmailError;
import com.elearning.repository.EnrollmentRepository;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User extends Person{
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Indexed(unique = true)
    private String username;
    private String password;
    private String email;
    private String role;
    private ArrayList<String> enrollmentIds;
    

    public User(String fname, String lname, Date dob, Gender gender, HomeAddress address,
                String username, String password, String email, String role) {
        super(fname, lname, dob, gender, address);
        this.username = username;
        this.password = password;
        setEmail(email);
        this.role = role;
        this.enrollmentIds = new ArrayList<>();
    }

    public void setEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailError();
        }
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