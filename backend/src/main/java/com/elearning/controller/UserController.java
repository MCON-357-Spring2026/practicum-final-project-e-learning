package com.elearning.controller;

import com.elearning.model.Person;
import com.elearning.repository.PersonRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final PersonRepository personRepository;

    public UserController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getUserById(@PathVariable String id) {
        return personRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
