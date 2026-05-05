package ControllerTests;

import com.elearning.controller.MessageBlastController;
import com.elearning.dto.MessageBlastDTO;
import com.elearning.dto.MessageDTO;
import com.elearning.dto.SendBlastDTO;
import com.elearning.enums.Gender;
import com.elearning.enums.Role;
import com.elearning.model.HomeAddress;
import com.elearning.model.Message;
import com.elearning.model.MessageBlast;
import com.elearning.model.Teacher;
import com.elearning.model.User;
import com.elearning.repository.PersonRepository;
import com.elearning.security.AuthenticatedUser;
import com.elearning.service.MessageBlastService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageBlastControllerTest {

    @Mock
    private MessageBlastService blastService;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private MessageBlastController messageBlastController;

    private AuthenticatedUser principal;
    private Teacher sender;
    private User recipient1;
    private User recipient2;
    private MessageBlast blast;
    private List<Message> blastMessages;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser("teacher-1", "teacher1", "TEACHER");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of())
        );

        HomeAddress address = new HomeAddress("123 Main St", "Springfield", "IL", "62704");
        sender = new Teacher("Jane", "Doe", new java.util.Date(), Gender.FEMALE, address,
                "teacher1", "password", "teacher1@example.com", Role.TEACHER, "Computer Science");
        sender.setId("teacher-1");

        recipient1 = new User("Alice", "Johnson", new java.util.Date(), Gender.FEMALE, address,
                "student1", "password", "alice@example.com", Role.STUDENT);
        recipient1.setId("student-1");

        recipient2 = new User("Bob", "Smith", new java.util.Date(), Gender.MALE, address,
                "student2", "password", "bob@example.com", Role.STUDENT);
        recipient2.setId("student-2");

        blast = new MessageBlast(
                "blast-1",
                "teacher-1",
                new ArrayList<>(List.of("msg-1", "msg-2")),
                "Course update",
                LocalDateTime.of(2026, 5, 2, 9, 0)
        );

        blastMessages = List.of(
                Message.builder()
                        .id("msg-1")
                        .senderId("teacher-1")
                        .receiverId("student-1")
                        .subject("Course update")
                        .body("Please complete lesson two before Friday.")
                        .read(false)
                        .blast(true)
                        .blastId("blast-1")
                        .sentAt(LocalDateTime.of(2026, 5, 2, 9, 0))
                        .build(),
                Message.builder()
                        .id("msg-2")
                        .senderId("teacher-1")
                        .receiverId("student-2")
                        .subject("Course update")
                        .body("Please complete lesson two before Friday.")
                        .read(false)
                        .blast(true)
                        .blastId("blast-1")
                        .sentAt(LocalDateTime.of(2026, 5, 2, 9, 1))
                        .build()
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void stubPeople() {
        when(personRepository.findById("teacher-1")).thenReturn(Optional.of(sender));
        when(personRepository.findById("student-1")).thenReturn(Optional.of(recipient1));
        when(personRepository.findById("student-2")).thenReturn(Optional.of(recipient2));
    }

    @Test
    void createBlast_ShouldReturnBlastDTO() {
        SendBlastDTO request = new SendBlastDTO("teachers", "Course update", "Please complete lesson two before Friday.");
        stubPeople();
        when(blastService.createBlastFromString("teacher-1", "teachers", "Course update", "Please complete lesson two before Friday."))
                .thenReturn(blast);
        when(blastService.getBlastMessages("blast-1")).thenReturn(blastMessages);

        ResponseEntity<?> response = messageBlastController.createBlast(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(MessageBlastDTO.class, response.getBody());
        MessageBlastDTO body = (MessageBlastDTO) response.getBody();
        assertEquals("blast-1", body.getId());
        assertEquals("teacher-1", body.getSender().getPersonId());
        assertEquals(2, body.getRecipients().size());
    }

    @Test
    void createBlast_WhenInvalidRequest_ShouldReturnBadRequest() {
        SendBlastDTO request = new SendBlastDTO("invalid", "Course update", "Body");
        when(blastService.createBlastFromString(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid recipient"));

        ResponseEntity<?> response = messageBlastController.createBlast(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Map.of("error", "Invalid recipient"), response.getBody());
    }

    @Test
    void getMyBlasts_ShouldReturnBlastDTOsForCurrentSender() {
        stubPeople();
        when(blastService.getBySenderId("teacher-1")).thenReturn(List.of(blast));
        when(blastService.getBlastMessages("blast-1")).thenReturn(blastMessages);

        ResponseEntity<List<MessageBlastDTO>> response = messageBlastController.getMyBlasts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Course update", response.getBody().get(0).getSubject());
        verify(blastService, times(1)).getBySenderId("teacher-1");
    }

    @Test
    void getBlastById_WhenExists_ShouldReturnBlastDTO() {
        stubPeople();
        when(blastService.getById("blast-1")).thenReturn(Optional.of(blast));
        when(blastService.getBlastMessages("blast-1")).thenReturn(blastMessages);

        ResponseEntity<MessageBlastDTO> response = messageBlastController.getBlastById("blast-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("blast-1", response.getBody().getId());
    }

    @Test
    void getBlastById_WhenMissing_ShouldReturnNotFound() {
        when(blastService.getById("missing")).thenReturn(Optional.empty());

        ResponseEntity<MessageBlastDTO> response = messageBlastController.getBlastById("missing");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getBlastMessages_ShouldReturnMessageDTOs() {
        stubPeople();
        when(blastService.getBlastMessages("blast-1")).thenReturn(blastMessages);

        ResponseEntity<List<MessageDTO>> response = messageBlastController.getBlastMessages("blast-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Alice Johnson", response.getBody().get(0).getReceiverName());
    }

    @Test
    void deleteBlast_ShouldReturnNoContent() {
        when(blastService.delete("blast-1")).thenReturn(true);

        ResponseEntity<Void> response = messageBlastController.deleteBlast("blast-1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(blastService, times(1)).delete("blast-1");
    }
}