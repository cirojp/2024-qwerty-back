package api.back;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

class PersonalCategoriaControllerTest {

    @Mock
    private PersonalCategoriaService personalCategoriaService;

    @Mock
    private TransaccionesController transaccionesController;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PersonalCategoriaController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPersonalCategoria() {
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);

        PersonalCategoria pc1 = new PersonalCategoria();
        pc1.setNombre("Salud");
        pc1.setIconPath("/icons/salud.png");
        PersonalCategoria pc2 = new PersonalCategoria();
        pc2.setNombre("Transporte");
        pc2.setIconPath("/icons/transporte.png");

        when(personalCategoriaService.getPersonalCategoria(email))
            .thenReturn(List.of(pc1, pc2));

        List<CategoriaRequest> result = controller.getPersonalCategoria(authentication);

        assertEquals(2, result.size());
        assertEquals("Salud", result.get(0).getNombre());
        assertEquals("/icons/salud.png", result.get(0).getIconPath());
        assertEquals("Transporte", result.get(1).getNombre());
    }

    @Test
    void testAddPersonalCategoria_Success() {
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);
        CategoriaRequest request = new CategoriaRequest("Ocio", "/icons/ocio.png");
        when(personalCategoriaService.checkIfNotExist(email, request)).thenReturn(true);
        ResponseEntity<CategoriaRequest> response =
            controller.addPersonalCategoria(request, authentication);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(request.getNombre(), response.getBody().getNombre());
        assertEquals(request.getIconPath(), response.getBody().getIconPath());
        verify(personalCategoriaService).addPersonalCategoria(email, "Ocio", "/icons/ocio.png");
    }


    @Test
    void testAddPersonalCategoria_Conflict() {
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);

        CategoriaRequest request = new CategoriaRequest("Salud", "/icons/salud.png");
        when(personalCategoriaService.checkIfNotExist(email, request)).thenReturn(false);

        ResponseEntity<CategoriaRequest> response =
            controller.addPersonalCategoria(request, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
        verify(personalCategoriaService, never()).addPersonalCategoria(any(), any(), any());
    }

    @Test
    void testDeletePersonalCategoria_Success() {
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);

        CategoriaRequest request = new CategoriaRequest("Comida", "/icons/comida.png");

        Transacciones t1 = new Transacciones(); t1.setCategoria("Comida"); t1.setId(10L);
        Transacciones t2 = new Transacciones(); t2.setCategoria("Otro"); t2.setId(11L);
        when(transaccionesController.getTransaccionesByUser(authentication))
            .thenReturn(List.of(t1, t2));

        // Llamada al endpoint
        ResponseEntity<Void> response =
            controller.deletePersonalCategoria(request, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // t1 coincide y debe actualizarse
        verify(transaccionesController).updateTransaccion(10L, t1, authentication);
        // borrar categoría
        verify(personalCategoriaService).findAndDeleteCategoria(email, "Comida", "/icons/comida.png");
    }

    @Test
    void testDeletePersonalCategoria_NotFound() {
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);

        CategoriaRequest request = new CategoriaRequest("NoExiste", "/icons/none.png");
        // Hacemos que el servicio lance excepción
        doThrow(new TransaccionNotFoundException("no")).when(personalCategoriaService)
            .findAndDeleteCategoria(email, "NoExiste", "/icons/none.png");

        ResponseEntity<Void> response =
            controller.deletePersonalCategoria(request, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testEditPersonalCategoria_Success() {
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);

        // Mock de categorías existentes
        PersonalCategoria pc = new PersonalCategoria();
        pc.setNombre("Vieja");
        pc.setIconPath("/icons/vieja.png");
        pc.setId(5L);
        when(personalCategoriaService.getPersonalCategoria(email))
            .thenReturn(List.of(pc));

        // Permitimos el cambio
        CategoriaRequest newCat = new CategoriaRequest("Nueva", "/icons/nueva.png");
        when(personalCategoriaService.checkIfNotExist(email, newCat)).thenReturn(true);

        // Mock transacciones: una que coincide y otra no
        Transacciones tr = new Transacciones();
        tr.setCategoria("Vieja");
        tr.setId(7L);
        when(transaccionesController.getTransaccionesByUser(authentication))
            .thenReturn(List.of(tr));

        // Llamamos al endpoint
        ResponseEntity<Void> response =
            controller.editPersonalCategoria("Vieja", newCat, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Debe guardar el cambio en el repositorio
        verify(personalCategoriaService).save(argThat(saved -> 
            saved.getNombre().equals("Nueva") &&
            saved.getIconPath().equals("/icons/nueva.png")
        ));
        // Debe actualizar la transacción
        verify(transaccionesController).updateTransaccion(7L, tr, authentication);
    }

    @Test
    void testEditPersonalCategoria_NotFound() {
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);

        // No existe la categoría "X"
        when(personalCategoriaService.getPersonalCategoria(email))
            .thenReturn(List.of());

        CategoriaRequest newCat = new CategoriaRequest("Algo", "/icons/a.png");
        when(personalCategoriaService.checkIfNotExist(email, newCat)).thenReturn(true);

        ResponseEntity<Void> response =
            controller.editPersonalCategoria("X", newCat, authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testEditPersonalCategoria_Conflict() {
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);

        // checkIfNotExist false → conflicto
        CategoriaRequest newCat = new CategoriaRequest("Dup", "/icons/d.png");
        when(personalCategoriaService.checkIfNotExist(email, newCat)).thenReturn(false);

        ResponseEntity<Void> response =
            controller.editPersonalCategoria("Vieja", newCat, authentication);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(personalCategoriaService, never()).save(any());
    }

    @Test
    void testEditPersonalCategoria_Exception() {
        String email = "user@example.com";
        when(authentication.getName()).thenReturn(email);

        // Forzamos excepción en getPersonalCategoria
        when(personalCategoriaService.getPersonalCategoria(email))
            .thenThrow(new RuntimeException("boom"));

        CategoriaRequest newCat = new CategoriaRequest("X", "/icons/x.png");
        ResponseEntity<Void> response =
            controller.editPersonalCategoria("X", newCat, authentication);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
