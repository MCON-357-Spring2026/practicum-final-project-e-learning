package com.elearning.security;

/**
 * Immutable record representing the authenticated user principal stored in
 * the Spring Security context after JWT validation.
 *
 * @param id       the user's unique identifier
 * @param username the user's username
 * @param role     the user's role (e.g. STUDENT, TEACHER, ADMIN)
 */
public record AuthenticatedUser(String id, String username, String role) {
}
