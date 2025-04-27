package api.back;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TransaccionesPendientesServiceTest {

    @Mock
    private TransaccionesPendientesRepository repo;

    @InjectMocks
    private TransaccionesPendientesService service;

    private TransaccionesPendientes tp;

    @BeforeEach
    void setUp() {
        // Creamos una transacción pendiente de ejemplo
        tp = new TransaccionesPendientes();
        tp.setId(100L);
        User u = new User();
        u.setId(55L);
        tp.setUser(u);
        tp.setGrupoId(77L);
    }

    @Test
    void save_delegatesToRepository() {
        when(repo.save(tp)).thenReturn(tp);
        TransaccionesPendientes out = service.save(tp);
        assertSame(tp, out);
        verify(repo).save(tp);
    }

    @Test
    void getPendingTransaccionesByUserId_delegatesToRepository() {
        List<TransaccionesPendientes> lista = List.of(tp);
        when(repo.findByUserId(55L)).thenReturn(lista);
        List<TransaccionesPendientes> out = service.getPendingTransaccionesByUserId(55L);
        assertSame(lista, out);
        verify(repo).findByUserId(55L);
    }

    @Test
    void deletePendingTransaccion_success() {
        // Si existe, debe borrarlo
        when(repo.findByIdAndUserId(100L, 55L)).thenReturn(Optional.of(tp));
        assertDoesNotThrow(() -> service.deletePendingTransaccion(100L, 55L));
        verify(repo).delete(tp);
    }

    @Test
    void deletePendingTransaccion_notFound_throws() {
        when(repo.findByIdAndUserId(200L, 55L)).thenReturn(Optional.empty());
        assertThrows(TransaccionNotFoundException.class,
            () -> service.deletePendingTransaccion(200L, 55L));
        verify(repo, never()).delete(any());
    }

    @Test
    void findByGrupoId_delegatesToRepository() {
        List<TransaccionesPendientes> lista = List.of(tp);
        when(repo.findByGrupoId(77L)).thenReturn(lista);
        List<TransaccionesPendientes> out = service.findByGrupoId(77L);
        assertSame(lista, out);
        verify(repo).findByGrupoId(77L);
    }

    @Test
    void deleteById_delegatesToRepository() {
        // no hace find, borra directamente por id
        service.delete(123L);
        verify(repo).deleteById(123L);
    }
}
