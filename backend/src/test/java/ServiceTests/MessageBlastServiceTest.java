package ServiceTests;

import com.elearning.enums.Gender;
import com.elearning.enums.Role;
import com.elearning.model.Course;
import com.elearning.model.Enrollment;
import com.elearning.model.HomeAddress;
import com.elearning.model.Message;
import com.elearning.model.MessageBlast;
import com.elearning.model.Teacher;
import com.elearning.model.User;
import com.elearning.repository.CourseRepository;
import com.elearning.repository.EnrollmentRepository;
import com.elearning.repository.MessageBlastRepository;
import com.elearning.repository.MessageRepository;
import com.elearning.repository.PersonRepository;
import com.elearning.service.MessageBlastService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageBlastServiceTest {

    @Mock
    private MessageBlastRepository blastRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private MessageBlastService messageBlastService;

    private Teacher teacher;
    private User admin;
    private User student1;
    private User student2;

    @BeforeEach
    void setUp() {
        HomeAddress address = new HomeAddress("123 Main St", "Springfield", "IL", "62704");

        teacher = new Teacher("Jane", "Doe", new java.util.Date(), Gender.FEMALE, address,
                "teacher1", "password", "teacher1@example.com", Role.TEACHER, "Computer Science");
        teacher.setId("teacher-1");

        admin = new User("Admin", "User", new java.util.Date(), Gender.MALE, address,
                "admin1", "password", "admin@example.com", Role.ADMIN);
        admin.setId("admin-1");

        student1 = new User("Alice", "Johnson", new java.util.Date(), Gender.FEMALE, address,
                "student1", "password", "alice@example.com", Role.STUDENT);
        student1.setId("student-1");

        student2 = new User("Bob", "Smith", new java.util.Date(), Gender.MALE, address,
                "student2", "password", "bob@example.com", Role.STUDENT);
        student2.setId("student-2");
    }

    @Test
    void getAll_ShouldReturnAllBlasts() {
        MessageBlast blast = new MessageBlast("blast-1", "teacher-1", new ArrayList<>(), "Subject", LocalDateTime.now());
        when(blastRepository.findAll()).thenReturn(List.of(blast));

        List<MessageBlast> result = messageBlastService.getAll();

        assertEquals(1, result.size());
        verify(blastRepository, times(1)).findAll();
    }

    @Test
    void getById_ShouldDelegateToRepository() {
        MessageBlast blast = new MessageBlast("blast-1", "teacher-1", new ArrayList<>(), "Subject", LocalDateTime.now());
        when(blastRepository.findById("blast-1")).thenReturn(Optional.of(blast));

        Optional<MessageBlast> result = messageBlastService.getById("blast-1");

        assertTrue(result.isPresent());
        verify(blastRepository, times(1)).findById("blast-1");
    }

    @Test
    void getBySenderId_ShouldDelegateToRepository() {
        MessageBlast blast = new MessageBlast("blast-1", "teacher-1", new ArrayList<>(), "Subject", LocalDateTime.now());
        when(blastRepository.findBySenderId("teacher-1")).thenReturn(List.of(blast));

        List<MessageBlast> result = messageBlastService.getBySenderId("teacher-1");

        assertEquals(1, result.size());
        verify(blastRepository, times(1)).findBySenderId("teacher-1");
    }

    @Test
    void resolveRecipients_WhenTeachers_ShouldReturnTeacherIds() {
        when(personRepository.findByRole(Role.TEACHER)).thenReturn(List.of(teacher));

        List<String> result = messageBlastService.resolveRecipients("teachers");

        assertEquals(List.of("teacher-1"), result);
    }

    @Test
    void resolveRecipients_WhenAdmin_ShouldReturnAdminIds() {
        when(personRepository.findByRole(Role.ADMIN)).thenReturn(List.of(admin));

        List<String> result = messageBlastService.resolveRecipients("admin");

        assertEquals(List.of("admin-1"), result);
    }

    @Test
    void resolveRecipients_WhenTeachersAndAdmin_ShouldMergeWithoutDuplicates() {
        Teacher alsoAdminTeacher = new Teacher();
        alsoAdminTeacher.setId("teacher-1");
        User secondAdmin = new User();
        secondAdmin.setId("admin-2");

        when(personRepository.findByRole(Role.TEACHER)).thenReturn(List.of(teacher));
        when(personRepository.findByRole(Role.ADMIN)).thenReturn(List.of(admin, secondAdmin));

        List<String> result = messageBlastService.resolveRecipients("teachers and admin");

        assertEquals(3, result.size());
        assertTrue(result.contains("teacher-1"));
        assertTrue(result.contains("admin-1"));
        assertTrue(result.contains("admin-2"));
    }

    @Test
    void resolveRecipients_WhenCourseId_ShouldReturnEnrolledStudentIds() {
        when(courseRepository.existsById("course-1")).thenReturn(true);
        when(enrollmentRepository.findByCourseId("course-1")).thenReturn(List.of(
                new Enrollment("student-1", "course-1"),
                new Enrollment("student-2", "course-1")
        ));

        List<String> result = messageBlastService.resolveRecipients("course-1");

        assertEquals(List.of("student-1", "student-2"), result);
    }

    @Test
    void resolveRecipients_WhenInvalid_ShouldThrowException() {
        when(courseRepository.existsById("invalid-group")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> messageBlastService.resolveRecipients("invalid-group"));

        assertEquals("Invalid recipient: not a recognized group or course ID", ex.getMessage());
    }

    @Test
    void createBlastFromString_WhenNoRecipientsFound_ShouldThrowException() {
        when(personRepository.findByRole(Role.TEACHER)).thenReturn(List.of());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> messageBlastService.createBlastFromString("teacher-1", "teachers", "Subject", "Body"));

        assertEquals("No recipients found for: teachers", ex.getMessage());
    }

    @Test
    void createBlast_ShouldSaveBlastAndMessages() {
        AtomicInteger saveCount = new AtomicInteger();
        when(blastRepository.save(any(MessageBlast.class))).thenAnswer(invocation -> {
            MessageBlast blast = invocation.getArgument(0);
            if (saveCount.getAndIncrement() == 0) {
                return new MessageBlast("blast-1", blast.getSenderId(), blast.getMessageIds(), blast.getSubject(), blast.getSentAt());
            }
            return blast;
        });

        AtomicInteger messageCounter = new AtomicInteger(1);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            message.setId("msg-" + messageCounter.getAndIncrement());
            return message;
        });

        MessageBlast result = messageBlastService.createBlast(
                "teacher-1",
                List.of("student-1", "student-2"),
                "Course update",
                "Please complete lesson 2."
        );

        assertEquals("blast-1", result.getId());
        assertEquals(2, result.getMessageIds().size());
        assertEquals(List.of("msg-1", "msg-2"), result.getMessageIds());

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository, times(2)).save(messageCaptor.capture());
        assertTrue(messageCaptor.getAllValues().stream().allMatch(Message::isBlast));
        assertTrue(messageCaptor.getAllValues().stream().allMatch(message -> "blast-1".equals(message.getBlastId())));
        assertTrue(messageCaptor.getAllValues().stream().allMatch(message -> !message.isRead()));

        verify(blastRepository, times(2)).save(any(MessageBlast.class));
    }

    @Test
    void createBlastFromString_ShouldResolveRecipientsAndCreateBlast() {
        when(personRepository.findByRole(Role.TEACHER)).thenReturn(List.of(teacher));

        AtomicInteger saveCount = new AtomicInteger();
        when(blastRepository.save(any(MessageBlast.class))).thenAnswer(invocation -> {
            MessageBlast blast = invocation.getArgument(0);
            if (saveCount.getAndIncrement() == 0) {
                return new MessageBlast("blast-1", blast.getSenderId(), blast.getMessageIds(), blast.getSubject(), blast.getSentAt());
            }
            return blast;
        });
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message message = invocation.getArgument(0);
            message.setId("msg-1");
            return message;
        });

        MessageBlast result = messageBlastService.createBlastFromString("admin-1", "teachers", "Meeting", "Staff meeting tomorrow.");

        assertEquals("blast-1", result.getId());
        assertEquals(1, result.getMessageIds().size());
    }

    @Test
    void getBlastMessages_WhenBlastExists_ShouldReturnMessages() {
        MessageBlast blast = new MessageBlast("blast-1", "teacher-1", new ArrayList<>(List.of("msg-1", "msg-2")), "Subject", LocalDateTime.now());
        Message message1 = Message.builder().id("msg-1").receiverId("student-1").build();
        Message message2 = Message.builder().id("msg-2").receiverId("student-2").build();

        when(blastRepository.findById("blast-1")).thenReturn(Optional.of(blast));
        when(messageRepository.findByIdIn(blast.getMessageIds())).thenReturn(List.of(message1, message2));

        List<Message> result = messageBlastService.getBlastMessages("blast-1");

        assertEquals(2, result.size());
        verify(messageRepository, times(1)).findByIdIn(blast.getMessageIds());
    }

    @Test
    void getBlastRecipientIds_ShouldReturnReceiverIds() {
        MessageBlast blast = new MessageBlast("blast-1", "teacher-1", new ArrayList<>(List.of("msg-1", "msg-2")), "Subject", LocalDateTime.now());
        Message message1 = Message.builder().id("msg-1").receiverId("student-1").build();
        Message message2 = Message.builder().id("msg-2").receiverId("student-2").build();

        when(blastRepository.findById("blast-1")).thenReturn(Optional.of(blast));
        when(messageRepository.findByIdIn(blast.getMessageIds())).thenReturn(List.of(message1, message2));

        ArrayList<String> result = messageBlastService.getBlastRecipientIds("blast-1");

        assertEquals(new ArrayList<>(List.of("student-1", "student-2")), result);
    }

    @Test
    void delete_WhenBlastExists_ShouldDeleteAndReturnTrue() {
        when(blastRepository.existsById("blast-1")).thenReturn(true);

        boolean result = messageBlastService.delete("blast-1");

        assertTrue(result);
        verify(blastRepository, times(1)).deleteById("blast-1");
    }

    @Test
    void delete_WhenBlastMissing_ShouldReturnFalse() {
        when(blastRepository.existsById("blast-1")).thenReturn(false);

        boolean result = messageBlastService.delete("blast-1");

        assertFalse(result);
        verify(blastRepository, never()).deleteById(anyString());
    }
}