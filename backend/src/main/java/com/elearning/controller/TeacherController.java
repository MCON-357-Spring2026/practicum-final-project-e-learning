package com.elearning.controller;

import com.elearning.enums.Role;
import com.elearning.model.Teacher;
import com.elearning.model.User;
import com.elearning.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final UserService userService;

    public TeacherController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getAllTeachers() {
        return ResponseEntity.ok(userService.getAllTeachers());
    }

    @GetMapping("/admin")
    public ResponseEntity<List<User>> getAllAdmins() {
        return ResponseEntity.ok(userService.getByRole(Role.ADMIN));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<User>> getAllPending() {
        return ResponseEntity.ok(userService.getByRole(Role.PENDING));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable String id) {
        return userService.getById(id)
                .filter(u -> u instanceof Teacher)
                .map(u -> ResponseEntity.ok((Teacher) u))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<Teacher>> getTeachersByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(userService.getTeachersByDepartment(department));
    }

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

    @PatchMapping("/{id}/role")
    public ResponseEntity<?> updateTeacherRole(@PathVariable String id, @RequestBody Role role) {
        return userService.updateRole(id, role)
                .map(u -> ResponseEntity.ok((Object) u))
                .orElse(ResponseEntity.notFound().build());
    }

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

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable String id) {
        if (userService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
