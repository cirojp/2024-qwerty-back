package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personal-moneda")
public class MonedaController {

    @Autowired
    private MonedaService monedaService;
    @Autowired
    private TransaccionesController transaccionesController;

    @GetMapping
    public ResponseEntity<List<Moneda>> getMonedas(Authentication authentication) {
        String email = authentication.getName();
        List<Moneda> monedas = monedaService.getMonedasByEmail(email);
        return ResponseEntity.ok(monedas);
    }

    @PostMapping
    public ResponseEntity<Moneda> addMoneda(@RequestBody Map<String, Object> request, Authentication authentication) {
        String email = authentication.getName();
        String nombre = request.get("nombre").toString();
        Double valor = Double.parseDouble(request.get("valor").toString());
        Moneda nueva = monedaService.addMoneda(email, nombre, valor);
        return ResponseEntity.ok(nueva);
    }

    @PutMapping
    public ResponseEntity<Moneda> updateMonedaPorNombre(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        String email = authentication.getName();
        String nombreActual = request.get("nombreActual").toString();
        String nombreNuevo = request.get("nombreNuevo").toString();
        Double valorNuevo = Double.parseDouble(request.get("valorNuevo").toString());

        List<Transacciones> transaccionesUser = transaccionesController.getTransaccionesByUser(authentication);
        for (Transacciones transaccion : transaccionesUser) {
            String moneda = transaccion.getMonedaOriginal();
            if (moneda != null && moneda.equals(nombreActual)) {
                transaccion.setMonedaOriginal(nombreNuevo);
                transaccionesController.updateTransaccion(transaccion.getId(), transaccion, authentication);
            }
        }

        Moneda actualizada = monedaService.updateMonedaPorNombre(email, nombreActual, nombreNuevo, valorNuevo);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMonedaPorNombre(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        String email = authentication.getName();
        String nombre = request.get("nombre").toString();

        List<Transacciones> transaccionesUser = transaccionesController.getTransaccionesByUser(authentication);
        for (Transacciones transaccion : transaccionesUser) {
            String moneda = transaccion.getMonedaOriginal();
            if (moneda != null && moneda.equals(nombre)) {
                transaccion.setMonedaOriginal("ARG");
                transaccion.setMontoOriginal(transaccion.getValor());
                transaccionesController.updateTransaccion(transaccion.getId(), transaccion, authentication);
            }
        }
        
        monedaService.deleteMonedaPorNombre(email, nombre);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

}
