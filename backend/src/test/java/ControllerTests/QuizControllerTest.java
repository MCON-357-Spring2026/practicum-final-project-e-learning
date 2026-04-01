package ControllerTests;

import com.elearning.controller.QuizController;
import com.elearning.model.Quiz;
import com.elearning.service.QuizService;
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
public class QuizControllerTest {

    @Mock
    private QuizService quizService;

    @InjectMocks
    private QuizController quizController;

    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        testQuiz = new Quiz("course1", "Java Basics Quiz");
        testQuiz.setId("q1");
    }

    @Test
    void getAllQuizzes_ShouldReturnList() {
        List<Quiz> quizzes = Arrays.asList(testQuiz, new Quiz("course2", "DS Quiz"));
        when(quizService.getAll()).thenReturn(quizzes);

        ResponseEntity<List<Quiz>> response = quizController.getAllQuizzes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getQuizById_WhenExists_ShouldReturnQuiz() {
        when(quizService.getById("q1")).thenReturn(Optional.of(testQuiz));

        ResponseEntity<Quiz> response = quizController.getQuizById("q1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Java Basics Quiz", response.getBody().getTitle());
    }

    @Test
    void getQuizById_WhenNotFound_ShouldReturn404() {
        when(quizService.getById("999")).thenReturn(Optional.empty());

        ResponseEntity<Quiz> response = quizController.getQuizById("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getQuizzesByCourseId_ShouldReturnList() {
        when(quizService.getByCourseId("course1")).thenReturn(List.of(testQuiz));

        ResponseEntity<List<Quiz>> response = quizController.getQuizzesByCourseId("course1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createQuiz_ShouldReturnCreatedQuiz() {
        when(quizService.create(any(Quiz.class))).thenReturn(testQuiz);

        ResponseEntity<?> response = quizController.createQuiz(testQuiz);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(Quiz.class, response.getBody());
    }

    @Test
    void createQuiz_WhenInvalidArg_ShouldReturn400() {
        when(quizService.create(any(Quiz.class))).thenThrow(new IllegalArgumentException("Course not found"));

        ResponseEntity<?> response = quizController.createQuiz(testQuiz);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateQuiz_WhenExists_ShouldReturnUpdated() {
        when(quizService.update(eq("q1"), any(Quiz.class))).thenReturn(Optional.of(testQuiz));

        ResponseEntity<Quiz> response = quizController.updateQuiz("q1", testQuiz);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateQuiz_WhenNotFound_ShouldReturn404() {
        when(quizService.update(eq("999"), any(Quiz.class))).thenReturn(Optional.empty());

        ResponseEntity<Quiz> response = quizController.updateQuiz("999", testQuiz);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void replaceQuiz_WhenExists_ShouldReturnReplaced() {
        when(quizService.replace(eq("q1"), any(Quiz.class))).thenReturn(Optional.of(testQuiz));

        ResponseEntity<Quiz> response = quizController.replaceQuiz("q1", testQuiz);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void replaceQuiz_WhenNotFound_ShouldReturn404() {
        when(quizService.replace(eq("999"), any(Quiz.class))).thenReturn(Optional.empty());

        ResponseEntity<Quiz> response = quizController.replaceQuiz("999", testQuiz);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteQuiz_ShouldReturnNoContent() {
        when(quizService.delete("q1")).thenReturn(true);

        ResponseEntity<Void> response = quizController.deleteQuiz("q1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(quizService, times(1)).delete("q1");
    }
}
