package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacciones")
// @CrossOrigin(origins = "http://127.0.0.1:5173/")
@CrossOrigin(origins = { "http://localhost:5173/", "http://127.0.0.1:5173/" })
public class TransaccionesController {

    private final TransaccionesService transaccionesService;
    private final UserService userService;

    @Autowired
    public TransaccionesController(TransaccionesService transaccionesService, UserService userService) {
        this.transaccionesService = transaccionesService;
        this.userService = userService;
    }

    @GetMapping("/user")
    public List<Transacciones> getTransaccionesByUser(Authentication authentication) {
        String email = authentication.getName(); // Obtenemos el email del usuario autenticado
        User user = userService.findByEmail(email); // Obtenemos el usuario por email
        return transaccionesService.getTransaccionesByUserId(user.getId()); // Llamamos al servicio con el ID del
                                                                            // usuario
    }

    @PostMapping
    public Transacciones createTransaccion(@RequestBody Transacciones transaccion, Authentication authentication) {
        String email = authentication.getName();
        return transaccionesService.createTransaccion(transaccion, email);
    }

    @GetMapping("/{id}")
    public Optional<Transacciones> getTransaccionById(@PathVariable Long id) {
        return transaccionesService.getTransaccionById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaccion(@PathVariable Long id, Authentication authentication) {
        try {
            String email = authentication.getName(); // Obtener el email del usuario autenticado
            transaccionesService.deleteTransaccion(id, email);
            return ResponseEntity.noContent().build();
        } catch (TransaccionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public Transacciones updateTransaccion(@PathVariable Long id, @RequestBody Transacciones transaccionActualizada,
            Authentication authentication) {
        String email = authentication.getName(); // Obtenemos el email del usuario autenticado
        return transaccionesService.updateTransaccion(id, transaccionActualizada, email);
    }

}
