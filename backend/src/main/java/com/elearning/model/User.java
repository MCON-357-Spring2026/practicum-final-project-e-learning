package com.elearning.model;

import java.util.ArrayList;
import java.util.Date;
import com.elearning.enums.Gender;
import com.elearning.enums.Role;
import com.elearning.repository.EnrollmentRepository;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a user in the e-learning platform. Extends {@link Person} with
 * authentication credentials (username/password), a {@link Role}, and a list
 * of enrollment IDs linking to {@link Enrollment} records.
 */
@Getter @Setter
public class User extends Person{

    @Indexed(unique = true)
    private String username;
    private String password;
    private String email;
    private Role role;
    private ArrayList<String> enrollmentIds;
    

    /**
     * Constructs a new User with personal details and credentials.
     *
     * @param fname    first name
     * @param lname    last name
     * @param dob      date of birth
     * @param gender   gender
     * @param address  home address
     * @param username unique username
     * @param password encoded password
     * @param email    email address (optional)
     * @param role     user role
     */
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
     *
     * @param enrollmentId   the enrollment ID to add
     * @param enrollmentRepo repository used to verify existence
     * @throws IllegalArgumentException if the enrollment does not exist
     */
    public void addEnrollmentId(String enrollmentId, EnrollmentRepository enrollmentRepo) {
        if (!enrollmentRepo.existsById(enrollmentId)) {
            throw new IllegalArgumentException("Enrollment not found with id: " + enrollmentId);
        }
        if (!enrollmentIds.contains(enrollmentId)) {
            enrollmentIds.add(enrollmentId);
        }
    }

    /**
     * Adds an enrollment ID without repository verification.
     *
     * @param enrollmentId the enrollment ID to add
     */
    public void addEnrollmentId(String enrollmentId) {
        if (!enrollmentIds.contains(enrollmentId)) {
            enrollmentIds.add(enrollmentId);
        }
    }

    /**
     * Removes an enrollment ID from this user's enrollment list.
     *
     * @param enrollmentId the enrollment ID to remove
     */
    public void removeEnrollmentId(String enrollmentId) {
        enrollmentIds.remove(enrollmentId);
    }
}