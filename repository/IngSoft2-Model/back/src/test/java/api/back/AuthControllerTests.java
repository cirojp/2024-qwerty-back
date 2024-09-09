package api.back;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class AuthControllerTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthController authController;
    @Autowired
    private UserService userService;
    @Test
    public void testRegistrarNuevoUsuario() {
        User user = new User();
        user.setEmail("unique@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        ResponseEntity<String> response = authController.register(user);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        userService.deleteUser(user);
    }

    @Test
    public void testRegistrarUsuarioConEmailExistente() {
        User existingUser = new User();
        existingUser.setEmail("used@example.com");
        existingUser.setPassword("password");
        authController.register(existingUser);

        User newUser = new User();
        newUser.setEmail("used@example.com");
        newUser.setPassword("password");
        ResponseEntity<String> response = authController.register(newUser);

        Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Assertions.assertEquals("El e-mail ya fue utilizado. Intente iniciar sesion", response.getBody());
    }

    @Test
    public void test_load_user_by_email_success() {
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword("123456");
        ResponseEntity<String> response = authController.register(mockUser);
        UserDetails userDetails = userService.loadUserByUsername(email);
        Assertions.assertNotNull(userDetails);
        Assertions.assertEquals(email, userDetails.getUsername());
    }
/*
    // Throws UsernameNotFoundException when user does not exist
    @Test
    public void test_load_user_by_email_not_found() {
        // Arrange
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        PasswordResetTokenRepository passwordResetTokenRepository = Mockito.mock(PasswordResetTokenRepository.class);
        JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
        UserService userService = new UserService(userRepository, passwordEncoder, passwordResetTokenRepository, mailSender);
    
        String email = "nonexistent@example.com";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    
        // Act & Assert
        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(email);
        });
    } */
}