package ServiceTests;

import com.elearning.enums.Gender;
import com.elearning.enums.Role;
import com.elearning.model.HomeAddress;
import com.elearning.model.Person;
import com.elearning.model.Teacher;
import com.elearning.model.User;
import com.elearning.repository.PersonRepository;
import com.elearning.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Teacher testTeacher;
    private HomeAddress testAddress;

    @BeforeEach
    void setUp() {
        testAddress = new HomeAddress("123 Main St", "Springfield", "IL", "62704");
        testUser = new User("Alice", "Johnson", new Date(), Gender.FEMALE, testAddress,
                "alice1", "password123", null, Role.STUDENT);
        testUser.setId("u1");

        testTeacher = new Teacher("Dr. Jane", "Doe", new Date(), Gender.FEMALE, testAddress,
                "jane1", "instrpass1", null, Role.TEACHER, "Computer Science");
        testTeacher.setId("t1");
    }

    @Test
    void getAll_ShouldReturnOnlyUsers() {
        Person plainPerson = new Person("Bob", "Smith", new Date(), Gender.MALE, testAddress);
        List<Person> people = Arrays.asList(plainPerson, testUser, testTeacher);
        when(personRepository.findAll()).thenReturn(people);

        List<User> result = userService.getAll();

        assertEquals(2, result.size());
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void getById_WhenUserExists_ShouldReturnUser() {
        when(personRepository.findById("u1")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getById("u1");

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getFirstName());
        verify(personRepository, times(1)).findById("u1");
    }

    @Test
    void getById_WhenNotFound_ShouldReturnEmpty() {
        when(personRepository.findById("999")).thenReturn(Optional.empty());

        Optional<User> result = userService.getById("999");

        assertFalse(result.isPresent());
    }

    @Test
    void getById_WhenPersonNotUser_ShouldReturnEmpty() {
        Person plainPerson = new Person("Bob", "Smith", new Date(), Gender.MALE, testAddress);
        when(personRepository.findById("p1")).thenReturn(Optional.of(plainPerson));

        Optional<User> result = userService.getById("p1");

        assertFalse(result.isPresent());
    }

    @Test
    void getByUsername_ShouldDelegateToRepo() {
        when(personRepository.findUserByUsername("alice1")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getByUsername("alice1");

        assertTrue(result.isPresent());
        assertEquals("alice1", result.get().getUsername());
        verify(personRepository, times(1)).findUserByUsername("alice1");
    }

    @Test
    void getByRole_ShouldDelegateToRepo() {
        when(personRepository.findByRole(Role.STUDENT)).thenReturn(List.of(testUser));

        List<User> result = userService.getByRole(Role.STUDENT);

        assertEquals(1, result.size());
        verify(personRepository, times(1)).findByRole(Role.STUDENT);
    }

    @Test
    void getAllTeachers_ShouldReturnTeacherRole() {
        when(personRepository.findByRole(Role.TEACHER)).thenReturn(List.of(testTeacher));

        List<User> result = userService.getAllTeachers();

        assertEquals(1, result.size());
        verify(personRepository, times(1)).findByRole(Role.TEACHER);
    }

    @Test
    void getTeachersByDepartment_ShouldDelegateToRepo() {
        when(personRepository.findTeachersByDepartment("Computer Science")).thenReturn(List.of(testTeacher));

        List<Teacher> result = userService.getTeachersByDepartment("Computer Science");

        assertEquals(1, result.size());
        verify(personRepository, times(1)).findTeachersByDepartment("Computer Science");
    }

    @Test
    void create_ShouldSaveAndReturnUser() {
        when(personRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.create(testUser);

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        verify(personRepository, times(1)).save(any(User.class));
    }

    @Test
    void update_WhenUserExists_ShouldUpdateFields() {
        User updateData = new User("UpdatedAlice", "Johnson", new Date(), Gender.FEMALE, testAddress,
                "alice1", "newpass", null, Role.STUDENT);

        when(personRepository.findById("u1")).thenReturn(Optional.of(testUser));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<User> result = userService.update("u1", updateData);

        assertTrue(result.isPresent());
        assertEquals("UpdatedAlice", result.get().getFirstName());
        verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    void update_WhenNotFound_ShouldReturnEmpty() {
        when(personRepository.findById("999")).thenReturn(Optional.empty());

        Optional<User> result = userService.update("999", testUser);

        assertFalse(result.isPresent());
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void update_WhenTeacher_ShouldUpdateTeacherFields() {
        Teacher updateData = new Teacher("Dr. Jane", "Updated", new Date(), Gender.FEMALE, testAddress,
                "jane1", "instrpass1", "jane@university.edu", Role.TEACHER, "Mathematics");

        when(personRepository.findById("t1")).thenReturn(Optional.of(testTeacher));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<User> result = userService.update("t1", updateData);

        assertTrue(result.isPresent());
        assertInstanceOf(Teacher.class, result.get());
        assertEquals("Mathematics", ((Teacher) result.get()).getDepartment());
    }

    @Test
    void updateRole_WhenUserExists_ShouldUpdateRole() {
        when(personRepository.findById("u1")).thenReturn(Optional.of(testUser));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<User> result = userService.updateRole("u1", Role.ADMIN);

        assertTrue(result.isPresent());
        assertEquals(Role.ADMIN, result.get().getRole());
    }

    @Test
    void updateRole_WhenNotFound_ShouldReturnEmpty() {
        when(personRepository.findById("999")).thenReturn(Optional.empty());

        Optional<User> result = userService.updateRole("999", Role.ADMIN);

        assertFalse(result.isPresent());
    }

    @Test
    void replace_WhenUserExists_ShouldReplaceAndReturn() {
        when(personRepository.existsById("u1")).thenReturn(true);
        when(personRepository.save(any(User.class))).thenReturn(testUser);

        Optional<User> result = userService.replace("u1", testUser);

        assertTrue(result.isPresent());
        verify(personRepository, times(1)).save(any(User.class));
    }

    @Test
    void replace_WhenNotFound_ShouldReturnEmpty() {
        when(personRepository.existsById("999")).thenReturn(false);

        Optional<User> result = userService.replace("999", testUser);

        assertFalse(result.isPresent());
        verify(personRepository, never()).save(any(User.class));
    }

    @Test
    void delete_WhenUserExists_ShouldReturnTrue() {
        when(personRepository.existsById("u1")).thenReturn(true);
        doNothing().when(personRepository).deleteById("u1");

        boolean result = userService.delete("u1");

        assertTrue(result);
        verify(personRepository, times(1)).deleteById("u1");
    }

    @Test
    void delete_WhenNotFound_ShouldReturnFalse() {
        when(personRepository.existsById("999")).thenReturn(false);

        boolean result = userService.delete("999");

        assertFalse(result);
        verify(personRepository, never()).deleteById(anyString());
    }
}
