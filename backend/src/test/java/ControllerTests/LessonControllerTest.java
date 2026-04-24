package ControllerTests;

import com.elearning.controller.LessonController;
import com.elearning.model.Lesson;
import com.elearning.service.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LessonControllerTest {

    @Mock
    private LessonService lessonService;

    @InjectMocks
    private LessonController lessonController;

    private Lesson testLesson;

    @BeforeEach
    void setUp() {
        testLesson = new Lesson("Intro to Java", "Basics of Java programming");
        testLesson.setMinutes(45);
    }

    @Test
    void getAllLessons_ShouldReturnListOfLessons() {
        // Arrange
        List<Lesson> lessons = Arrays.asList(testLesson,
            new Lesson("Variables", "Understanding variables in Java"));
        when(lessonService.getAll()).thenReturn(lessons);

        // Act
        ResponseEntity<List<Lesson>> response = lessonController.getAllLessons();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(lessonService, times(1)).getAll();
    }

    @Test
    void getLessonById_WhenLessonExists_ShouldReturnLesson() {
        // Arrange
        when(lessonService.getById("1")).thenReturn(Optional.of(testLesson));

        // Act
        ResponseEntity<Lesson> response = lessonController.getLessonById("1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Intro to Java", response.getBody().getTitle());
        verify(lessonService, times(1)).getById("1");
    }

    @Test
    void getLessonById_WhenLessonNotFound_ShouldReturn404() {
        // Arrange
        when(lessonService.getById("999")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Lesson> response = lessonController.getLessonById("999");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(lessonService, times(1)).getById("999");
    }

    @Test
    void createLesson_ShouldReturnCreatedLesson() {
        // Arrange
        when(lessonService.create(any(Lesson.class))).thenReturn(testLesson);

        // Act
        ResponseEntity<Lesson> response = lessonController.createLesson(testLesson);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Intro to Java", response.getBody().getTitle());
        verify(lessonService, times(1)).create(any(Lesson.class));
    }

    @Test
    void updateLesson_WhenLessonExists_ShouldReturnUpdatedLesson() {
        // Arrange
        Lesson updatedLesson = new Lesson("Advanced Java", "Deep dive into Java");
        when(lessonService.update(eq("1"), any(Lesson.class))).thenReturn(Optional.of(updatedLesson));

        // Act
        ResponseEntity<Lesson> response = lessonController.updateLesson("1", updatedLesson);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Advanced Java", response.getBody().getTitle());
        verify(lessonService, times(1)).update(eq("1"), any(Lesson.class));
    }

    @Test
    void updateLesson_WhenLessonNotFound_ShouldReturn404() {
        // Arrange
        when(lessonService.update(eq("999"), any(Lesson.class))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Lesson> response = lessonController.updateLesson("999", testLesson);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(lessonService, times(1)).update(eq("999"), any(Lesson.class));
    }

    @Test
    void replaceLesson_WhenLessonExists_ShouldReturnReplacedLesson() {
        // Arrange
        when(lessonService.replace(eq("1"), any(Lesson.class))).thenReturn(Optional.of(testLesson));

        // Act
        ResponseEntity<Lesson> response = lessonController.replaceLesson("1", testLesson);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(lessonService, times(1)).replace(eq("1"), any(Lesson.class));
    }

    @Test
    void replaceLesson_WhenLessonNotFound_ShouldReturn404() {
        // Arrange
        when(lessonService.replace(eq("999"), any(Lesson.class))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Lesson> response = lessonController.replaceLesson("999", testLesson);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(lessonService, times(1)).replace(eq("999"), any(Lesson.class));
    }

    @Test
    void deleteLesson_ShouldReturnNoContent() {
        // Arrange
        when(lessonService.delete("1")).thenReturn(true);

        // Act
        ResponseEntity<Void> response = lessonController.deleteLesson("1");

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(lessonService, times(1)).delete("1");
    }
}
