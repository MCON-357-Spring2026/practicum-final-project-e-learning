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

/**
 * Service handling user authentication and registration.
 * Validates credentials, generates JWT tokens, and creates new user accounts.
 */
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

    /**
     * Authenticates a user and returns a JWT token with user info.
     *
     * @param request the login request containing username and password
     * @return a map containing the JWT token and user details
     * @throws IllegalArgumentException if the credentials are invalid
     */
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

    /**
     * Registers a new user (student or teacher) and returns a JWT token.
     * Students are assigned the STUDENT role; teachers are assigned the PENDING role.
     *
     * @param request the registration request containing credentials and personal info
     * @return a map containing the JWT token and new user details
     * @throws IllegalArgumentException if the username is already in use
     */
    public Map<String, Object> register(RegisterRequest request) {
        if (personRepository.findUserByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already in use");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User savedUser;

        if ("teacher".equalsIgnoreCase(request.getAccountType())) {
            com.elearning.model.Teacher teacher = new com.elearning.model.Teacher(
                    request.getFirstName(), request.getLastName(),
                    request.getDateOfBirth(), request.getGender(), request.getAddress(),
                    request.getUsername(), encodedPassword, request.getEmail(),
                    Role.PENDING);
            savedUser = (User) personRepository.save(teacher);
        } else {
            User user = new User(
                    request.getFirstName(), request.getLastName(),
                    request.getDateOfBirth(), request.getGender(), request.getAddress(),
                    request.getUsername(), encodedPassword, request.getEmail(),
                    Role.STUDENT);
            savedUser = (User) personRepository.save(user);
        }

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
