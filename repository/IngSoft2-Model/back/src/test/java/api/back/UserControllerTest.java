package api.back;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.security.Principal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void changePassword_WhenCurrentPasswordMatches_ReturnsOk() {
        // Arrange
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("alice@example.com");

        // Usamos el constructor que recibe currentPassword y newPassword
        ChangePasswordRequest req = new ChangePasswordRequest("oldPass", "newPass");

        when(userService.changePassword("alice@example.com", "oldPass", "newPass"))
            .thenReturn(true);

        // Act
        ResponseEntity<String> resp = controller.changePassword(req, principal);

        // Assert
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("Password changed successfully.", resp.getBody());
    }

    @Test
    void changePassword_WhenCurrentPasswordWrong_ReturnsBadRequest() {
        // Arrange
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn("bob@example.com");

        ChangePasswordRequest req = new ChangePasswordRequest("wrongOld", "whatever");

        when(userService.changePassword("bob@example.com", "wrongOld", "whatever"))
            .thenReturn(false);

        // Act
        ResponseEntity<String> resp = controller.changePassword(req, principal);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        assertEquals("Current password is incorrect.", resp.getBody());
    }

    @Test
    void getUserTransactions_ReturnsCreatedCount() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("charlie@example.com");

        User user = new User();
        user.setTransaccionesCreadas(7);
        when(userService.findByEmail("charlie@example.com")).thenReturn(user);

        // Act
        ResponseEntity<Integer> resp = controller.getUserTransactions(auth);

        // Assert
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(7, resp.getBody());
    }
}
