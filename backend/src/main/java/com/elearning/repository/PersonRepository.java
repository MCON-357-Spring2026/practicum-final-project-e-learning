package com.elearning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.elearning.enums.Role;
import com.elearning.model.Person;
import com.elearning.model.User;
import com.elearning.model.Teacher;

/**
 * MongoDB repository for {@link Person} entities and its subclasses
 * ({@link User}, {@link Teacher}). Provides queries for user lookup by
 * username, role, and teacher department.
 */
@Repository
public interface PersonRepository extends MongoRepository<Person, String>{

    /**
     * Finds a user by their unique username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    public Optional<User> findUserByUsername(String username);

    /**
     * Finds all users with the given role.
     *
     * @param role the role to filter by
     * @return list of users with the given role
     */
    public List<User> findByRole(Role role);

    /**
     * Finds all teachers in a given department.
     *
     * @param department the department name
     * @return list of teachers in the department
     */
    public List<Teacher> findTeachersByDepartment(String department);
    
}
