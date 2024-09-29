package api.back;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/public")
public class ExternalApiController {

    @Autowired
    UserService userService;

    @PostMapping("/sendTransaccion")
    public ResponseEntity<String> postPendingTransaccion(@RequestParam String valor, @RequestParam String email) {
        User usuario = userService.findByEmail(email);
        if (usuario != null) {
            TransaccionesPendientes transaccionPendiente = new TransaccionesPendientes(Double.parseDouble(valor),
                    usuario, "Clase",
                    "Clases",
                    LocalDate.now());
            return ResponseEntity.ok().body(valor);
        } else {
            return ResponseEntity.badRequest().body("Usuario no Registrado");
        }
    }

}
