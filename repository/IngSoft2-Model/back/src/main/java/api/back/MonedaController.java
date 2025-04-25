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

        Moneda actualizada = monedaService.updateMonedaPorNombre(email, nombreActual, nombreNuevo, valorNuevo);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMoneda(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();
        monedaService.deleteMoneda(email, id);
        return ResponseEntity.noContent().build();
    }
}
