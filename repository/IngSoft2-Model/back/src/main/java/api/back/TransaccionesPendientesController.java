package api.back;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transaccionesPendientes")
@CrossOrigin(origins = { "http://localhost:5173/", "http://127.0.0.1:5173" })
public class TransaccionesPendientesController {

    private final TransaccionesPendientesService transaccionesPendientesService;
    private final UserService userService;

    public TransaccionesPendientesController(TransaccionesPendientesService transaccionesPendientesService, UserService userService) {
        this.transaccionesPendientesService = transaccionesPendientesService;
        this.userService = userService;
    }

    // Endpoint para obtener todas las transacciones pendientes del usuario autenticado
    @GetMapping("/user")
    public ResponseEntity<List<TransaccionesPendientes>> getPendingTransaccionesByUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email); // Obtener el usuario por email
        List<TransaccionesPendientes> pendingTransactions = transaccionesPendientesService.getPendingTransaccionesByUserId(user.getId());

        // Retornar una lista vacía si no hay transacciones pendientes
        return ResponseEntity.ok(pendingTransactions);
    }

    // Endpoint para eliminar una transacción pendiente por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePendingTransaccion(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        try {
            transaccionesPendientesService.deletePendingTransaccion(id, user.getId());
            return ResponseEntity.noContent().build();
        } catch (TransaccionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
