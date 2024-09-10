package api.back;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

public class PersonalTipoGastoControllerTest {

    // Retrieve personal tipo gastos for authenticated user
    @Test
    public void test_get_personal_tipo_gastos_authenticated_user() {
        // Arrange
        PersonalTipoGastoService personalTipoGastoService = mock(PersonalTipoGastoService.class);
        PersonalTipoGastoController controller = new PersonalTipoGastoController();
        ReflectionTestUtils.setField(controller, "personalTipoGastoService", personalTipoGastoService);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");

        List<PersonalTipoGasto> expectedGastos = Arrays.asList(new PersonalTipoGasto(), new PersonalTipoGasto());
        when(personalTipoGastoService.getPersonalTipoGastos("user@example.com")).thenReturn(expectedGastos);

        // Act
        List<PersonalTipoGasto> result = controller.getPersonalTipoGastos(authentication);

        // Assert
        assertEquals(expectedGastos, result);
    }

    // Handle case when authentication object is null
    @Test
    public void test_get_personal_tipo_gastos_authentication_null() {
        // Arrange
        PersonalTipoGastoService personalTipoGastoService = mock(PersonalTipoGastoService.class);
        PersonalTipoGastoController controller = new PersonalTipoGastoController();
        ReflectionTestUtils.setField(controller, "personalTipoGastoService", personalTipoGastoService);

        Authentication authentication = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            controller.getPersonalTipoGastos(authentication);
        });
    }

    // Correctly adds a new PersonalTipoGasto when valid nombre and authentication
    // are provided
    @Test
    public void test_add_personal_tipo_gasto_with_valid_nombre_and_authentication() {
        // Arrange
        PersonalTipoGastoService personalTipoGastoService = mock(PersonalTipoGastoService.class);
        PersonalTipoGastoController controller = new PersonalTipoGastoController();
        ReflectionTestUtils.setField(controller, "personalTipoGastoService", personalTipoGastoService);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("test@example.com");

        String nombre = "Gasto1";
        PersonalTipoGasto expectedGasto = new PersonalTipoGasto();
        when(personalTipoGastoService.addPersonalTipoGasto("test@example.com", nombre)).thenReturn(expectedGasto);

        // Act
        PersonalTipoGasto result = controller.addPersonalTipoGasto(nombre, authentication);

        // Assert
        assertEquals(expectedGasto, result);
        verify(personalTipoGastoService).addPersonalTipoGasto("test@example.com", nombre);
    }
}