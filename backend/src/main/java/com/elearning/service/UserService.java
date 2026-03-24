package com.elearning.service;

import java.util.List;
import java.util.Optional;

import com.elearning.enums.Role;
import com.elearning.model.Teacher;
import com.elearning.model.User;
import com.elearning.repository.PersonRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService implements ServiceInterface<User> {

    private final PersonRepository repo;

    public UserService(PersonRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<User> getAll() {
        return repo.findAll().stream()
                .filter(p -> p instanceof User)
                .map(p -> (User) p)
                .toList();
    }

    @Override
    public Optional<User> getById(String id) {
        return repo.findById(id)
                .filter(p -> p instanceof User)
                .map(p -> (User) p);
    }

    public Optional<User> getByUsername(String username) {
        return repo.findUserByUsername(username);
    }

    public List<User> getByRole(Role role) {
        return repo.findByRole(role);
    }

    public List<User> getAllTeachers() {
        return repo.findByRole(Role.TEACHER);
    }

    public List<Teacher> getTeachersByDepartment(String department) {
        return repo.findTeachersByDepartment(department);
    }

    public Optional<User> updateRole(String id, Role role) {
        return repo.findById(id)
                .filter(p -> p instanceof User)
                .map(p -> {
                    User user = (User) p;
                    user.setRole(role);
                    return (User) repo.save(user);
                });
    }

    @Override
    public User create(User user) {
        return repo.save(user);
    }

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

    @Override
    public Optional<User> replace(String id, User user) {
        if (repo.existsById(id)) {
            user.setId(id);
            return Optional.of(repo.save(user));
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(String id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }
}
