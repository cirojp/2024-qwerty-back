package api.back;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class PersonalCategoriaServiceTest {

    @Mock
    private PersonalCategoriaRepository personalCategoriaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PersonalCategoriaService personalCategoriaService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("user@example.com");
    }

    // Test para obtener las categorías de un usuario
    @Test
    void testGetPersonalCategoria() {
        PersonalCategoria categoria1 = new PersonalCategoria();
        categoria1.setNombre("Comida");

        PersonalCategoria categoria2 = new PersonalCategoria();
        categoria2.setNombre("Transporte");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(personalCategoriaRepository.findByUser(user)).thenReturn(List.of(categoria1, categoria2));

        List<PersonalCategoria> result = personalCategoriaService.getPersonalCategoria(user.getEmail());

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(personalCategoriaRepository).findByUser(user);
    }

    // Test para agregar una nueva categoría
    @Test
void testAddPersonalCategoria() {
    String nombre = "Salud";
    String iconPath = "/icons/salud.png";
    PersonalCategoria categoria = new PersonalCategoria();
    categoria.setNombre(nombre);
    categoria.setIconPath(iconPath);
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    when(personalCategoriaRepository.save(any(PersonalCategoria.class))).thenReturn(categoria);
    PersonalCategoria result = personalCategoriaService.addPersonalCategoria(user.getEmail(), nombre, iconPath);
    assertNotNull(result, "La categoría no debe ser null");
    assertEquals(nombre, result.getNombre());
    assertEquals(iconPath, result.getIconPath());
    verify(personalCategoriaRepository).save(any(PersonalCategoria.class));
}


    // Test para eliminar una categoría por ID
    @Test
    void testDeletePersonalCategoria() {
        Long categoriaId = 1L;

        doNothing().when(personalCategoriaRepository).deleteById(categoriaId);

        personalCategoriaService.deletePersonalCategoria(categoriaId);

        verify(personalCategoriaRepository).deleteById(categoriaId);
    }

    // Test para encontrar y eliminar una categoría
    @Test
    void testFindAndDeleteCategoria() {
        String nombreCategoria = "Salud";
        String iconPath = "/icons/salud.png";

        PersonalCategoria categoria = new PersonalCategoria();
        categoria.setNombre(nombreCategoria);
        categoria.setIconPath(iconPath);
        categoria.setId(1L);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(personalCategoriaRepository.findByUser(user)).thenReturn(List.of(categoria));

        personalCategoriaService.findAndDeleteCategoria(user.getEmail(), nombreCategoria, iconPath);

        verify(personalCategoriaRepository).deleteById(categoria.getId());
    }

    // Test para verificar si una categoría no existe
    @Test
    void testCheckIfNotExist() {
        // Aquí ahora se pasan ambos parámetros al constructor de CategoriaRequest
        CategoriaRequest categoriaRequest = new CategoriaRequest("Salud", "/icons/salud.png");

        PersonalCategoria categoria = new PersonalCategoria();
        categoria.setNombre("Comida");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(personalCategoriaRepository.findByUser(user)).thenReturn(List.of(categoria));

        boolean result = personalCategoriaService.checkIfNotExist(user.getEmail(), categoriaRequest);

        assertTrue(result);
    }

    // Test para verificar si una categoría ya existe
    @Test
    void testCheckIfNotExistCategoryExists() {
        CategoriaRequest categoriaRequest = new CategoriaRequest("Comida", "/icons/comida.png");

        PersonalCategoria categoria = new PersonalCategoria();
        categoria.setNombre("Comida");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(personalCategoriaRepository.findByUser(user)).thenReturn(List.of(categoria));

        boolean result = personalCategoriaService.checkIfNotExist(user.getEmail(), categoriaRequest);

        assertFalse(result);
    }

    // Test para verificar si el usuario no existe
    @Test
    void testGetPersonalCategoriaUserNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> personalCategoriaService.getPersonalCategoria(user.getEmail())
        );
        assertEquals("User not found", exception.getMessage());
    }
}
