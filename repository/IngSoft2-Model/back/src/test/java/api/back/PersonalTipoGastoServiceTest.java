package api.back;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("test")
public class PersonalTipoGastoServiceTest {

    // getPersonalTipoGastos returns list of PersonalTipoGasto for valid email
    @Test
    public void test_getPersonalTipoGastos_validEmail_returnsList() {
        // Arrange
        String email = "valid@example.com";
        User user = new User();
        user.setEmail(email);
        List<PersonalTipoGasto> expectedList = Arrays.asList(new PersonalTipoGasto(), new PersonalTipoGasto());

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PersonalTipoGastoRepository personalTipoGastoRepository = Mockito.mock(PersonalTipoGastoRepository.class);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(personalTipoGastoRepository.findByUser(user)).thenReturn(expectedList);

        PersonalTipoGastoService service = new PersonalTipoGastoService();
        ReflectionTestUtils.setField(service, "userRepository", userRepository);
        ReflectionTestUtils.setField(service, "personalTipoGastoRepository", personalTipoGastoRepository);

        // Act
        List<PersonalTipoGasto> actualList = service.getPersonalTipoGastos(email);

        // Assert
        assertEquals(expectedList, actualList);
    }

    // getPersonalTipoGastos throws UsernameNotFoundException for non-existent email
    @Test
    public void test_getPersonalTipoGastos_nonExistentEmail_throwsException() {
        // Arrange
        String email = "nonexistent@example.com";

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PersonalTipoGastoRepository personalTipoGastoRepository = Mockito.mock(PersonalTipoGastoRepository.class);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        PersonalTipoGastoService service = new PersonalTipoGastoService();
        ReflectionTestUtils.setField(service, "userRepository", userRepository);
        ReflectionTestUtils.setField(service, "personalTipoGastoRepository", personalTipoGastoRepository);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            service.getPersonalTipoGastos(email);
        });
    }
}