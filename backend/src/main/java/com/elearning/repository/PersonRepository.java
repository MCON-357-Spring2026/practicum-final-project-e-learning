package com.elearning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.elearning.enums.Role;
import com.elearning.model.Person;
import com.elearning.model.User;
import com.elearning.model.Teacher;

@Repository
public interface PersonRepository extends MongoRepository<Person, String>{

    public Optional<User> findUserByEmail(String email);

    public Optional<User> findUserByUsername(String username);

    public List<User> findByRole(Role role);

    public List<Teacher> findTeachersByDepartment(String department);
    
}
