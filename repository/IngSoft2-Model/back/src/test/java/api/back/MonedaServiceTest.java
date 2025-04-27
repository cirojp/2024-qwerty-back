package api.back;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public class MonedaServiceTest {

    @Mock
    private MonedaRepository monedaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MonedaService monedaService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inicializamos un usuario para las pruebas
        user = new User();
        user.setEmail("test@example.com");
    }

    // Test para agregar una moneda correctamente
    @Test
    void testAddMoneda_Success() {
        String email = "test@example.com";
        String nombre = "USD";
        Double valor = 100.0;

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(monedaRepository.save(any(Moneda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Moneda moneda = monedaService.addMoneda(email, nombre, valor);

        assertNotNull(moneda);
        assertEquals(nombre, moneda.getNombre());
        assertEquals(valor, moneda.getValor());
        verify(monedaRepository).save(any(Moneda.class));
    }

    // Test para agregar una moneda cuando el usuario no existe
    @Test
    void testAddMoneda_UserNotFound() {
        String email = "nonexistent@example.com";
        String nombre = "USD";
        Double valor = 100.0;

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            monedaService.addMoneda(email, nombre, valor);
        });
    }

    // Test para actualizar una moneda correctamente
    @Test
    void testUpdateMonedaPorNombre_Success() {
        String email = "test@example.com";
        String nombreActual = "USD";
        String nombreNuevo = "EUR";
        Double valorNuevo = 120.0;

        Moneda existingMoneda = new Moneda();
        existingMoneda.setNombre(nombreActual);
        existingMoneda.setValor(100.0);
        when(monedaRepository.findByUserEmailAndNombre(email, nombreActual)).thenReturn(Optional.of(existingMoneda));
        when(monedaRepository.save(any(Moneda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Moneda updatedMoneda = monedaService.updateMonedaPorNombre(email, nombreActual, nombreNuevo, valorNuevo);

        assertNotNull(updatedMoneda);
        assertEquals(nombreNuevo, updatedMoneda.getNombre());
        assertEquals(valorNuevo, updatedMoneda.getValor());
        verify(monedaRepository).save(any(Moneda.class));
    }

    // Test para actualizar una moneda cuando no se encuentra la moneda
    @Test
    void testUpdateMonedaPorNombre_NotFound() {
        String email = "test@example.com";
        String nombreActual = "USD";
        String nombreNuevo = "EUR";
        Double valorNuevo = 120.0;

        when(monedaRepository.findByUserEmailAndNombre(email, nombreActual)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            monedaService.updateMonedaPorNombre(email, nombreActual, nombreNuevo, valorNuevo);
        });
    }

    // Test para eliminar una moneda correctamente
    @Test
    void testDeleteMonedaPorNombre_Success() {
        String email = "test@example.com";
        String nombre = "USD";

        Moneda existingMoneda = new Moneda();
        existingMoneda.setNombre(nombre);
        when(monedaRepository.findByUserEmailAndNombre(email, nombre)).thenReturn(Optional.of(existingMoneda));

        monedaService.deleteMonedaPorNombre(email, nombre);

        verify(monedaRepository).delete(existingMoneda);
    }

    // Test para eliminar una moneda cuando no se encuentra la moneda
    @Test
    void testDeleteMonedaPorNombre_NotFound() {
        String email = "test@example.com";
        String nombre = "USD";

        when(monedaRepository.findByUserEmailAndNombre(email, nombre)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            monedaService.deleteMonedaPorNombre(email, nombre);
        });
    }

    // Test para obtener las monedas de un usuario
    @Test
    void testGetMonedasByEmail_Success() {
        String email = "test@example.com";
        List<Moneda> expectedMonedas = List.of(new Moneda(), new Moneda());
        when(monedaRepository.findByUserEmail(email)).thenReturn(expectedMonedas);

        List<Moneda> actualMonedas = monedaService.getMonedasByEmail(email);

        assertEquals(expectedMonedas, actualMonedas);
        verify(monedaRepository).findByUserEmail(email);
    }
}
