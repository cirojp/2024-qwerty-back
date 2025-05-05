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

    public TransaccionesPendientesController(TransaccionesPendientesService transaccionesPendientesService,
            UserService userService, RestTemplate restTemplate) {
        this.transaccionesPendientesService = transaccionesPendientesService;
        this.userService = userService;
        this.restTemplate = restTemplate;
    }
    @GetMapping("/user")
    public ResponseEntity<List<TransaccionesPendientesDTO>> getPendingTransaccionesByUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        /*List<TransaccionesPendientes> pendingTransactions = transaccionesPendientesService
                .getPendingTransaccionesByUserId(user.getId());

        return ResponseEntity.ok(pendingTransactions);*/
        List<TransaccionesPendientesDTO> pendingTransactionsDTO = transaccionesPendientesService
        .getPendingTransaccionesByUserId(user.getId())
        .stream()
        .map(TransaccionesPendientesDTO::new)
        .toList();

        return ResponseEntity.ok(pendingTransactionsDTO);
    }

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

    @PostMapping("/aceptada")
    public ResponseEntity<String> transaccionAceptada(@RequestParam String id_reserva, Authentication authentication) {
        return enviarNotificacionReserva(id_reserva, authentication, "aceptada");
    }

    @PostMapping("/rechazada")
    public ResponseEntity<String> transaccionRechazada(@RequestParam String id_reserva, Authentication authentication) {
        return enviarNotificacionReserva(id_reserva, authentication, "rechazada");
    }

    private ResponseEntity<String> enviarNotificacionReserva(String id_reserva, Authentication authentication,
            String status) {
        String email = authentication.getName();

        // Crear el cuerpo del JSON
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("id_reserva", id_reserva);
        body.put("reservationStatus", status);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        String url = "https://backendapi.fpenonori.com/reservation/confirm";

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        return ResponseEntity.ok(response.getBody());
    }

    @PostMapping("/askPayUser")
    public ResponseEntity<Void> postPaymentToUser(@RequestBody TransaccionRequest transaccion,
            Authentication authentication) {
        String email = authentication.getName();
        User usuario = userService.findByEmail(transaccion.getEmail());
        if (usuario != null) {
            TransaccionesPendientes transaccionPendiente = new TransaccionesPendientes(
                    transaccion.getValor(),
                    usuario,
                    transaccion.getMotivo(),
                    transaccion.getId_reserva(),
                    transaccion.getFecha(),
                    transaccion.getMonedaOriginal(),
                    transaccion.getMontoOriginal());
            transaccionPendiente.setSentByEmail(email);
            transaccionesPendientesService.save(transaccionPendiente);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
