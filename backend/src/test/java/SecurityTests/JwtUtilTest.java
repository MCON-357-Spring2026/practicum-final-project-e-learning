package SecurityTests;

import com.elearning.security.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String validToken;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("ThisIsATestSecretKeyThatIsAtLeast32Bytes!!", 86400000L);
        validToken = jwtUtil.generateToken("testuser", "user123", "STUDENT");
    }

    @Test
    void generateToken_ShouldReturnNonNullToken() {
        assertNotNull(validToken);
        assertFalse(validToken.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String username = jwtUtil.extractUsername(validToken);
        assertEquals("testuser", username);
    }

    @Test
    void extractUserId_ShouldReturnCorrectUserId() {
        String userId = jwtUtil.extractUserId(validToken);
        assertEquals("user123", userId);
    }

    @Test
    void extractRole_ShouldReturnCorrectRole() {
        String role = jwtUtil.extractRole(validToken);
        assertEquals("STUDENT", role);
    }

    @Test
    void isTokenExpired_WithValidToken_ShouldReturnFalse() {
        assertFalse(jwtUtil.isTokenExpired(validToken));
    }

    @Test
    void isTokenExpired_WithExpiredToken_ShouldThrowOrReturnTrue() {
        JwtUtil shortLivedJwtUtil = new JwtUtil("ThisIsATestSecretKeyThatIsAtLeast32Bytes!!", 0L);
        String expiredToken = shortLivedJwtUtil.generateToken("testuser", "user123", "STUDENT");

        assertThrows(ExpiredJwtException.class, () -> shortLivedJwtUtil.isTokenExpired(expiredToken));
    }

    @Test
    void isTokenValid_WithCorrectUserDetails_ShouldReturnTrue() {
        UserDetails userDetails = new User("testuser", "password",
                List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));

        assertTrue(jwtUtil.isTokenValid(validToken, userDetails));
    }

    @Test
    void isTokenValid_WithWrongUsername_ShouldReturnFalse() {
        UserDetails userDetails = new User("wronguser", "password",
                List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));

        assertFalse(jwtUtil.isTokenValid(validToken, userDetails));
    }

    @Test
    void generateToken_WithDifferentRoles_ShouldPreserveRole() {
        String teacherToken = jwtUtil.generateToken("teacher1", "t1", "TEACHER");
        String adminToken = jwtUtil.generateToken("admin1", "a1", "ADMIN");

        assertEquals("TEACHER", jwtUtil.extractRole(teacherToken));
        assertEquals("ADMIN", jwtUtil.extractRole(adminToken));
    }

    @Test
    void extractClaims_WithInvalidToken_ShouldThrowException() {
        assertThrows(Exception.class, () -> jwtUtil.extractUsername("invalid.token.here"));
    }

    @Test
    void extractClaims_WithTamperedToken_ShouldThrowException() {
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";
        assertThrows(Exception.class, () -> jwtUtil.extractUsername(tamperedToken));
    }

    @Test
    void tokensForDifferentUsers_ShouldBeDifferent() {
        String token1 = jwtUtil.generateToken("user1", "id1", "STUDENT");
        String token2 = jwtUtil.generateToken("user2", "id2", "STUDENT");

        assertNotEquals(token1, token2);
    }
}
