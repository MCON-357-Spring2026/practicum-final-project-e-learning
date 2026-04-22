package SecurityTests;

import com.elearning.security.CustomAuthEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CustomAuthEntryPointTest {

    private CustomAuthEntryPoint entryPoint;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        entryPoint = new CustomAuthEntryPoint();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void commence_ShouldReturn401Status() throws Exception {
        entryPoint.commence(request, response, new BadCredentialsException("Bad credentials"));

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void commence_ShouldReturnJsonContentType() throws Exception {
        entryPoint.commence(request, response, new BadCredentialsException("Bad credentials"));

        assertEquals("application/json", response.getContentType());
    }

    @Test
    @SuppressWarnings("unchecked")
    void commence_ShouldReturnErrorMessageInBody() throws Exception {
        entryPoint.commence(request, response, new BadCredentialsException("Bad credentials"));

        String body = response.getContentAsString();
        Map<String, String> responseBody = objectMapper.readValue(body, Map.class);

        assertEquals("Unauthorized", responseBody.get("error"));
        assertEquals("Bad credentials", responseBody.get("message"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void commence_WithDifferentMessage_ShouldReflectInResponse() throws Exception {
        entryPoint.commence(request, response,
                new BadCredentialsException("Full authentication is required"));

        String body = response.getContentAsString();
        Map<String, String> responseBody = objectMapper.readValue(body, Map.class);

        assertEquals("Full authentication is required", responseBody.get("message"));
    }
}
