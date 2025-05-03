package api.back;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles("test")
public class TransaccionesControllerTest {

    @Mock Authentication auth;
    @Mock UserService userService;
    @Mock TransaccionesService txService;
    @Mock TransaccionesPendientesService pendientesService;
    @Mock JwtUtil jwtUtil;

    @InjectMocks
    TransaccionesController controller;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
        // Inyectamos el mock de jwtUtil en el campo privado "jwtUtil" del controller
        ReflectionTestUtils.setField(controller, "jwtUtil", jwtUtil);
    }

    @Test
    public void testObtenerTransaccionesDeUsuario() {
        String email = "user@example.com";
        User user = new User();
        user.setId(1L);
        List<Transacciones> expected = List.of(new Transacciones(), new Transacciones());

        when(auth.getName()).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(user);
        when(txService.getTransaccionesByUserId(1L)).thenReturn(expected);

        List<Transacciones> actual = controller.getTransaccionesByUser(auth);

        assertEquals(expected, actual);
    }

    @Test
    public void testCheckUserValidToken() {
        // El método no usa auth.getName(), así que no lo stubbeamos aquí
        String header = "Bearer ABC";
        when(jwtUtil.isTokenExpired("ABC")).thenReturn(false);

        boolean valid = controller.checkUserValidToken(auth, header);

        assertTrue(valid);
        verify(jwtUtil).isTokenExpired("ABC");
    }

    @Test
    public void testGetTransaccionById() {
        Long id = 42L;
        Optional<Transacciones> tx = Optional.of(new Transacciones());
        when(txService.getTransaccionById(id)).thenReturn(tx);

        Optional<Transacciones> result = controller.getTransaccionById(id);

        assertEquals(tx, result);
    }

    /*@Test
    public void testCreateTransaccionWithValidData() {
        String email = "test@example.com";
        Transacciones t = new Transacciones();
        User user = new User();
        user.setTransaccionesCreadas(0);

        when(auth.getName()).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(user);
        when(txService.createTransaccion(t, email)).thenReturn(t);

        Transacciones result = controller.createTransaccion(t, auth);

        assertEquals(t, result);
        verify(txService).createTransaccion(t, email);
        assertEquals(1, user.getTransaccionesCreadas());
    }*/

    @Test
    public void testDeleteTransaccionSuccess() {
        Long id = 1L;
        when(auth.getName()).thenReturn("user@example.com");

        ResponseEntity<Void> resp = controller.deleteTransaccion(id, auth);

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(txService).deleteTransaccion(id, "user@example.com");
    }

    @Test
    public void testDeleteTransaccionNotFound() {
        Long id = 2L;
        String email = "u@e";
        when(auth.getName()).thenReturn(email);
        doThrow(new TransaccionNotFoundException("")).when(txService).deleteTransaccion(id, email);

        ResponseEntity<Void> resp = controller.deleteTransaccion(id, auth);

        assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
    }

    @Test
    public void testUpdateTransaccionSuccess() {
        Long id = 1L;
        Transacciones updated = new Transacciones();
        updated.setMotivo("Nuevo");

        when(auth.getName()).thenReturn("user@example.com");
        when(txService.updateTransaccion(id, updated, "user@example.com"))
            .thenReturn(updated);

        Transacciones result = controller.updateTransaccion(id, updated, auth);

        assertEquals(updated, result);
        verify(txService).updateTransaccion(id, updated, "user@example.com");
    }

    /*@Test
    public void testCreatePagoSuccess() {
        String email = "user@example.com", mail = "test@example.com";
        Transacciones t = new Transacciones();
        t.setMotivo("Pago");
        t.setValor(100.0);
        t.setMonedaOriginal("USD");
        t.setMontoOriginal(100.0);

        when(auth.getName()).thenReturn(email);
        when(userService.findByEmail(mail)).thenReturn(new User());
        when(userService.findByEmail(email)).thenReturn(new User());
        when(txService.createTransaccion(any(Transacciones.class), eq(mail))).thenReturn(t);
        when(txService.createTransaccion(t, email)).thenReturn(t);

        Transacciones result = controller.createPago(mail, t, auth);

        assertEquals(t, result);
        verify(txService).createTransaccion(any(Transacciones.class), eq(mail));
        verify(pendientesService).save(any(TransaccionesPendientes.class));
        verify(txService).createTransaccion(t, email);
    }*/

    @Test
    public void testGetTransaccionesRecurrentes() {
        String email = "user@example.com";
        User user = new User();
        user.setId(1L);
        List<Transacciones> recurrentes = List.of(new Transacciones());

        when(auth.getName()).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(user);
        when(txService.getTransaccionesRecurrentes(1L)).thenReturn(recurrentes);

        List<Transacciones> result = controller.getTransaccionesRecurrentes(auth);

        assertEquals(recurrentes, result);
    }

    @Test
    public void testGetTransaccionesByFilters() {
        Authentication a2 = mock(Authentication.class);
        when(a2.getName()).thenReturn("user@example.com");

        User user = new User(); user.setId(42L);
        when(userService.findByEmail("user@example.com")).thenReturn(user);

        List<Transacciones> f1 = List.of(new Transacciones());
        List<Transacciones> f2 = List.of(new Transacciones(), new Transacciones());
        when(txService.getTransaccionesFiltradas(42L, "Food", 2023, 7)).thenReturn(f1);
        when(txService.getTransaccionesFiltradas(42L, "Todas", 2023, 7)).thenReturn(f2);

        TransaccionesResponse resp = controller.getTransaccionesByFilters("Food", 2023, 7, a2);

        assertNotNull(resp);
        verify(txService).getTransaccionesFiltradas(42L, "Food", 2023, 7);
        verify(txService).getTransaccionesFiltradas(42L, "Todas", 2023, 7);
    }
}
