package com.elearning.controller;

import com.elearning.dto.LimitedTeacherDTO;
import com.elearning.dto.TeacherDTO;
import com.elearning.enums.Role;
import com.elearning.model.Teacher;
import com.elearning.model.User;
import com.elearning.security.AuthenticatedUser;
import com.elearning.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * REST controller for teacher and role management endpoints at {@code /api/teachers}.
 * Provides CRUD operations for teachers and admin/pending role queries.
 */
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final UserService userService;

    public TeacherController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Checks whether the current user should see full teacher details.
     */
    private boolean isPrivileged(Authentication auth, String teacherId) {
        if (auth == null || auth.getPrincipal() == null) return false;
        AuthenticatedUser principal = (AuthenticatedUser) auth.getPrincipal();
        return "ADMIN".equals(principal.role()) || principal.id().equals(teacherId);
    }

    /**
     * Builds a courseId → course title map for the given course IDs.
     */
    private Map<String, String> buildCourseMap(List<String> courseIds) {
        return userService.buildCourseMap(courseIds);
    }

    /**
     * Builds an enrollmentId → course title map for the given enrollment IDs.
     */
    private Map<String, String> buildEnrollmentMap(List<String> enrollmentIds) {
        return userService.buildEnrollmentMap(enrollmentIds);
    }

    private TeacherDTO toFullDTO(Teacher t) {
        return new TeacherDTO(
                t.getId(), t.getFirstName(), t.getLastName(),
                t.getDateOfBirth(), t.getGender(), t.getAddress(),
                t.getEmail(), t.getRole(), t.getDepartment(),
                buildCourseMap(t.getCourseIds()),
                buildEnrollmentMap(t.getEnrollmentIds()));
    }

    private LimitedTeacherDTO toLimitedDTO(Teacher t) {
        return new LimitedTeacherDTO(
                t.getId(), t.getFirstName(), t.getLastName(),
                t.getDepartment(), buildCourseMap(t.getCourseIds()));
    }

    private Object toDTO(Teacher t, Authentication auth) {
        return isPrivileged(auth, t.getId()) ? toFullDTO(t) : toLimitedDTO(t);
    }

    /**
     * Retrieves all users with the TEACHER role.
     * Returns full DTOs for admins, limited DTOs for everyone else.
     *
     * @param auth the current authentication
     * @return 200 with list of teacher DTOs
     */
    @GetMapping("/")
    public ResponseEntity<?> getAllTeachers(Authentication auth) {
        List<?> dtos = userService.getAllTeachers().stream()
                .filter(u -> u instanceof Teacher)
                .map(u -> toDTO((Teacher) u, auth))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Retrieves all users with the ADMIN role.
     *
     * @return 200 with list of admins
     */
    @GetMapping("/admin")
    public ResponseEntity<List<User>> getAllAdmins() {
        return ResponseEntity.ok(userService.getByRole(Role.ADMIN));
    }

    /**
     * Retrieves all users with the PENDING role.
     *
     * @return 200 with list of pending users
     */
    @GetMapping("/pending")
    public ResponseEntity<List<User>> getAllPending() {
        return ResponseEntity.ok(userService.getByRole(Role.PENDING));
    }

    /**
     * Retrieves a teacher by ID.
     * Returns full DTO for admins or the teacher themselves, limited DTO otherwise.
     *
     * @param id   the teacher ID
     * @param auth the current authentication
     * @return 200 with a teacher DTO, or 404 if not found or not a Teacher
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTeacherById(@PathVariable String id, Authentication auth) {
        return userService.getById(id)
                .filter(u -> u instanceof Teacher)
                .map(u -> ResponseEntity.ok(toDTO((Teacher) u, auth)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Public preview endpoint returning limited teacher info.
     *
     * @param id the teacher ID
     * @return 200 with a LimitedTeacherDTO, or 404 if not found or not a Teacher
     */
    @GetMapping("/{id}/preview")
    public ResponseEntity<?> getTeacherPreview(@PathVariable String id) {
        return userService.getById(id)
                .filter(u -> u instanceof Teacher)
                .map(u -> ResponseEntity.ok(toLimitedDTO((Teacher) u)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all teachers in a given department.
     * Returns full DTOs for admins, limited DTOs for everyone else.
     *
     * @param department the department name
     * @param auth       the current authentication
     * @return 200 with list of teacher DTOs
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<?> getTeachersByDepartment(@PathVariable String department, Authentication auth) {
        List<?> dtos = userService.getTeachersByDepartment(department).stream()
                .map(t -> toDTO(t, auth))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Creates a new teacher.
     *
     * @param teacher the teacher to create
     * @return 201 with the created teacher, 409 if username exists, or 400 on validation error
     */
    @PostMapping("/")
    public ResponseEntity<?> createTeacher(@RequestBody Teacher teacher) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(teacher));
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "A user with this username already exists."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Sets a teacher's department. Requires the requesting user to be the teacher or an ADMIN.
     *
     * @param id         the teacher ID
     * @param department the department name
     * @return 200 with the updated teacher, or 404 if not found or not a Teacher
     */
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @PatchMapping("/{id}/department")
    public ResponseEntity<?> updateTeacherDepartment(@PathVariable String id, @RequestBody String department) {
        return userService.getById(id)
                .filter(u -> u instanceof Teacher)
                .map(u -> {
                    Teacher t = (Teacher) u;
                    t.setDepartment(department);
                    return ResponseEntity.ok((Object) userService.create(t));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Updates a teacher's role to the given value. Requires ADMIN role.
     *
     * @param id   the teacher ID
     * @param role the new role
     * @return 200 with the updated user, or 404 if not found
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<?> updateTeacherRole(@PathVariable String id, @RequestBody Role role) {
        return userService.updateRole(id, role)
                .map(u -> ResponseEntity.ok((Object) u))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Promotes a user to the TEACHER role. Requires ADMIN role.
     *
     * @param id the user ID
     * @return 200 with the updated user, or 404 if not found
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/promote/teacher")
    public ResponseEntity<?> promoteToTeacher(@PathVariable String id) {
        return userService.updateRole(id, Role.TEACHER)
                .map(u -> ResponseEntity.ok((Object) u))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Promotes a user to the ADMIN role. Requires ADMIN role.
     *
     * @param id the user ID
     * @return 200 with the updated user, or 404 if not found
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/promote/admin")
    public ResponseEntity<?> promoteToAdmin(@PathVariable String id) {
        return userService.updateRole(id, Role.ADMIN)
                .map(u -> ResponseEntity.ok((Object) u))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Partially updates a teacher. Requires the requesting user to be the owner or an ADMIN.
     *
     * @param id      the teacher ID
     * @param teacher the fields to update
     * @return 200 with the updated teacher, 404 if not found, or 409 on duplicate username
     */
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTeacher(@PathVariable String id, @RequestBody Teacher teacher) {
        try {
            return userService.update(id, teacher)
                    .map(u -> ResponseEntity.ok((Object) u))
                    .orElse(ResponseEntity.notFound().build());
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "A user with this username already exists."));
        }
    }

    /**
     * Fully replaces a teacher. Requires the requesting user to be the owner or an ADMIN.
     *
     * @param id      the teacher ID
     * @param teacher the replacement teacher data
     * @return 200 with the replaced teacher, 404 if not found, or 409 on duplicate username
     */
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> replaceTeacher(@PathVariable String id, @RequestBody Teacher teacher) {
        try {
            return userService.replace(id, teacher)
                    .map(u -> ResponseEntity.ok((Object) u))
                    .orElse(ResponseEntity.notFound().build());
        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "A user with this username already exists."));
        }
    }

    /**
     * Deletes a teacher. Requires ADMIN role.
     *
     * @param id the teacher ID
     * @return 204 if deleted, or 404 if not found
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable String id) {
        if (userService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
