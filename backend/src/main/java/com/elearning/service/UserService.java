package com.elearning.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.elearning.enums.Role;
import com.elearning.model.Teacher;
import com.elearning.model.User;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.PersonRepository;
import org.springframework.stereotype.Service;

/**
 * Service for managing {@link User} and {@link Teacher} entities.
 * Provides CRUD operations, role-based queries, and department filtering.
 */
@Service
public class UserService implements ServiceInterface<User> {

    private final PersonRepository repo;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public UserService(PersonRepository repo,
                       CourseRepository courseRepository,
                       EnrollmentRepository enrollmentRepository) {
        this.repo = repo;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Builds a courseId → course title map for the given course IDs.
     *
     * @param courseIds list of course IDs
     * @return map of courseId to course title
     */
    public Map<String, String> buildCourseMap(List<String> courseIds) {
        Map<String, String> map = new LinkedHashMap<>();
        if (courseIds == null) return map;
        for (String cid : courseIds) {
            courseRepository.findById(cid).ifPresent(c -> map.put(cid, c.getTitle()));
        }
        return map;
    }

    /**
     * Builds an enrollmentId → course title map for the given enrollment IDs.
     *
     * @param enrollmentIds list of enrollment IDs
     * @return map of enrollmentId to course title
     */
    public Map<String, String> buildEnrollmentMap(List<String> enrollmentIds) {
        Map<String, String> map = new LinkedHashMap<>();
        if (enrollmentIds == null) return map;
        for (String eid : enrollmentIds) {
            enrollmentRepository.findById(eid).ifPresent(e ->
                courseRepository.findById(e.getCourseId()).ifPresent(c -> map.put(eid, c.getTitle()))
            );
        }
        return map;
    }

    /** {@inheritDoc} */
    @Override
    public List<User> getAll() {
        return repo.findAll().stream()
                .filter(p -> p instanceof User)
                .map(p -> (User) p)
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<User> getById(String id) {
        return repo.findById(id)
                .filter(p -> p instanceof User)
                .map(p -> (User) p);
    }

    /**
     * Finds a user by their unique username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found
     */
    public Optional<User> getByUsername(String username) {
        return repo.findUserByUsername(username);
    }

    /**
     * Finds all users with the given role.
     *
     * @param role the role to filter by
     * @return list of users with the given role
     */
    public List<User> getByRole(Role role) {
        return repo.findByRole(role);
    }

    /**
     * Retrieves all users with the TEACHER role.
     *
     * @return list of teacher users
     */
    public List<User> getAllTeachers() {
        return repo.findByRole(Role.TEACHER);
    }

    /**
     * Retrieves all teachers in a given department.
     *
     * @param department the department name
     * @return list of teachers in the department
     */
    public List<Teacher> getTeachersByDepartment(String department) {
        return repo.findTeachersByDepartment(department);
    }

    /**
     * Updates a user's role.
     *
     * @param id   the user ID
     * @param role the new role
     * @return an Optional containing the updated user if found
     */
    public Optional<User> updateRole(String id, Role role) {
        return repo.findById(id)
                .filter(p -> p instanceof User)
                .map(p -> {
                    User user = (User) p;
                    user.setRole(role);
                    return (User) repo.save(user);
                });
    }

    /** {@inheritDoc} */
    @Override
    public User create(User user) {
        return repo.save(user);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<User> update(String id, User user) {
        return repo.findById(id)
                .filter(p -> p instanceof User)
                .map(p -> {
                    User existing = (User) p;
                    if (user.getFirstName() != null) existing.setFirstName(user.getFirstName());
                    if (user.getLastName() != null) existing.setLastName(user.getLastName());
                    if (user.getDateOfBirth() != null) existing.setDateOfBirth(user.getDateOfBirth());
                    if (user.getGender() != null) existing.setGender(user.getGender());
                    if (user.getAddress() != null) existing.setAddress(user.getAddress());
                    if (user.getUsername() != null) existing.setUsername(user.getUsername());
                    if (user.getPassword() != null) existing.setPassword(user.getPassword());
                    if (user.getRole() != null) existing.setRole(user.getRole());

                    if (existing instanceof Teacher && user instanceof Teacher) {
                        Teacher existingTeacher = (Teacher) existing;
                        Teacher incomingTeacher = (Teacher) user;
                        if (incomingTeacher.getDepartment() != null) existingTeacher.setDepartment(incomingTeacher.getDepartment());
                        if (incomingTeacher.getCourseIds() != null) existingTeacher.setCourseIds(incomingTeacher.getCourseIds());
                    }

                    return (User) repo.save(existing);
                });
    }

    /** {@inheritDoc} */
    @Override
    public Optional<User> replace(String id, User user) {
        if (repo.existsById(id)) {
            user.setId(id);
            return Optional.of(repo.save(user));
        }
        return Optional.empty();
    }

    /** {@inheritDoc} */
    @Override
    public boolean delete(String id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }
}
