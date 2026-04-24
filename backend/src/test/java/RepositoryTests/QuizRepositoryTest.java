package RepositoryTests;

import com.elearning.model.Quiz;
import com.elearning.repository.QuizRepository;
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
public class QuizRepositoryTest {

    @Mock
    private QuizRepository quizRepository;

    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        testQuiz = new Quiz("course1", "Java Basics Quiz");
        testQuiz.setId("q1");
    }

    @Test
    void findAll_ShouldReturnAllQuizzes() {
        List<Quiz> quizzes = Arrays.asList(testQuiz, new Quiz("course2", "DS Quiz"));
        when(quizRepository.findAll()).thenReturn(quizzes);

        List<Quiz> result = quizRepository.findAll();

        assertEquals(2, result.size());
        verify(quizRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnQuiz() {
        when(quizRepository.findById("q1")).thenReturn(Optional.of(testQuiz));

        Optional<Quiz> result = quizRepository.findById("q1");

        assertTrue(result.isPresent());
        assertEquals("Java Basics Quiz", result.get().getTitle());
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        when(quizRepository.findById("999")).thenReturn(Optional.empty());

        Optional<Quiz> result = quizRepository.findById("999");

        assertFalse(result.isPresent());
    }

    @Test
    void save_ShouldReturnSavedQuiz() {
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);

        Quiz result = quizRepository.save(testQuiz);

        assertNotNull(result);
        assertEquals("Java Basics Quiz", result.getTitle());
    }

    @Test
    void deleteById_ShouldInvokeDelete() {
        doNothing().when(quizRepository).deleteById("q1");

        quizRepository.deleteById("q1");

        verify(quizRepository, times(1)).deleteById("q1");
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        when(quizRepository.existsById("q1")).thenReturn(true);

        assertTrue(quizRepository.existsById("q1"));
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        when(quizRepository.existsById("999")).thenReturn(false);

        assertFalse(quizRepository.existsById("999"));
    }

    @Test
    void getByCourseId_ShouldReturnQuizzesForCourse() {
        when(quizRepository.getByCourseId("course1")).thenReturn(List.of(testQuiz));

        List<Quiz> result = quizRepository.getByCourseId("course1");

        assertEquals(1, result.size());
        assertEquals("course1", result.get(0).getCourseId());
    }

    @Test
    void getByCourseId_WhenNoneExist_ShouldReturnEmptyList() {
        when(quizRepository.getByCourseId("noexist")).thenReturn(List.of());

        List<Quiz> result = quizRepository.getByCourseId("noexist");

        assertTrue(result.isEmpty());
    }
}
