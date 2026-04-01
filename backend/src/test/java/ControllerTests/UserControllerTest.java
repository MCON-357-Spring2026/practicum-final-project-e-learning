package ControllerTests;

import com.elearning.controller.UserController;
import com.elearning.enums.Gender;
import com.elearning.enums.Role;
import com.elearning.model.HomeAddress;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        HomeAddress address = new HomeAddress("123 Main St", "Springfield", "IL", "62704");
        testUser = new User("Alice", "Johnson", new Date(), Gender.FEMALE, address,
                "alice1", "password123", null, Role.STUDENT);
        testUser.setId("u1");
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        when(userService.getAll()).thenReturn(Arrays.asList(testUser));

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(userService, times(1)).getAll();
    }

    @Test
    void getUserById_WhenExists_ShouldReturnUser() {
        when(userService.getById("u1")).thenReturn(Optional.of(testUser));

        ResponseEntity<User> response = userController.getUserById("u1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Alice", response.getBody().getFirstName());
    }

    @Test
    void getUserById_WhenNotFound_ShouldReturn404() {
        when(userService.getById("999")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserById("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUserByUsername_WhenExists_ShouldReturnUser() {
        when(userService.getByUsername("alice1")).thenReturn(Optional.of(testUser));

        ResponseEntity<User> response = userController.getUserByUsername("alice1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("alice1", response.getBody().getUsername());
    }

    @Test
    void getUserByUsername_WhenNotFound_ShouldReturn404() {
        when(userService.getByUsername("unknown")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserByUsername("unknown");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getUsersByRole_ShouldReturnList() {
        when(userService.getByRole(Role.STUDENT)).thenReturn(List.of(testUser));

        ResponseEntity<List<User>> response = userController.getUsersByRole(Role.STUDENT);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createUser_ShouldReturn201() {
        when(userService.create(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = userController.createUser(testUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertInstanceOf(User.class, response.getBody());
    }

    @Test
    void createUser_WhenDuplicate_ShouldReturn409() {
        when(userService.create(any(User.class))).thenThrow(new DuplicateKeyException("dup"));

        ResponseEntity<?> response = userController.createUser(testUser);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void createUser_WhenInvalidArg_ShouldReturn400() {
        when(userService.create(any(User.class))).thenThrow(new IllegalArgumentException("bad"));

        ResponseEntity<?> response = userController.createUser(testUser);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void updateUser_WhenExists_ShouldReturnUpdated() {
        when(userService.update(eq("u1"), any(User.class))).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = userController.updateUser("u1", testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateUser_WhenNotFound_ShouldReturn404() {
        when(userService.update(eq("999"), any(User.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.updateUser("999", testUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateUser_WhenDuplicate_ShouldReturn409() {
        when(userService.update(eq("u1"), any(User.class))).thenThrow(new DuplicateKeyException("dup"));

        ResponseEntity<?> response = userController.updateUser("u1", testUser);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void replaceUser_WhenExists_ShouldReturnReplaced() {
        when(userService.replace(eq("u1"), any(User.class))).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = userController.replaceUser("u1", testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void replaceUser_WhenNotFound_ShouldReturn404() {
        when(userService.replace(eq("999"), any(User.class))).thenReturn(Optional.empty());

        ResponseEntity<?> response = userController.replaceUser("999", testUser);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteUser_WhenExists_ShouldReturn204() {
        when(userService.delete("u1")).thenReturn(true);

        ResponseEntity<Void> response = userController.deleteUser("u1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteUser_WhenNotFound_ShouldReturn404() {
        when(userService.delete("999")).thenReturn(false);

        ResponseEntity<Void> response = userController.deleteUser("999");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
