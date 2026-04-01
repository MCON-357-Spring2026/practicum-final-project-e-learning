package ServiceTests;

import com.elearning.model.Course;
import com.elearning.model.Quiz;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.QuizRepository;
import com.elearning.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private QuizService quizService;

    private Quiz testQuiz;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        testQuiz = new Quiz("course1", "Java Basics Quiz");
        testQuiz.setId("q1");

        testCourse = new Course("Java Programming", "inst1", "Computer Science", 3, 101);
        testCourse.setId("course1");
    }

    @Test
    void getAll_ShouldReturnAllQuizzes() {
        List<Quiz> quizzes = Arrays.asList(testQuiz, new Quiz("course2", "Data Structures Quiz"));
        when(quizRepository.findAll()).thenReturn(quizzes);

        List<Quiz> result = quizService.getAll();

        assertEquals(2, result.size());
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void getById_WhenExists_ShouldReturnQuiz() {
        when(quizRepository.findById("q1")).thenReturn(Optional.of(testQuiz));

        Optional<Quiz> result = quizService.getById("q1");

        assertTrue(result.isPresent());
        assertEquals("Java Basics Quiz", result.get().getTitle());
    }

    @Test
    void getById_WhenNotFound_ShouldReturnEmpty() {
        when(quizRepository.findById("999")).thenReturn(Optional.empty());

        Optional<Quiz> result = quizService.getById("999");

        assertFalse(result.isPresent());
    }

    @Test
    void getByCourseId_ShouldReturnQuizzes() {
        when(quizRepository.getByCourseId("course1")).thenReturn(List.of(testQuiz));

        List<Quiz> result = quizService.getByCourseId("course1");

        assertEquals(1, result.size());
        verify(quizRepository, times(1)).getByCourseId("course1");
    }

    @Test
    void create_WithValidCourse_ShouldSaveAndSyncCourse() {
        when(courseRepository.findById("course1")).thenReturn(Optional.of(testCourse));
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Quiz result = quizService.create(testQuiz);

        assertNotNull(result);
        verify(quizRepository, times(1)).save(any(Quiz.class));
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void create_WhenCourseNotFound_ShouldThrowException() {
        when(courseRepository.findById("course1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> quizService.create(testQuiz));
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    @Test
    void create_WithNullCourseId_ShouldSaveWithoutCourseSync() {
        Quiz noCourseQuiz = new Quiz(null, "Standalone Quiz");
        when(quizRepository.save(any(Quiz.class))).thenReturn(noCourseQuiz);

        Quiz result = quizService.create(noCourseQuiz);

        assertNotNull(result);
        verify(courseRepository, never()).findById(anyString());
    }

    @Test
    void update_WhenExists_ShouldUpdateFields() {
        Quiz updateData = new Quiz("course1", "Updated Quiz Title");
        when(quizRepository.findById("q1")).thenReturn(Optional.of(testQuiz));
        when(quizRepository.save(any(Quiz.class))).thenAnswer(inv -> inv.getArgument(0));

        Optional<Quiz> result = quizService.update("q1", updateData);

        assertTrue(result.isPresent());
        assertEquals("Updated Quiz Title", result.get().getTitle());
    }

    @Test
    void update_WhenNotFound_ShouldReturnEmpty() {
        when(quizRepository.findById("999")).thenReturn(Optional.empty());

        Optional<Quiz> result = quizService.update("999", testQuiz);

        assertFalse(result.isPresent());
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    @Test
    void replace_WhenExists_ShouldReplaceAndReturn() {
        when(quizRepository.existsById("q1")).thenReturn(true);
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);

        Optional<Quiz> result = quizService.replace("q1", testQuiz);

        assertTrue(result.isPresent());
        verify(quizRepository, times(1)).save(any(Quiz.class));
    }

    @Test
    void replace_WhenNotFound_ShouldReturnEmpty() {
        when(quizRepository.existsById("999")).thenReturn(false);

        Optional<Quiz> result = quizService.replace("999", testQuiz);

        assertFalse(result.isPresent());
        verify(quizRepository, never()).save(any(Quiz.class));
    }

    @Test
    void delete_WhenExists_ShouldCascadeRemoveFromCourse() {
        testCourse.addQuizID("q1");
        when(quizRepository.findById("q1")).thenReturn(Optional.of(testQuiz));
        when(courseRepository.findById("course1")).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);
        doNothing().when(quizRepository).deleteById("q1");

        boolean result = quizService.delete("q1");

        assertTrue(result);
        assertFalse(testCourse.getQuizIDs().contains("q1"));
        verify(courseRepository, times(1)).save(any(Course.class));
        verify(quizRepository, times(1)).deleteById("q1");
    }

    @Test
    void delete_WhenNotFound_ShouldReturnFalse() {
        when(quizRepository.findById("999")).thenReturn(Optional.empty());

        boolean result = quizService.delete("999");

        assertFalse(result);
        verify(quizRepository, never()).deleteById(anyString());
    }
}
