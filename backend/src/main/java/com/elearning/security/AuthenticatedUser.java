package com.elearning.security;

public record AuthenticatedUser(String id, String username, String role) {
}
