package ServiceTests;

import com.elearning.model.Course;
import com.elearning.repository.CourseRepository;
import com.elearning.service.CourseService;
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
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        testCourse = new Course("Java Programming", "Dr. Smith", "Computer Science", 3, 101);
    }

    @Test
    void getAll_ShouldReturnAllCourses() {
        // Arrange
        List<Course> courses = Arrays.asList(
            testCourse,
            new Course("Data Structures", "Dr. Johnson", "Computer Science", 4, 102)
        );
        when(courseRepository.findAll()).thenReturn(courses);

        // Act
        List<Course> result = courseService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void getById_WhenCourseExists_ShouldReturnCourse() {
        // Arrange
        when(courseRepository.findById("1")).thenReturn(Optional.of(testCourse));

        // Act
        Optional<Course> result = courseService.getById("1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Java Programming", result.get().getTitle());
        verify(courseRepository, times(1)).findById("1");
    }

    @Test
    void getById_WhenCourseNotFound_ShouldReturnEmpty() {
        // Arrange
        when(courseRepository.findById("999")).thenReturn(Optional.empty());

        // Act
        Optional<Course> result = courseService.getById("999");

        // Assert
        assertFalse(result.isPresent());
        verify(courseRepository, times(1)).findById("999");
    }

    @Test
    void create_ShouldSaveAndReturnCourse() {
        // Arrange
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // Act
        Course result = courseService.create(testCourse);

        // Assert
        assertNotNull(result);
        assertEquals("Java Programming", result.getTitle());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void update_WhenCourseExists_ShouldUpdateAndReturnCourse() {
        // Arrange
        Course updatedData = new Course("Java Advanced", "Dr. Smith", "Computer Science", 3, 101);
        when(courseRepository.findById("1")).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(updatedData);

        // Act
        Optional<Course> result = courseService.update("1", updatedData);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Java Advanced", result.get().getTitle());
        verify(courseRepository, times(1)).findById("1");
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void update_WhenCourseNotFound_ShouldReturnEmpty() {
        // Arrange
        when(courseRepository.findById("999")).thenReturn(Optional.empty());

        // Act
        Optional<Course> result = courseService.update("999", testCourse);

        // Assert
        assertFalse(result.isPresent());
        verify(courseRepository, times(1)).findById("999");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void replace_WhenCourseExists_ShouldReplaceAndReturnCourse() {
        // Arrange
        when(courseRepository.existsById("1")).thenReturn(true);
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // Act
        Optional<Course> result = courseService.replace("1", testCourse);

        // Assert
        assertTrue(result.isPresent());
        verify(courseRepository, times(1)).existsById("1");
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void replace_WhenCourseNotFound_ShouldReturnEmpty() {
        // Arrange
        when(courseRepository.existsById("999")).thenReturn(false);

        // Act
        Optional<Course> result = courseService.replace("999", testCourse);

        // Assert
        assertFalse(result.isPresent());
        verify(courseRepository, times(1)).existsById("999");
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    void delete_WhenCourseExists_ShouldReturnTrue() {
        // Arrange
        when(courseRepository.existsById("1")).thenReturn(true);
        doNothing().when(courseRepository).deleteById("1");

        // Act
        boolean result = courseService.delete("1");

        // Assert
        assertTrue(result);
        verify(courseRepository, times(1)).existsById("1");
        verify(courseRepository, times(1)).deleteById("1");
    }

    @Test
    void delete_WhenCourseNotFound_ShouldReturnFalse() {
        // Arrange
        when(courseRepository.existsById("999")).thenReturn(false);

        // Act
        boolean result = courseService.delete("999");

        // Assert
        assertFalse(result);
        verify(courseRepository, times(1)).existsById("999");
        verify(courseRepository, never()).deleteById(anyString());
    }

    @Test
    void update_WhenCourseExists_ShouldUpdateQuizIDs() {
        // Arrange
        ArrayList<String> quizIDs = new ArrayList<>(Arrays.asList("q1", "q2"));
        Course updatedData = new Course("Java Programming", "Dr. Smith", "Computer Science", 3, 101);
        updatedData.setQuizIDs(quizIDs);

        Course existingCourse = new Course("Java Programming", "Dr. Smith", "Computer Science", 3, 101);
        existingCourse.setQuizIDs(new ArrayList<>());

        when(courseRepository.findById("1")).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Course> result = courseService.update("1", updatedData);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getQuizIDs().size());
        assertTrue(result.get().getQuizIDs().contains("q1"));
        assertTrue(result.get().getQuizIDs().contains("q2"));
        verify(courseRepository, times(1)).findById("1");
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void update_WhenQuizIDsNull_ShouldNotUpdateQuizIDs() {
        // Arrange
        Course updatedData = new Course("Java Advanced", "Dr. Smith", "Computer Science", 3, 101);
        // quizIDs is null by default

        ArrayList<String> existingQuizIDs = new ArrayList<>(Arrays.asList("q1"));
        Course existingCourse = new Course("Java Programming", "Dr. Smith", "Computer Science", 3, 101);
        existingCourse.setQuizIDs(existingQuizIDs);

        when(courseRepository.findById("1")).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Course> result = courseService.update("1", updatedData);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getQuizIDs().size());
        assertTrue(result.get().getQuizIDs().contains("q1"));
    }
}
