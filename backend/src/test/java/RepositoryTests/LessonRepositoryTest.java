package RepositoryTests;

import com.elearning.model.Lesson;
import com.elearning.repository.LessonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LessonRepositoryTest {

    @Mock
    private LessonRepository lessonRepository;

    private Lesson testLesson;

    @BeforeEach
    void setUp() {
        testLesson = new Lesson("Intro to Java", "Basics of Java programming");
        testLesson.setId("1");
        testLesson.setMinutes(45);
    }

    @Test
    void findAll_ShouldReturnAllLessons() {
        // Arrange
        List<Lesson> lessons = Arrays.asList(
            testLesson,
            new Lesson("Variables", "Understanding variables in Java")
        );
        when(lessonRepository.findAll()).thenReturn(lessons);

        // Act
        List<Lesson> result = lessonRepository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(lessonRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnLesson() {
        // Arrange
        when(lessonRepository.findById("1")).thenReturn(Optional.of(testLesson));

        // Act
        Optional<Lesson> result = lessonRepository.findById("1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Intro to Java", result.get().getTitle());
        verify(lessonRepository, times(1)).findById("1");
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(lessonRepository.findById("999")).thenReturn(Optional.empty());

        // Act
        Optional<Lesson> result = lessonRepository.findById("999");

        // Assert
        assertFalse(result.isPresent());
        verify(lessonRepository, times(1)).findById("999");
    }

    @Test
    void save_ShouldReturnSavedLesson() {
        // Arrange
        when(lessonRepository.save(any(Lesson.class))).thenReturn(testLesson);

        // Act
        Lesson result = lessonRepository.save(testLesson);

        // Assert
        assertNotNull(result);
        assertEquals("Intro to Java", result.getTitle());
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void deleteById_ShouldInvokeDelete() {
        // Arrange
        doNothing().when(lessonRepository).deleteById("1");

        // Act
        lessonRepository.deleteById("1");

        // Assert
        verify(lessonRepository, times(1)).deleteById("1");
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        // Arrange
        when(lessonRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = lessonRepository.existsById("1");

        // Assert
        assertTrue(result);
        verify(lessonRepository, times(1)).existsById("1");
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        // Arrange
        when(lessonRepository.existsById("999")).thenReturn(false);

        // Act
        boolean result = lessonRepository.existsById("999");

        // Assert
        assertFalse(result);
        verify(lessonRepository, times(1)).existsById("999");
    }
}
