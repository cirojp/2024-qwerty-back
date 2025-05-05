package api.back;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class PersonalTipoGastoControllerTest {

    @Mock
    private PersonalTipoGastoService personalTipoGastoService;

    @Mock
    private TransaccionesController transaccionesController;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PersonalTipoGastoController controller;

    private User user;

    @BeforeEach
    void setUp() {
        // No es estrictamente necesario si usamos @ExtendWith, 
        // pero lo dejamos por claridad.
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("user@example.com");
    }

    /*@Test
    void testGetPersonalTipoGastos() {
        List<PersonalTipoGasto> expected = List.of(new PersonalTipoGasto(), new PersonalTipoGasto());
        when(authentication.getName()).thenReturn(user.getEmail());
        when(personalTipoGastoService.getPersonalTipoGastos(user.getEmail())).thenReturn(expected);

        List<PersonalTipoGasto> result = controller.getPersonalTipoGastos(authentication);

        assertNotNull(result);
        assertEquals(expected, result);
        verify(personalTipoGastoService).getPersonalTipoGastos(user.getEmail());
    }

    @Test
    void testAddPersonalTipoGasto() {
        String nombre = "\"Transporte\""; // con comillas, el controlador debe limpiarlas
        PersonalTipoGasto tipoGasto = new PersonalTipoGasto();
        tipoGasto.setNombre("Transporte");

        when(authentication.getName()).thenReturn(user.getEmail());
        when(personalTipoGastoService.addPersonalTipoGasto(user.getEmail(), "Transporte"))
            .thenReturn(tipoGasto);

        PersonalTipoGasto result = controller.addPersonalTipoGasto(nombre, authentication);

        assertNotNull(result);
        assertEquals("Transporte", result.getNombre());
        verify(personalTipoGastoService).addPersonalTipoGasto(user.getEmail(), "Transporte");
    }

    @Test
    void testUpdatePersonalTipoGasto() {
        Map<String, String> requestBody = Map.of(
            "nombreActual", "\"Transporte\"",
            "nombreNuevo", "\"Comida\""
        );
        PersonalTipoGasto updated = new PersonalTipoGasto();
        updated.setNombre("Comida");

        // Simulamos que no hay transacciones con ese tipo (solo interesa que se invoque al servicio)
        when(authentication.getName()).thenReturn(user.getEmail());
        when(transaccionesController.getTransaccionesByUser(authentication)).thenReturn(List.of());
        when(personalTipoGastoService.updatePersonalTipoGasto(user.getEmail(), "Transporte", "Comida"))
            .thenReturn(updated);

        PersonalTipoGasto result = controller.updatePersonalTipoGasto(requestBody, authentication);

        assertNotNull(result);
        assertEquals("Comida", result.getNombre());
        verify(personalTipoGastoService).updatePersonalTipoGasto(user.getEmail(), "Transporte", "Comida");
    }

    @Test
    void testDeletePersonalTipoGasto() {
        // Preparo un listado de transacciones sin coincidencias
        when(authentication.getName()).thenReturn(user.getEmail());
        when(transaccionesController.getTransaccionesByUser(authentication)).thenReturn(List.of());

        ResponseEntity<Void> response = controller.deletePersonalTipoGasto("\"Transporte\"", authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Se debe haber invocado con el nombre ya limpiado
        verify(personalTipoGastoService).deletePersonalTipoGastoByName(user.getEmail(), "Transporte");
    }

    @Test
    void testDeletePersonalTipoGasto_whenTipoGastoNotFound() {
        when(authentication.getName()).thenReturn(user.getEmail());
        when(transaccionesController.getTransaccionesByUser(authentication)).thenReturn(List.of());

        // Hacemos que el servicio lance excepción
        doThrow(new RuntimeException("Tipo de gasto no encontrado"))
            .when(personalTipoGastoService)
            .deletePersonalTipoGastoByName(user.getEmail(), "Transporte");

        // Ahora esperamos que el controlador propague la excepción
        RuntimeException ex = assertThrows(
            RuntimeException.class,
            () -> controller.deletePersonalTipoGasto("\"Transporte\"", authentication)
        );
        assertEquals("Tipo de gasto no encontrado", ex.getMessage());
    }*/
}
