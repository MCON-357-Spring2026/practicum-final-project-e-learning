package SecurityTests;

import com.elearning.security.JwtFilter;
import com.elearning.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    private JwtUtil jwtUtil;
    private JwtFilter jwtFilter;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("ThisIsATestSecretKeyThatIsAtLeast32Bytes!!", 86400000L);
        jwtFilter = new JwtFilter(jwtUtil);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilter_WithValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        String token = jwtUtil.generateToken("testuser", "user123", "STUDENT");
        request.addHeader("Authorization", "Bearer " + token);

        jwtFilter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilter_WithNoAuthHeader_ShouldNotSetAuthentication() throws ServletException, IOException {
        jwtFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilter_WithInvalidToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        request.addHeader("Authorization", "Bearer invalid.token.value");

        jwtFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilter_WithNonBearerHeader_ShouldNotSetAuthentication() throws ServletException, IOException {
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");

        jwtFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilter_WithExpiredToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        JwtUtil expiredJwtUtil = new JwtUtil("ThisIsATestSecretKeyThatIsAtLeast32Bytes!!", 0L);
        String expiredToken = expiredJwtUtil.generateToken("testuser", "user123", "STUDENT");
        request.addHeader("Authorization", "Bearer " + expiredToken);

        jwtFilter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilter_WithValidToken_ShouldSetCorrectAuthorities() throws ServletException, IOException {
        String token = jwtUtil.generateToken("teacher1", "t1", "TEACHER");
        request.addHeader("Authorization", "Bearer " + token);

        jwtFilter.doFilter(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER")));
    }

    @Test
    void doFilter_AlwaysContinuesFilterChain() throws ServletException, IOException {
        // Even with a bad token, the filter chain must continue
        request.addHeader("Authorization", "Bearer garbage");

        jwtFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}
