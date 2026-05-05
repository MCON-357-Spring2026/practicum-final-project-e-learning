package RepositoryTests;

import com.elearning.model.MessageBlast;
import com.elearning.repository.MessageBlastRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageBlastRepositoryTest {

    @Mock
    private MessageBlastRepository messageBlastRepository;

    private MessageBlast testBlast;

    @BeforeEach
    void setUp() {
        testBlast = new MessageBlast(
                "blast-1",
                "teacher-1",
                new ArrayList<>(List.of("msg-1", "msg-2")),
                "Course update",
                LocalDateTime.of(2026, 5, 1, 10, 0)
        );
    }

    @Test
    void findAll_ShouldReturnAllBlasts() {
        when(messageBlastRepository.findAll()).thenReturn(List.of(testBlast));

        List<MessageBlast> result = messageBlastRepository.findAll();

        assertEquals(1, result.size());
        assertEquals("blast-1", result.get(0).getId());
        verify(messageBlastRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnBlast() {
        when(messageBlastRepository.findById("blast-1")).thenReturn(Optional.of(testBlast));

        Optional<MessageBlast> result = messageBlastRepository.findById("blast-1");

        assertTrue(result.isPresent());
        assertEquals("Course update", result.get().getSubject());
        verify(messageBlastRepository, times(1)).findById("blast-1");
    }

    @Test
    void findBySenderId_ShouldReturnMatchingBlasts() {
        when(messageBlastRepository.findBySenderId("teacher-1")).thenReturn(List.of(testBlast));

        List<MessageBlast> result = messageBlastRepository.findBySenderId("teacher-1");

        assertEquals(1, result.size());
        assertEquals("teacher-1", result.get(0).getSenderId());
        verify(messageBlastRepository, times(1)).findBySenderId("teacher-1");
    }

    @Test
    void save_ShouldReturnSavedBlast() {
        when(messageBlastRepository.save(any(MessageBlast.class))).thenReturn(testBlast);

        MessageBlast result = messageBlastRepository.save(testBlast);

        assertNotNull(result);
        assertEquals("blast-1", result.getId());
        verify(messageBlastRepository, times(1)).save(any(MessageBlast.class));
    }

    @Test
    void deleteById_ShouldInvokeDelete() {
        doNothing().when(messageBlastRepository).deleteById("blast-1");

        messageBlastRepository.deleteById("blast-1");

        verify(messageBlastRepository, times(1)).deleteById("blast-1");
    }

    @Test
    void existsById_ShouldReturnTrueWhenBlastExists() {
        when(messageBlastRepository.existsById("blast-1")).thenReturn(true);

        boolean result = messageBlastRepository.existsById("blast-1");

        assertTrue(result);
        verify(messageBlastRepository, times(1)).existsById("blast-1");
    }
}