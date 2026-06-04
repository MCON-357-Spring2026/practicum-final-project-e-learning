package com.elearning.dto;

import com.elearning.enums.Department;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Lightweight data transfer object exposing only a teacher's name,
 * department, and course map.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LimitedTeacherDTO {
    private String id;
    private String firstName;
    private String lastName;
    private Department department;
    private Map<String, String> courses;
}
