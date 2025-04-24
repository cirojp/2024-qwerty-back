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
    public List<Moneda> getMonedas(Authentication authentication) {
        String email = authentication.getName();
        return monedaService.getMonedasByEmail(email);
    }

    @PostMapping
    public ResponseEntity<Moneda> addMoneda(@RequestBody Map<String, Object> request, Authentication authentication) {
        String email = authentication.getName();
        String nombre = request.get("nombre").toString();
        Double valor = Double.parseDouble(request.get("valor").toString());
        Moneda nueva = monedaService.addMoneda(email, nombre, valor);
        return ResponseEntity.ok(nueva);
    }
}
