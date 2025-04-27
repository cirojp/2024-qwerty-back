package api.back;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransaccionesServiceTest {

    @Mock private TransaccionesRepository transRepo;
    @Mock private UserRepository userRepo;
    @Mock private UserService userService;
    @InjectMocks private TransaccionesService service;

    private final String EMAIL = "u@example.com";
    private User usuario;

    @BeforeEach
    void setup() {
        usuario = new User();
        usuario.setId(42L);
        usuario.setEmail(EMAIL);
    }

    @Test
    void getTransaccionesByUserId_delegatesToRepository() {
        List<Transacciones> lista = List.of(new Transacciones(), new Transacciones());
        when(transRepo.findByUserIdOrderByFechaDesc(42L)).thenReturn(lista);

        List<Transacciones> out = service.getTransaccionesByUserId(42L);
        assertSame(lista, out);
        verify(transRepo).findByUserIdOrderByFechaDesc(42L);
    }

    @Test
    void createTransaccion_setsUserAndDateAndSaves() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        // la fecha vendrá null ⇒ se asigna LocalDate.now()
        Transacciones t = new Transacciones();
        t.setFecha(null);
        when(transRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Transacciones created = service.createTransaccion(t, EMAIL);
        assertNotNull(created.getUser());
        assertEquals(usuario, created.getUser());
        assertNotNull(created.getFecha());
        // sin frecuencia recurrente, siguienteEjecución sigue null
        assertNull(created.getSiguienteEjecucion());
    }

    @Test
    void createTransaccion_withRecurrente_calculaSiguienteEjecucion() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        Transacciones t = new Transacciones();
        t.setFecha(LocalDate.of(2024,1,1));
        t.setFrecuenciaRecurrente("mensualmente");
        when(transRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Transacciones created = service.createTransaccion(t, EMAIL);
        assertEquals(LocalDate.of(2024,2,1), created.getSiguienteEjecucion());
    }

    @Test
    void getTransaccionById_returnsOptionalFromRepo() {
        Transacciones t = new Transacciones();
        when(transRepo.findById(5L)).thenReturn(Optional.of(t));
        assertTrue(service.getTransaccionById(5L).isPresent());
        assertSame(t, service.getTransaccionById(5L).get());
    }

    @Test
    void deleteTransaccion_success() {
        Transacciones t = new Transacciones();
        when(transRepo.findByIdAndUserEmail(7L, EMAIL)).thenReturn(Optional.of(t));

        // no lanza excepción
        assertDoesNotThrow(() -> service.deleteTransaccion(7L, EMAIL));
        verify(transRepo).delete(t);
    }

    @Test
    void deleteTransaccion_notFound_throws() {
        when(transRepo.findByIdAndUserEmail(8L, EMAIL)).thenReturn(Optional.empty());
        assertThrows(TransaccionNotFoundException.class,
            () -> service.deleteTransaccion(8L, EMAIL));
    }

    @Test
    void updateTransaccion_success_updatesFieldsAndSaves() {
        // preparar existente
        Transacciones existente = new Transacciones();
        existente.setId(9L);
        existente.setUser(usuario);
        when(userService.findByEmail(EMAIL)).thenReturn(usuario);
        when(transRepo.findByIdAndUserId(9L, usuario.getId())).thenReturn(Optional.of(existente));
        when(transRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // payload con cambios
        Transacciones payload = new Transacciones();
        payload.setMotivo("m1");
        payload.setValor(123.0);
        payload.setFecha(LocalDate.of(2023,5,5));
        payload.setCategoria("cat");
        payload.setTipoGasto("tp");
        payload.setMonedaOriginal("USD");
        payload.setMontoOriginal(50.0);
        payload.setFrecuenciaRecurrente("anualmente");

        Transacciones updated = service.updateTransaccion(9L, payload, EMAIL);
        assertEquals("m1", updated.getMotivo());
        assertEquals(123.0, updated.getValor());
        assertEquals(LocalDate.of(2023,5,5), updated.getFecha());
        assertEquals("cat", updated.getCategoria());
        assertEquals("tp", updated.getTipoGasto());
        assertEquals("USD", updated.getMonedaOriginal());
        assertEquals(50.0, updated.getMontoOriginal());
        // siguiente ejecución anual desde 2023-05-05
        assertEquals(LocalDate.of(2024,5,5), updated.getSiguienteEjecucion());
    }

    @Test
    void updateTransaccion_notFound_throws() {
        when(userService.findByEmail(EMAIL)).thenReturn(usuario);
        when(transRepo.findByIdAndUserId(10L, usuario.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
            () -> service.updateTransaccion(10L, new Transacciones(), EMAIL));
    }

    @Test
    void getTransaccionesByUserIdAndCategory_delegates() {
        List<Transacciones> lista = List.of(new Transacciones());
        when(transRepo.findByUserIdAndCategoriaOrderByFechaDesc(42L, "C"))
            .thenReturn(lista);

        assertSame(lista, service.getTransaccionesByUserIdAndCategory(42L, "C"));
    }

    @Test
    void getTransaccionesFiltradas_variasRamas() {
        // caso: categoría != Todas, año y mes null
        when(transRepo.findByUserIdAndCategoriaOrderByFechaDesc(1L, "X"))
            .thenReturn(List.of());
        service.getTransaccionesFiltradas(1L, "X", null, null);
        verify(transRepo).findByUserIdAndCategoriaOrderByFechaDesc(1L, "X");

        // caso: categoría != Todas, año+mes definidos
        LocalDate sd = LocalDate.of(2022,3,1), ed = sd.plusMonths(1).minusDays(1);
        when(transRepo.findByUserIdAndCategoriaAndFechaBetween(1L, "Y", sd, ed))
            .thenReturn(List.of());
        service.getTransaccionesFiltradas(1L, "Y", 2022, 3);
        verify(transRepo).findByUserIdAndCategoriaAndFechaBetween(1L, "Y", sd, ed);

        // caso: categoría Todas, año+mes definidos
        when(transRepo.findByUserIdAndFechaBetween(2L, sd, ed))
            .thenReturn(List.of());
        service.getTransaccionesFiltradas(2L, "Todas", 2022, 3);
        verify(transRepo).findByUserIdAndFechaBetween(2L, sd, ed);

        // caso fallback
        when(transRepo.findByUserIdOrderByFechaDesc(3L)).thenReturn(List.of());
        service.getTransaccionesFiltradas(3L, null, null, null);
        verify(transRepo).findByUserIdOrderByFechaDesc(3L);
    }

    @Test
    void getTransaccionesRecurrentes_delegates() {
        when(transRepo.findByUserIdAndFrecuenciaRecurrenteIsNotNull(5L))
            .thenReturn(List.of());
        List<Transacciones> out = service.getTransaccionesRecurrentes(5L);
        assertNotNull(out);
        verify(transRepo).findByUserIdAndFrecuenciaRecurrenteIsNotNull(5L);
    }

    @Test
    void procesarTransaccionesRecurrentes_creaYActualizaRegistros() {
        // simulamos hoy
        LocalDate hoy = LocalDate.now();
        Transacciones t1 = new Transacciones();
        t1.setId(100L);
        t1.setFecha(hoy.minusDays(1));
        t1.setFrecuenciaRecurrente("diariamente");
        t1.setUser(usuario);
        t1.setMotivo("foo");
        t1.setCategoria("C");
        t1.setTipoGasto("T");
        t1.setValor(10.0);
        t1.setMonedaOriginal("ARG");
        t1.setMontoOriginal(10.0);

        when(transRepo.findBySiguienteEjecucion(hoy)).thenReturn(List.of(t1));
        when(transRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.procesarTransaccionesRecurrentes();

        // para cada t1 debe guardarse la nueva transacción y luego actualizarse t1
        // total 2 invocaciones a save por cada elemento
        verify(transRepo, times(2)).save(any(Transacciones.class));
    }

}
