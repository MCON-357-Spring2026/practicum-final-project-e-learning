package ControllerTests;

import com.elearning.controller.CourseController;
import com.elearning.dto.CreateCourseDTO;
import com.elearning.model.Course;
import com.elearning.security.AuthenticatedUser;
import com.elearning.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private Course testCourse;
    private CreateCourseDTO testDTO;
    private AuthenticatedUser testPrincipal;

    @BeforeEach
    void setUp() {
        testCourse = new Course("Java Programming", "Dr. Smith", "Computer Science", 3, 101, "Intro to Java");
        testDTO = new CreateCourseDTO("Java Programming", "Intro to Java", "Computer Science", 3, 101, "");
        testPrincipal = new AuthenticatedUser("Dr. Smith", "instructor", "TEACHER");
    }

    @Test
    void getAllCourses_ShouldReturnListOfCourses() {
        // Arrange
        List<Course> courses = Arrays.asList(testCourse, 
            new Course("Data Structures", "Dr. Johnson", "Computer Science", 4, 102, "Learn data structures"));
        when(courseService.getAll()).thenReturn(courses);

        // Act
        ResponseEntity<List<Course>> response = courseController.getAllCourses();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(courseService, times(1)).getAll();
    }

    @Test
    void getCourseById_WhenCourseExists_ShouldReturnCourse() {
        // Arrange
        when(courseService.getById("1")).thenReturn(Optional.of(testCourse));

        // Act
        ResponseEntity<Course> response = courseController.getCourseById("1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Java Programming", response.getBody().getTitle());
        verify(courseService, times(1)).getById("1");
    }

    @Test
    void getCourseById_WhenCourseNotFound_ShouldReturn404() {
        // Arrange
        when(courseService.getById("999")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Course> response = courseController.getCourseById("999");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(courseService, times(1)).getById("999");
    }

    @Test
    void createCourse_ShouldReturnCreatedCourse() {
        // Arrange
        when(courseService.create(any(Course.class))).thenReturn(testCourse);

        // Act
        ResponseEntity<?> response = courseController.createCourse(testDTO, testPrincipal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Course.class, response.getBody());
        assertEquals("Java Programming", ((Course) response.getBody()).getTitle());
        verify(courseService, times(1)).create(any(Course.class));
    }

    @Test
    void createCourse_WhenDuplicate_ShouldReturn409() {
        // Arrange
        when(courseService.create(any(Course.class))).thenThrow(new DuplicateKeyException("duplicate"));

        // Act
        ResponseEntity<?> response = courseController.createCourse(testDTO, testPrincipal);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(courseService, times(1)).create(any(Course.class));
    }

    @Test
    void updateCourse_WhenCourseExists_ShouldReturnUpdatedCourse() {
        // Arrange
        Course updatedCourse = new Course("Java Advanced", "Dr. Smith", "Computer Science", 3, 101, "Advanced Java topics");
        when(courseService.update(eq("1"), any(Course.class))).thenReturn(Optional.of(updatedCourse));

        // Act
        ResponseEntity<?> response = courseController.updateCourse("1", updatedCourse);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Course.class, response.getBody());
        assertEquals("Java Advanced", ((Course) response.getBody()).getTitle());
        verify(courseService, times(1)).update(eq("1"), any(Course.class));
    }

    @Test
    void updateCourse_WhenCourseNotFound_ShouldReturn404() {
        // Arrange
        when(courseService.update(eq("999"), any(Course.class))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = courseController.updateCourse("999", testCourse);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(courseService, times(1)).update(eq("999"), any(Course.class));
    }

    @Test
    void replaceCourse_WhenCourseExists_ShouldReturnReplacedCourse() {
        // Arrange
        when(courseService.replace(eq("1"), any(Course.class))).thenReturn(Optional.of(testCourse));

        // Act
        ResponseEntity<?> response = courseController.replaceCourse("1", testCourse);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(courseService, times(1)).replace(eq("1"), any(Course.class));
    }

    @Test
    void replaceCourse_WhenCourseNotFound_ShouldReturn404() {
        // Arrange
        when(courseService.replace(eq("999"), any(Course.class))).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = courseController.replaceCourse("999", testCourse);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(courseService, times(1)).replace(eq("999"), any(Course.class));
    }

    @Test
    void deleteCourse_ShouldReturnNoContent() {
        // Arrange
        when(courseService.delete("1")).thenReturn(true);

        // Act
        ResponseEntity<Void> response = courseController.deleteCourse("1");

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(courseService, times(1)).delete("1");
    }

    @Test
    void createCourse_WithQuizIDs_ShouldReturnCourseWithQuizIDs() {
        // Arrange
        ArrayList<String> quizIDs = new ArrayList<>(Arrays.asList("q1", "q2"));
        testCourse.setQuizIDs(quizIDs);
        when(courseService.create(any(Course.class))).thenReturn(testCourse);

        // Act
        ResponseEntity<?> response = courseController.createCourse(testDTO, testPrincipal);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Course.class, response.getBody());
        assertEquals(2, ((Course) response.getBody()).getQuizIDs().size());
        verify(courseService, times(1)).create(any(Course.class));
    }

    @Test
    void updateCourse_WithQuizIDs_ShouldReturnUpdatedQuizIDs() {
        // Arrange
        ArrayList<String> quizIDs = new ArrayList<>(Arrays.asList("q1", "q3"));
        Course updatedCourse = new Course("Java Programming", "Dr. Smith", "Computer Science", 3, 101, "Intro to Java");
        updatedCourse.setQuizIDs(quizIDs);
        when(courseService.update(eq("1"), any(Course.class))).thenReturn(Optional.of(updatedCourse));

        // Act
        ResponseEntity<?> response = courseController.updateCourse("1", updatedCourse);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(Course.class, response.getBody());
        assertEquals(2, ((Course) response.getBody()).getQuizIDs().size());
        assertTrue(((Course) response.getBody()).getQuizIDs().contains("q3"));
        verify(courseService, times(1)).update(eq("1"), any(Course.class));
    }
}
