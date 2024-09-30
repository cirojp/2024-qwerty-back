package api.back;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transaccionesPendientes")
@CrossOrigin(origins = { "http://localhost:5173/", "http://127.0.0.1:5173" })
public class TransaccionesPendientesController {

    private final TransaccionesPendientesService transaccionesPendientesService;
    private final UserService userService;
    private final RestTemplate restTemplate;

    public TransaccionesPendientesController(TransaccionesPendientesService transaccionesPendientesService, UserService userService, RestTemplate restTemplate) {
        this.transaccionesPendientesService = transaccionesPendientesService;
        this.userService = userService;
        this.restTemplate = restTemplate;
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

    /*@PostMapping("/aceptada")
    public ResponseEntity<String> transaccionAceptada(@RequestParam String id_reserva, Authentication authentication) {
        return enviarNotificacionReserva(id_reserva, authentication, "aceptada");
    }

    // Endpoint para transacción rechazada
    @PostMapping("/rechazada")
    public ResponseEntity<String> transaccionRechazada(@RequestParam String id_reserva, Authentication authentication) {
        return enviarNotificacionReserva(id_reserva, authentication, "rechazada");
    }

    // Método para enviar la notificación de reserva a la URL externa
    private ResponseEntity<String> enviarNotificacionReserva(String id_reserva, Authentication authentication, String status) {
        String email = authentication.getName();  // Obtener el email del usuario autenticado

        // Crear el cuerpo del JSON
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("id_reserva", id_reserva);
        body.put("reservationStatus", status);

        // Establecer headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Crear la request con el cuerpo y los headers
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // URL de la aplicación del otro grupo
        String url = "https://aplicacionDelOtroGrupo.com";

        // Hacer la petición POST
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        // Devolver la respuesta del servidor
        return ResponseEntity.ok(response.getBody());
    }*/
}
