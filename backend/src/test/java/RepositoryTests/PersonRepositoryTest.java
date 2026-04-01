package RepositoryTests;

import com.elearning.enums.Gender;
import com.elearning.enums.Role;
import com.elearning.model.HomeAddress;
import com.elearning.model.Person;
import com.elearning.model.Teacher;
import com.elearning.model.User;
import com.elearning.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonRepositoryTest {

    @Mock
    private PersonRepository personRepository;

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
    void findAll_ShouldReturnAllPeople() {
        List<Person> people = Arrays.asList(testUser, testTeacher);
        when(personRepository.findAll()).thenReturn(people);

        List<Person> result = personRepository.findAll();

        assertEquals(2, result.size());
        verify(personRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenExists_ShouldReturnPerson() {
        when(personRepository.findById("u1")).thenReturn(Optional.of(testUser));

        Optional<Person> result = personRepository.findById("u1");

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getFirstName());
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        when(personRepository.findById("999")).thenReturn(Optional.empty());

        Optional<Person> result = personRepository.findById("999");

        assertFalse(result.isPresent());
    }

    @Test
    void save_ShouldReturnSavedPerson() {
        when(personRepository.save(any(Person.class))).thenReturn(testUser);

        Person result = personRepository.save(testUser);

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
    }

    @Test
    void deleteById_ShouldInvokeDelete() {
        doNothing().when(personRepository).deleteById("u1");

        personRepository.deleteById("u1");

        verify(personRepository, times(1)).deleteById("u1");
    }

    @Test
    void existsById_WhenExists_ShouldReturnTrue() {
        when(personRepository.existsById("u1")).thenReturn(true);

        assertTrue(personRepository.existsById("u1"));
    }

    @Test
    void existsById_WhenNotExists_ShouldReturnFalse() {
        when(personRepository.existsById("999")).thenReturn(false);

        assertFalse(personRepository.existsById("999"));
    }

    @Test
    void findUserByUsername_WhenExists_ShouldReturnUser() {
        when(personRepository.findUserByUsername("alice1")).thenReturn(Optional.of(testUser));

        Optional<User> result = personRepository.findUserByUsername("alice1");

        assertTrue(result.isPresent());
        assertEquals("alice1", result.get().getUsername());
    }

    @Test
    void findUserByUsername_WhenNotExists_ShouldReturnEmpty() {
        when(personRepository.findUserByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> result = personRepository.findUserByUsername("unknown");

        assertFalse(result.isPresent());
    }

    @Test
    void findByRole_ShouldReturnUsersWithRole() {
        when(personRepository.findByRole(Role.STUDENT)).thenReturn(List.of(testUser));

        List<User> result = personRepository.findByRole(Role.STUDENT);

        assertEquals(1, result.size());
        assertEquals(Role.STUDENT, result.get(0).getRole());
    }

    @Test
    void findTeachersByDepartment_ShouldReturnTeachers() {
        when(personRepository.findTeachersByDepartment("Computer Science")).thenReturn(List.of(testTeacher));

        List<Teacher> result = personRepository.findTeachersByDepartment("Computer Science");

        assertEquals(1, result.size());
        assertEquals("Computer Science", result.get(0).getDepartment());
    }
}
