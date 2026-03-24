package com.elearning.service;

import com.elearning.dto.LoginRequest;
import com.elearning.dto.RegisterRequest;
import com.elearning.enums.Role;
import com.elearning.model.User;
import com.elearning.repository.PersonRepository;
import com.elearning.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final PersonRepository personRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(PersonRepository personRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> login(LoginRequest request) {
        User user = personRepository.findUserByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().name());

        return Map.of(
                "token", token,
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "role", user.getRole().name()
                )
        );
    }

    public Map<String, Object> register(RegisterRequest request) {
        if (personRepository.findUserByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already in use");
        }

        String[] nameParts = request.getName() != null ? request.getName().split(" ", 2) : new String[]{"User", ""};
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        User user = new User(firstName, lastName, null, null, null,
                request.getUsername(), passwordEncoder.encode(request.getPassword()),
                null, Role.STUDENT);

        User savedUser = (User) personRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getId(), savedUser.getRole().name());

        return Map.of(
                "token", token,
                "user", Map.of(
                        "id", savedUser.getId(),
                        "username", savedUser.getUsername(),
                        "role", savedUser.getRole().name()
                )
        );
    }
}
