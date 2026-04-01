package ServiceTests;

import com.elearning.model.Course;
import com.elearning.model.Lesson;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.LessonRepository;
import com.elearning.service.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private LessonService lessonService;

    private Lesson testLesson;

    @BeforeEach
    void setUp() {
        testLesson = new Lesson("Intro to Java", "Basics of Java programming");
        testLesson.setId("1");
        testLesson.setCourseId("course1");
        testLesson.setMinutes(45);
        testLesson.setResources(new ArrayList<>(Arrays.asList("slides.pdf", "demo.zip")));
    }

    @Test
    void getAll_ShouldReturnAllLessons() {
        // Arrange
        List<Lesson> lessons = Arrays.asList(
            testLesson,
            new Lesson("Variables", "Understanding variables in Java")
        );
        when(lessonRepository.findAll()).thenReturn(lessons);

        // Act
        List<Lesson> result = lessonService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(lessonRepository, times(1)).findAll();
    }

    @Test
    void getById_WhenLessonExists_ShouldReturnLesson() {
        // Arrange
        when(lessonRepository.findById("1")).thenReturn(Optional.of(testLesson));

        // Act
        Optional<Lesson> result = lessonService.getById("1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Intro to Java", result.get().getTitle());
        verify(lessonRepository, times(1)).findById("1");
    }

    @Test
    void getById_WhenLessonNotFound_ShouldReturnEmpty() {
        // Arrange
        when(lessonRepository.findById("999")).thenReturn(Optional.empty());

        // Act
        Optional<Lesson> result = lessonService.getById("999");

        // Assert
        assertFalse(result.isPresent());
        verify(lessonRepository, times(1)).findById("999");
    }

    @Test
    void create_ShouldSaveAndReturnLesson() {
        // Arrange
        Course course = new Course("Java Programming", "Dr. Smith", "Computer Science", 3, 101);
        course.setId("course1");
        when(courseRepository.findById("course1")).thenReturn(Optional.of(course));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(testLesson);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        Lesson result = lessonService.create(testLesson);

        // Assert
        assertNotNull(result);
        assertEquals("Intro to Java", result.getTitle());
        verify(lessonRepository, times(1)).save(any(Lesson.class));
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void create_WhenCourseNotFound_ShouldThrowException() {
        // Arrange
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> lessonService.create(testLesson));
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void update_WhenLessonExists_ShouldUpdateAndReturnLesson() {
        // Arrange
        Lesson updatedData = new Lesson("Advanced Java", "Deep dive into Java");
        updatedData.setMinutes(60);
        when(lessonRepository.findById("1")).thenReturn(Optional.of(testLesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(updatedData);

        // Act
        Optional<Lesson> result = lessonService.update("1", updatedData);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Advanced Java", result.get().getTitle());
        verify(lessonRepository, times(1)).findById("1");
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void update_WhenLessonNotFound_ShouldReturnEmpty() {
        // Arrange
        when(lessonRepository.findById("999")).thenReturn(Optional.empty());

        // Act
        Optional<Lesson> result = lessonService.update("999", testLesson);

        // Assert
        assertFalse(result.isPresent());
        verify(lessonRepository, times(1)).findById("999");
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void update_ShouldOnlyUpdateNonNullAndNonZeroFields() {
        // Arrange
        Lesson partialUpdate = new Lesson();
        partialUpdate.setTitle("Updated Title");
        // description is null, minutes is 0 — these should NOT be updated

        Lesson savedLesson = new Lesson("Updated Title", "Basics of Java programming");
        savedLesson.setMinutes(45);

        when(lessonRepository.findById("1")).thenReturn(Optional.of(testLesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(savedLesson);

        // Act
        Optional<Lesson> result = lessonService.update("1", partialUpdate);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Updated Title", result.get().getTitle());
        assertEquals("Basics of Java programming", result.get().getDescription());
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void replace_WhenLessonExists_ShouldReplaceAndReturnLesson() {
        // Arrange
        when(lessonRepository.existsById("1")).thenReturn(true);
        when(lessonRepository.save(any(Lesson.class))).thenReturn(testLesson);

        // Act
        Optional<Lesson> result = lessonService.replace("1", testLesson);

        // Assert
        assertTrue(result.isPresent());
        verify(lessonRepository, times(1)).existsById("1");
        verify(lessonRepository, times(1)).save(any(Lesson.class));
    }

    @Test
    void replace_WhenLessonNotFound_ShouldReturnEmpty() {
        // Arrange
        when(lessonRepository.existsById("999")).thenReturn(false);

        // Act
        Optional<Lesson> result = lessonService.replace("999", testLesson);

        // Assert
        assertFalse(result.isPresent());
        verify(lessonRepository, times(1)).existsById("999");
        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void delete_WhenLessonExists_ShouldCascadeRemoveFromCourse() {
        // Arrange
        Course course = new Course("Java Programming", "Dr. Smith", "Computer Science", 3, 101);
        course.setId("course1");
        course.addLessonID("1");

        when(lessonRepository.findById("1")).thenReturn(Optional.of(testLesson));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        doNothing().when(lessonRepository).deleteById("1");

        // Act
        boolean result = lessonService.delete("1");

        // Assert
        assertTrue(result);
        assertFalse(course.getLessonIDs().contains("1"));
        verify(courseRepository, times(1)).findById("course1");
        verify(courseRepository, times(1)).save(any(Course.class));
        verify(lessonRepository, times(1)).deleteById("1");
    }

    @Test
    void delete_WhenLessonNotFound_ShouldReturnFalse() {
        // Arrange
        when(lessonRepository.findById("999")).thenReturn(Optional.empty());

        // Act
        boolean result = lessonService.delete("999");

        // Assert
        assertFalse(result);
        verify(lessonRepository, times(1)).findById("999");
        verify(lessonRepository, never()).deleteById(anyString());
    }
}
