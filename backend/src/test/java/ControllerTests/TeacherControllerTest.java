package ControllerTests;

import com.elearning.controller.TeacherController;
import com.elearning.enums.Gender;
import com.elearning.enums.Role;
import com.elearning.model.HomeAddress;
import com.elearning.model.Teacher;
import com.elearning.model.User;
import com.elearning.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher testTeacher;
    private HomeAddress testAddress;

    @BeforeEach
    void setUp() {
        testAddress = new HomeAddress("789 Elm St", "Urbana", "IL", "61801");
        testTeacher = new Teacher("Dr. Jane", "Doe", new Date(), Gender.FEMALE, testAddress,
                "jane1", "instrpass1", null, Role.TEACHER, "Computer Science");
        testTeacher.setId("t1");
    }

    @Test
    void getAllTeachers_ShouldReturnList() {
        when(userService.getAllTeachers()).thenReturn(List.of(testTeacher));

        ResponseEntity<List<User>> response = teacherController.getAllTeachers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(userService, times(1)).getAllTeachers();
    }

    @Test
    void getAllAdmins_ShouldReturnAdminList() {
        User admin = new User("Admin", "User", new Date(), Gender.MALE, testAddress,
                "admin1", "pass", null, Role.ADMIN);
        when(userService.getByRole(Role.ADMIN)).thenReturn(List.of(admin));

        ResponseEntity<List<User>> response = teacherController.getAllAdmins();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(userService, times(1)).getByRole(Role.ADMIN);
    }

    @Test
    void getAllPending_ShouldReturnPendingList() {
        User pending = new User("Pending", "User", new Date(), Gender.MALE, testAddress,
                "pending1", "pass", null, Role.PENDING);
        when(userService.getByRole(Role.PENDING)).thenReturn(List.of(pending));

        ResponseEntity<List<User>> response = teacherController.getAllPending();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(userService, times(1)).getByRole(Role.PENDING);
    }

    @Test
    void getTeacherById_WhenExists_ShouldReturnTeacher() {
        when(userService.getById("t1")).thenReturn(Optional.of(testTeacher));

        ResponseEntity<Teacher> response = teacherController.getTeacherById("t1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dr. Jane", response.getBody().getFirstName());
    }

    @Test
    void getTeacherById_WhenNotFound_ShouldReturn404() {
        when(userService.getById("999")).thenReturn(Optional.empty());

        ResponseEntity<Teacher> response = teacherController.getTeacherById("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getTeacherById_WhenNotTeacher_ShouldReturn404() {
        User plainUser = new User("Alice", "Johnson", new Date(), Gender.FEMALE, testAddress,
                "alice1", "pass", null, Role.STUDENT);
        when(userService.getById("u1")).thenReturn(Optional.of(plainUser));

        ResponseEntity<Teacher> response = teacherController.getTeacherById("u1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getTeachersByDepartment_ShouldReturnList() {
        when(userService.getTeachersByDepartment("Computer Science")).thenReturn(List.of(testTeacher));

        ResponseEntity<List<Teacher>> response = teacherController.getTeachersByDepartment("Computer Science");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createTeacher_ShouldReturn201() {
        when(userService.create(any(Teacher.class))).thenReturn(testTeacher);

        ResponseEntity<?> response = teacherController.createTeacher(testTeacher);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createTeacher_WhenDuplicate_ShouldReturn409() {
        when(userService.create(any(Teacher.class))).thenThrow(new DuplicateKeyException("dup"));

        ResponseEntity<?> response = teacherController.createTeacher(testTeacher);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void createTeacher_WhenInvalidArg_ShouldReturn400() {
        when(userService.create(any(Teacher.class))).thenThrow(new IllegalArgumentException("bad"));

        ResponseEntity<?> response = teacherController.createTeacher(testTeacher);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateTeacherRole_WhenExists_ShouldReturnUpdated() {
        User updated = new User("Dr. Jane", "Doe", new Date(), Gender.FEMALE, testAddress,
                "jane1", "instrpass1", "jane@university.edu", Role.ADMIN);
        when(userService.updateRole("t1", Role.ADMIN)).thenReturn(Optional.of(updated));

        ResponseEntity<?> response = teacherController.updateTeacherRole("t1", Role.ADMIN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateTeacherRole_WhenNotFound_ShouldReturn404() {
        when(userService.updateRole("999", Role.ADMIN)).thenReturn(Optional.empty());

        ResponseEntity<?> response = teacherController.updateTeacherRole("999", Role.ADMIN);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateTeacher_WhenExists_ShouldReturnUpdated() {
        when(userService.update(eq("t1"), any(Teacher.class))).thenReturn(Optional.of(testTeacher));

        ResponseEntity<?> response = teacherController.updateTeacher("t1", testTeacher);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateTeacher_WhenNotFound_ShouldReturn404() {
        when(userService.update(eq("999"), any(Teacher.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = teacherController.updateTeacher("999", testTeacher);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateTeacher_WhenDuplicate_ShouldReturn409() {
        when(userService.update(eq("t1"), any(Teacher.class))).thenThrow(new DuplicateKeyException("dup"));

        ResponseEntity<?> response = teacherController.updateTeacher("t1", testTeacher);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void replaceTeacher_WhenExists_ShouldReturnReplaced() {
        when(userService.replace(eq("t1"), any(Teacher.class))).thenReturn(Optional.of(testTeacher));

        ResponseEntity<?> response = teacherController.replaceTeacher("t1", testTeacher);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void replaceTeacher_WhenNotFound_ShouldReturn404() {
        when(userService.replace(eq("999"), any(Teacher.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = teacherController.replaceTeacher("999", testTeacher);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteTeacher_WhenExists_ShouldReturn204() {
        when(userService.delete("t1")).thenReturn(true);

        ResponseEntity<Void> response = teacherController.deleteTeacher("t1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteTeacher_WhenNotFound_ShouldReturn404() {
        when(userService.delete("999")).thenReturn(false);

        ResponseEntity<Void> response = teacherController.deleteTeacher("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
