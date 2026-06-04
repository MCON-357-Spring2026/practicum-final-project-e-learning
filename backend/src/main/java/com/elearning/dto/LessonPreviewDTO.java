package com.elearning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonPreviewDTO {
    private String id;
    private String title;
    private String description;
    private int minutes;

    public LessonPreviewDTO(com.elearning.model.Lesson lesson) {
        this.id = lesson.getId();
        this.title = lesson.getTitle();
        this.description = lesson.getDescription();
        this.minutes = lesson.getMinutes();
    }
}
