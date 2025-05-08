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
    private final UserService userService;
    private final TransaccionesService transaccionesService;

    public MonedaController(TransaccionesService transaccionesService, UserService userService) {
        this.transaccionesService = transaccionesService;
        this.userService = userService;
    }
    @GetMapping
    public ResponseEntity<List<Moneda>> getMonedas(Authentication authentication) {
        String email = authentication.getName();
        List<Moneda> monedas = monedaService.getMonedasByEmail(email);
        return ResponseEntity.ok(monedas);
    }

    @PostMapping
    public ResponseEntity<?> addMoneda(@RequestBody Map<String, Object> request, Authentication authentication) {
        String email = authentication.getName();
        String nombre = request.get("nombre").toString();
        //Double valor = Double.parseDouble(request.get("valor").toString());
        Double valor;
        try {
            valor = Double.parseDouble(request.get("valor").toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("El valor ingresado no es válido.");
        }
        if (valor < 0) {
            return ResponseEntity.badRequest().body("El valor no puede ser negativo.");
        }
        if (nombre.equals(null) || nombre.equals("") ) {
            return ResponseEntity.badRequest().body("El Nombre no puede ser null o vacio.");
        }
        // Validación: nombre no duplicado para ese usuario
        if (monedaService.monedaYaExiste(email, nombre)) {
            return ResponseEntity.badRequest().body("Ya existe una moneda con ese nombre para el usuario.");
        }
        Moneda nueva = monedaService.addMoneda(email, nombre, valor);
        return ResponseEntity.ok(nueva);
    }

    @PutMapping
    public ResponseEntity<?> updateMonedaPorNombre(
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        String email = authentication.getName();
        String nombreActual = request.get("nombreActual").toString();
        String nombreNuevo = request.get("nombreNuevo").toString();
        //Double valorNuevo = Double.parseDouble(request.get("valorNuevo").toString());
        Double valorNuevo;
        try {
            valorNuevo = Double.parseDouble(request.get("valorNuevo").toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("El valor ingresado no es válido.");
        }
        if (valorNuevo < 0) {
            return ResponseEntity.badRequest().body("El valor no puede ser negativo.");
        }
        if (nombreNuevo.equals(null) || nombreNuevo.equals("") ) {
            return ResponseEntity.badRequest().body("El Nombre no puede ser null o vacio.");
        }
        // Validación: nombre no duplicado para ese usuario
        if (monedaService.monedaYaExiste(email, nombreNuevo)) {
            return ResponseEntity.badRequest().body("Ya existe una moneda con ese nombre para el usuario.");
        }

        //List<Transacciones> transaccionesUser = transaccionesController.getTransaccionesByUser(authentication);
        User user = userService.findByEmail(email); 
        List<Transacciones> transaccionesUser = transaccionesService.getTransaccionesByUserId(user.getId());
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

    /*@DeleteMapping
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
    }*/

}
