package RepositoryTests;

import com.elearning.model.Course;
import com.elearning.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseRepositoryTest {

    @Mock
    private CourseRepository courseRepository;

    private Course testCourse;

    @BeforeEach
    void setUp() {
        testCourse = new Course("Java Programming", "Dr. Smith", "Computer Science", 3, 101, "Intro to Java");
        testCourse.setId("1");
    }

    @Test
    void findAll_ShouldReturnAllCourses() {
        // Arrange
        List<Course> courses = Arrays.asList(
            testCourse,
            new Course("Data Structures", "Dr. Johnson", "Computer Science", 4, 102, "Learn data structures")
        );
        when(courseRepository.findAll()).thenReturn(courses);

        // Act
        List<Course> result = courseRepository.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnCourse() {
        // Arrange
        when(courseRepository.findById("1")).thenReturn(Optional.of(testCourse));

        // Act
        Optional<Course> result = courseRepository.findById("1");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Java Programming", result.get().getTitle());
        verify(courseRepository, times(1)).findById("1");
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(courseRepository.findById("999")).thenReturn(Optional.empty());

        // Act
        Optional<Course> result = courseRepository.findById("999");

        // Assert
        assertFalse(result.isPresent());
        verify(courseRepository, times(1)).findById("999");
    }

    @Test
    void save_ShouldReturnSavedCourse() {
        // Arrange
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // Act
        Course result = courseRepository.save(testCourse);

        // Assert
        assertNotNull(result);
        assertEquals("Java Programming", result.getTitle());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void deleteById_ShouldInvokeDelete() {
        // Arrange
        doNothing().when(courseRepository).deleteById("1");

        // Act
        courseRepository.deleteById("1");

        // Assert
        verify(courseRepository, times(1)).deleteById("1");
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        // Arrange
        when(courseRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = courseRepository.existsById("1");

        // Assert
        assertTrue(result);
        verify(courseRepository, times(1)).existsById("1");
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        // Arrange
        when(courseRepository.existsById("999")).thenReturn(false);

        // Act
        boolean result = courseRepository.existsById("999");

        // Assert
        assertFalse(result);
        verify(courseRepository, times(1)).existsById("999");
    }

    @Test
    void getByInstructorId_ShouldReturnCoursesByInstructorId() {
        // Arrange
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.getByInstructorId("Dr. Smith")).thenReturn(courses);

        // Act
        List<Course> result = courseRepository.getByInstructorId("Dr. Smith");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dr. Smith", result.get(0).getInstructorId());
        verify(courseRepository, times(1)).getByInstructorId("Dr. Smith");
    }

    @Test
    void getByDepartment_ShouldReturnCoursesByDepartment() {
        // Arrange
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.getByDepartment("Computer Science")).thenReturn(courses);

        // Act
        List<Course> result = courseRepository.getByDepartment("Computer Science");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Computer Science", result.get(0).getDepartment());
        verify(courseRepository, times(1)).getByDepartment("Computer Science");
    }

    @Test
    void save_WithQuizIDs_ShouldReturnCourseWithQuizIDs() {
        // Arrange
        ArrayList<String> quizIDs = new ArrayList<>(Arrays.asList("q1", "q2"));
        testCourse.setQuizIDs(quizIDs);
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // Act
        Course result = courseRepository.save(testCourse);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getQuizIDs());
        assertEquals(2, result.getQuizIDs().size());
        assertTrue(result.getQuizIDs().contains("q1"));
        assertTrue(result.getQuizIDs().contains("q2"));
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void findById_WithQuizIDs_ShouldReturnCourseWithQuizIDs() {
        // Arrange
        ArrayList<String> quizIDs = new ArrayList<>(Arrays.asList("q1"));
        testCourse.setQuizIDs(quizIDs);
        when(courseRepository.findById("1")).thenReturn(Optional.of(testCourse));

        // Act
        Optional<Course> result = courseRepository.findById("1");

        // Assert
        assertTrue(result.isPresent());
        assertNotNull(result.get().getQuizIDs());
        assertEquals(1, result.get().getQuizIDs().size());
        assertTrue(result.get().getQuizIDs().contains("q1"));
        verify(courseRepository, times(1)).findById("1");
    }
}
