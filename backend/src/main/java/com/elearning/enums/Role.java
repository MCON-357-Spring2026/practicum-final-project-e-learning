package com.elearning.enums;

/**
 * Represents the roles a user can have in the e-learning platform.
 */
public enum Role {
    /** A student enrolled in courses. */
    STUDENT,
    /** A user awaiting role approval. */
    PENDING,
    /** An instructor who creates and manages courses. */
    TEACHER,
    /** A platform administrator with full privileges. */
    ADMIN
}
