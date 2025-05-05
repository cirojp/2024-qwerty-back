package api.back;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MonedaControllerTest {

    @Mock
    private MonedaService monedaService;

    @Mock
    private TransaccionesController transaccionesController;

    @InjectMocks
    private MonedaController monedaController;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test para obtener las monedas de un usuario
   /* @Test
    void testGetMonedas_Success() {
        String email = "test@example.com";
        List<Moneda> monedas = List.of(new Moneda(), new Moneda());
        when(authentication.getName()).thenReturn(email);
        when(monedaService.getMonedasByEmail(email)).thenReturn(monedas);

        ResponseEntity<List<Moneda>> response = monedaController.getMonedas(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(monedas, response.getBody());
        verify(monedaService).getMonedasByEmail(email);
    }

    // Test para agregar una nueva moneda
    @Test
    void testAddMoneda_Success() {
        String email = "test@example.com";
        Map<String, Object> request = Map.of("nombre", "USD", "valor", 100.0);
        Moneda nuevaMoneda = new Moneda();
        nuevaMoneda.setNombre("USD");
        nuevaMoneda.setValor(100.0);

        when(authentication.getName()).thenReturn(email);
        when(monedaService.addMoneda(email, "USD", 100.0)).thenReturn(nuevaMoneda);

        ResponseEntity<Moneda> response = monedaController.addMoneda(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(nuevaMoneda, response.getBody());
        verify(monedaService).addMoneda(email, "USD", 100.0);
    }

    @Test
    void testDeleteMonedaPorNombre_Success() {
        String email = "test@example.com";
        Map<String, Object> request = Map.of("nombre", "USD");

        when(authentication.getName()).thenReturn(email);
        when(transaccionesController.getTransaccionesByUser(authentication)).thenReturn(List.of(new Transacciones()));
        doNothing().when(monedaService).deleteMonedaPorNombre(email, "USD");

        ResponseEntity<Void> response = monedaController.deleteMonedaPorNombre(request, authentication);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(monedaService).deleteMonedaPorNombre(email, "USD");
    }

    @Test
    public void test_update_moneda_missing_parameters() {
        // Arrange
        MonedaController monedaController = new MonedaController();
    
        MonedaService monedaService = mock(MonedaService.class);
        TransaccionesController transaccionesController = mock(TransaccionesController.class);
    
        ReflectionTestUtils.setField(monedaController, "monedaService", monedaService);
        ReflectionTestUtils.setField(monedaController, "transaccionesController", transaccionesController);
    
        Authentication authentication = mock(Authentication.class);
        Map<String, Object> request = new HashMap<>();
        // Missing nombreActual parameter
        request.put("nombreNuevo", "DOLLAR");
        request.put("valorNuevo", "1.5");
    
        when(authentication.getName()).thenReturn("user@example.com");
    
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            monedaController.updateMonedaPorNombre(request, authentication);
        });
    
        // Test with null request
        assertThrows(NullPointerException.class, () -> {
            monedaController.updateMonedaPorNombre(null, authentication);
        });
    
        // Verify no interactions with services
        verifyNoInteractions(monedaService);
        verifyNoInteractions(transaccionesController);
    }
    @Test
    public void test_update_moneda_invalid_valor_format() {
        // Arrange
        MonedaController monedaController = new MonedaController();
    
        MonedaService monedaService = mock(MonedaService.class);
        TransaccionesController transaccionesController = mock(TransaccionesController.class);
    
        ReflectionTestUtils.setField(monedaController, "monedaService", monedaService);
        ReflectionTestUtils.setField(monedaController, "transaccionesController", transaccionesController);
    
        Authentication authentication = mock(Authentication.class);
        Map<String, Object> request = new HashMap<>();
        request.put("nombreActual", "USD");
        request.put("nombreNuevo", "DOLLAR");
        request.put("valorNuevo", "invalid-number"); // Invalid number format
    
        when(authentication.getName()).thenReturn("user@example.com");
    
        // Act & Assert
        assertThrows(NumberFormatException.class, () -> {
            monedaController.updateMonedaPorNombre(request, authentication);
        });
    
        // Verify no interactions with monedaService
        verifyNoInteractions(monedaService);
    
        // Verify no transactions were updated
        verify(transaccionesController, never()).updateTransaccion(anyLong(), any(Transacciones.class), any(Authentication.class));
    }

    @Test
    public void test_delete_moneda_missing_nombre() {
        // Arrange
        MonedaController monedaController = new MonedaController();
        MonedaService monedaService = mock(MonedaService.class);
        TransaccionesController transaccionesController = mock(TransaccionesController.class);
    
        ReflectionTestUtils.setField(monedaController, "monedaService", monedaService);
        ReflectionTestUtils.setField(monedaController, "transaccionesController", transaccionesController);
    
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");
    
        Map<String, Object> request = new HashMap<>();
        // Missing "nombre" field
    
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            monedaController.deleteMonedaPorNombre(request, authentication);
        });
    
        verify(monedaService, never()).deleteMonedaPorNombre(anyString(), anyString());
    }
    @Test
    public void test_delete_moneda_null_authentication() {
        // Arrange
        MonedaController monedaController = new MonedaController();
        MonedaService monedaService = mock(MonedaService.class);
        TransaccionesController transaccionesController = mock(TransaccionesController.class);
    
        ReflectionTestUtils.setField(monedaController, "monedaService", monedaService);
        ReflectionTestUtils.setField(monedaController, "transaccionesController", transaccionesController);
    
        Map<String, Object> request = new HashMap<>();
        request.put("nombre", "USD");
    
        Authentication authentication = null;
    
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            monedaController.deleteMonedaPorNombre(request, authentication);
        });
    
        verify(monedaService, never()).deleteMonedaPorNombre(anyString(), anyString());
    }*/
}
