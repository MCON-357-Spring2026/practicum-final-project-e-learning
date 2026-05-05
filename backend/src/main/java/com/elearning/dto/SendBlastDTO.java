package com.elearning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendBlastDTO {
    private String recipients; // "teachers", "admin", "teachers and admin", or a courseId
    private String subject;
    private String body;
}
