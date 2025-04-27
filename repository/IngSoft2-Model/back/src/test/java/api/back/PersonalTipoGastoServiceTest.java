package api.back;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class PersonalTipoGastoServiceTest {

    @Mock
    private PersonalTipoGastoRepository tipoGastoRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private PersonalTipoGastoService service;

    private final String EMAIL = "user@example.com";

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail(EMAIL);
        user.setId(123L);
    }

    @Test
    void getPersonalTipoGastos_whenUserExists_thenReturnsList() {
        List<PersonalTipoGasto> expected = List.of(
            new PersonalTipoGasto(), new PersonalTipoGasto()
        );
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(tipoGastoRepo.findByUser(user)).thenReturn(expected);

        List<PersonalTipoGasto> actual = service.getPersonalTipoGastos(EMAIL);

        assertEquals(expected, actual);
        verify(userRepo).findByEmail(EMAIL);
        verify(tipoGastoRepo).findByUser(user);
    }

    @Test
    void getPersonalTipoGastos_whenUserNotFound_thenThrows() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(
            UsernameNotFoundException.class,
            () -> service.getPersonalTipoGastos(EMAIL),
            "User not found"
        );
        verify(tipoGastoRepo, never()).findByUser(any());
    }

    @Test
    void addPersonalTipoGasto_whenUserExists_thenSavesWithNameAndUser() {
        String nombre = "Transporte";
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(tipoGastoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PersonalTipoGasto saved = service.addPersonalTipoGasto(EMAIL, nombre);

        assertNotNull(saved);
        assertEquals(nombre, saved.getNombre());
        assertEquals(user, saved.getUser());
        verify(tipoGastoRepo).save(saved);
    }

    @Test
    void addPersonalTipoGasto_whenUserNotFound_thenThrows() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(
            UsernameNotFoundException.class,
            () -> service.addPersonalTipoGasto(EMAIL, "Cualquier"),
            "User not found"
        );
        verify(tipoGastoRepo, never()).save(any());
    }

    @Test
    void deletePersonalTipoGasto_invokesRepository() {
        Long id = 77L;
        // no need to stub anything, solo verificamos que llama a deleteById
        service.deletePersonalTipoGasto(id);
        verify(tipoGastoRepo).deleteById(id);
    }

    @Test
    void updatePersonalTipoGasto_whenFound_thenRenamesAndSaves() {
        String actual = "Old";
        String nuevo  = "New";
        PersonalTipoGasto tipo = new PersonalTipoGasto();
        tipo.setNombre(actual);
        tipo.setUser(user);

        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(tipoGastoRepo.findByUserAndNombre(user, actual))
            .thenReturn(Optional.of(tipo));
        when(tipoGastoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PersonalTipoGasto result = service.updatePersonalTipoGasto(
            EMAIL, actual, nuevo
        );

        assertEquals(nuevo, result.getNombre());
        verify(tipoGastoRepo).save(tipo);
    }

    @Test
    void updatePersonalTipoGasto_whenTipoNotFound_thenThrows() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(tipoGastoRepo.findByUserAndNombre(user, "noExiste"))
            .thenReturn(Optional.empty());

        assertThrows(
            RuntimeException.class,
            () -> service.updatePersonalTipoGasto(EMAIL, "noExiste", "Cualquiera")
        );
        verify(tipoGastoRepo, never()).save(any());
    }

    @Test
    void deletePersonalTipoGastoByName_whenFound_thenDeletes() {
        String nombre = "Comida";
        PersonalTipoGasto tipo = new PersonalTipoGasto();
        tipo.setNombre(nombre);
        tipo.setUser(user);

        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(tipoGastoRepo.findByUserAndNombre(user, nombre))
            .thenReturn(Optional.of(tipo));

        service.deletePersonalTipoGastoByName(EMAIL, nombre);

        verify(tipoGastoRepo).delete(tipo);
    }

    @Test
    void deletePersonalTipoGastoByName_whenTipoNotFound_thenThrows() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(tipoGastoRepo.findByUserAndNombre(user, "X"))
            .thenReturn(Optional.empty());

        assertThrows(
            RuntimeException.class,
            () -> service.deletePersonalTipoGastoByName(EMAIL, "X")
        );
        verify(tipoGastoRepo, never()).delete(any());
    }

    @Test
    void deletePersonalTipoGastoByName_whenUserNotFound_thenThrows() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(
            UsernameNotFoundException.class,
            () -> service.deletePersonalTipoGastoByName(EMAIL, "Cualquier")
        );
        verify(tipoGastoRepo, never()).delete(any());
    }
}
