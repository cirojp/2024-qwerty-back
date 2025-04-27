package api.back;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class TransaccionesPendientesControllerTest {

    @Mock
    private TransaccionesPendientesService pendientesService;

    @Mock
    private UserService userService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TransaccionesPendientesController controller;

    @Mock
    private Authentication auth;

    private User user;
    private final String email = "user@example.com";

    @BeforeEach
void setUp() {
    user = new User();
    user.setId(42L);
    when(auth.getName()).thenReturn(email);
    lenient().when(userService.findByEmail(email)).thenReturn(user);
}

    @Test
    void getPendingTransaccionesByUser_returnsList() {
        // Arrange
        TransaccionesPendientes tp1 = new TransaccionesPendientes();
        TransaccionesPendientes tp2 = new TransaccionesPendientes();
        List<TransaccionesPendientes> lista = List.of(tp1, tp2);
        when(pendientesService.getPendingTransaccionesByUserId(42L)).thenReturn(lista);

        // Act
        ResponseEntity<List<TransaccionesPendientes>> resp =
            controller.getPendingTransaccionesByUser(auth);

        // Assert
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertSame(lista, resp.getBody());
        verify(pendientesService).getPendingTransaccionesByUserId(42L);
    }

    @Test
    void deletePendingTransaccion_success_noContent() {
        // Arrange: service does not throw
        doNothing().when(pendientesService).deletePendingTransaccion(123L, 42L);

        // Act
        ResponseEntity<Void> resp =
            controller.deletePendingTransaccion(123L, auth);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(pendientesService).deletePendingTransaccion(123L, 42L);
    }

    @Test
    void deletePendingTransaccion_notFound_returns404() {
        // Arrange
        doThrow(new TransaccionNotFoundException("no existe"))
            .when(pendientesService).deletePendingTransaccion(999L, 42L);

        // Act
        ResponseEntity<Void> resp =
            controller.deletePendingTransaccion(999L, auth);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
        verify(pendientesService).deletePendingTransaccion(999L, 42L);
    }

    @Test
    void transaccionAceptada_invokesRestTemplate_andReturnsBody() {
        // Arrange
        String idReserva = "ABC123";
        String url = "https://backendapi.fpenonori.com/reservation/confirm";
        @SuppressWarnings("unchecked")
        ResponseEntity<String> backendResp = new ResponseEntity<>("OK", HttpStatus.OK);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(String.class)))
            .thenReturn(backendResp);

        // Act
        ResponseEntity<String> resp = controller.transaccionAceptada(idReserva, auth);

        // Assert
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("OK", resp.getBody());
        verify(restTemplate).exchange(
            eq(url), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)
        );
    }

    @Test
    void transaccionRechazada_invokesRestTemplate_andReturnsBody() {
        // Arrange
        String idReserva = "XYZ999";
        @SuppressWarnings("unchecked")
        ResponseEntity<String> backendResp = new ResponseEntity<>("RECHAZADO", HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
            .thenReturn(backendResp);

        // Act
        ResponseEntity<String> resp = controller.transaccionRechazada(idReserva, auth);

        // Assert
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("RECHAZADO", resp.getBody());
        verify(restTemplate).exchange(
            anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)
        );
    }

    @Test
    void postPaymentToUser_userExists_savesAndReturnsOk() {
        // Arrange
        TransaccionRequest req = new TransaccionRequest();
        req.setEmail("other@example.com");
        req.setValor(50.0);
        req.setMotivo("MotivoTest");
        req.setId_reserva("RES1");
        LocalDate hoy = LocalDate.now();
        req.setFecha(hoy);
        req.setMonedaOriginal("USD");
        req.setMontoOriginal(100.0);

        User target = new User();
        target.setId(99L);
        when(userService.findByEmail("other@example.com")).thenReturn(target);

        // Act
        ResponseEntity<Void> resp = controller.postPaymentToUser(req, auth);

        // Assert
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ArgumentCaptor<TransaccionesPendientes> capt = ArgumentCaptor.forClass(TransaccionesPendientes.class);
        verify(pendientesService).save(capt.capture());

        TransaccionesPendientes saved = capt.getValue();
        assertEquals(50.0, saved.getValor());
        assertEquals("MotivoTest", saved.getMotivo());
        assertEquals("RES1", saved.getId_reserva());
        assertEquals(hoy, saved.getFecha());
        assertEquals("USD", saved.getMonedaOriginal());
        assertEquals(100.0, saved.getMontoOriginal());
        assertEquals(target, saved.getUser());
        assertEquals(email, saved.getSentByEmail());
    }

    @Test
    void postPaymentToUser_userNotFound_returnsBadRequest() {
        // Arrange
        TransaccionRequest req = new TransaccionRequest();
        req.setEmail("noone@xx.com");
        when(userService.findByEmail("noone@xx.com")).thenReturn(null);

        // Act
        ResponseEntity<Void> resp = controller.postPaymentToUser(req, auth);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
        verify(pendientesService, never()).save(any());
    }
}
