package api.back;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootTest
@ActiveProfiles("test")
public class JwtRequestFilterTests {
    @Autowired
    JwtRequestFilter jwtRequestFilter;

    @Test
    public void test_authorization_header_is_null() throws ServletException, IOException {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtRequestFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(request).getHeader("Authorization");
        verify(chain).doFilter(request, response);
    }

    /*
     * @Test
     * public void test_extract_jwt_token_from_authorization_header() throws
     * ServletException, IOException {
     * // Arrange
     * HttpServletRequest request = mock(HttpServletRequest.class);
     * HttpServletResponse response = mock(HttpServletResponse.class);
     * FilterChain chain = mock(FilterChain.class);
     * JwtUtil jwtUtil = mock(JwtUtil.class);
     * UserService userService = mock(UserService.class);
     * 
     * String token = "Bearer valid.jwt.token";
     * String email = "user@example.com";
     * UserDetails userDetails = mock(UserDetails.class);
     * 
     * when(request.getHeader("Authorization")).thenReturn(token);
     * when(jwtUtil.extractUsername("valid.jwt.token")).thenReturn(email);
     * when(userService.loadUserByUsername(email)).thenReturn(userDetails);
     * when(jwtUtil.validateToken("valid.jwt.token", userDetails)).thenReturn(true);
     * 
     * // Act
     * jwtRequestFilter.doFilterInternal(request, response, chain);
     * 
     * // Assert
     * verify(request).getHeader("Authorization");
     * verify(jwtUtil).extractUsername("valid.jwt.token");
     * verify(userService).loadUserByUsername(email);
     * verify(jwtUtil).validateToken("valid.jwt.token", userDetails);
     * verify(chain).doFilter(request, response);
     * }
     */
}
