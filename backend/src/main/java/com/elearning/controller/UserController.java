package com.elearning.controller;

import com.elearning.dto.ProfileDTO;
import com.elearning.dto.UserDTO;
import com.elearning.enums.Role;
import com.elearning.model.User;
import com.elearning.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * REST controller for user management endpoints at {@code /api/users}.
 * Provides CRUD operations for users with role-based access control.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private UserDTO toDTO(User u) {
        return new UserDTO(
                u.getId(), u.getFirstName(), u.getLastName(),
                u.getDateOfBirth(), u.getGender(), u.getAddress(),
                u.getEmail(), u.getRole(),
                userService.buildEnrollmentMap(u.getEnrollmentIds()));
    }

    /**
     * Retrieves all users.
     *
     * @return 200 with list of all users as DTOs
     */
    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> dtos = userService.getAll().stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Retrieves a user by their ID. Requires the requesting user to be the
     * owner or an ADMIN.
     *
     * @param id the user ID
     * @return 200 with the user DTO, or 404 if not found
     */
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        return userService.getById(id)
                .map(u -> ResponseEntity.ok(toDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username
     * @return 200 with the user DTO, or 404 if not found
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return userService.getByUsername(username)
                .map(u -> ResponseEntity.ok(toDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all users with a given role.
     *
     * @param role the role to filter by
     * @return 200 with list of matching user DTOs
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable Role role) {
        List<UserDTO> dtos = userService.getByRole(role).stream().map(this::toDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Retrieves the profile of a user by their ID.
     * Only the user themselves can access their own profile.
     *
     * @param id the user ID
     * @return 200 with the profile DTO, or 404 if not found
     */
    @PreAuthorize("#id == authentication.principal.id")
    @GetMapping("/{id}/profile")
    public ResponseEntity<ProfileDTO> getUserProfile(@PathVariable String id) {
        return userService.getById(id)
                .map(u -> ResponseEntity.ok(new ProfileDTO(u)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @return 201 with the created user DTO, 409 if username exists, or 400 on validation error
     */
    @PostMapping("/")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(userService.create(user)));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "A user with this username already exists."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Partially updates a user. Requires the requesting user to be the owner or an ADMIN.
     *
     * @param id   the user ID
     * @param user the fields to update
     * @return 200 with the updated user DTO, 404 if not found, or 409 on duplicate username
     */
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user) {
        try {
            return userService.update(id, user)
                    .map(u -> ResponseEntity.ok((Object) toDTO(u)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "A user with this username already exists."));
        }
    }

    /**
     * Fully replaces a user. Requires the requesting user to be the owner or an ADMIN.
     *
     * @param id   the user ID
     * @param user the replacement user data
     * @return 200 with the replaced user DTO, 404 if not found, or 409 on duplicate username
     */
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> replaceUser(@PathVariable String id, @RequestBody User user) {
        try {
            return userService.replace(id, user)
                    .map(u -> ResponseEntity.ok((Object) toDTO(u)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "A user with this username already exists."));
        }
    }

    /**
     * Deletes a user. Requires ADMIN role.
     *
     * @param id the user ID
     * @return 204 if deleted, or 404 if not found
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        if (userService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
