package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personal-tipo-gasto")
public class PersonalTipoGastoController {

    @Autowired
    private PersonalTipoGastoService personalTipoGastoService;
    @Autowired
    private TransaccionesController transaccionesController;
    private final UserService userService;
    private final TransaccionesService transaccionesService;

    public PersonalTipoGastoController(TransaccionesService transaccionesService, UserService userService) {
        this.transaccionesService = transaccionesService;
        this.userService = userService;
    }
    @GetMapping
    public List<PersonalTipoGastoDTO> getPersonalTipoGastos(Authentication authentication) {
        String email = authentication.getName();
        List<PersonalTipoGasto> tipoGastos =  personalTipoGastoService.getPersonalTipoGastos(email);
        return tipoGastos.stream()
            .map(PersonalTipoGastoDTO::new)
            .toList();
    }

    @PostMapping
    public ResponseEntity<?>  addPersonalTipoGasto(@RequestBody String nombre, Authentication authentication) {
        try {
            String email = authentication.getName();
            // Quitar las comillas dobles y las llaves del texto si es necesario
            nombre = nombre.trim().replaceAll("\"", "");
            PersonalTipoGasto nuevo =  personalTipoGastoService.addPersonalTipoGasto(email, nombre);
            return ResponseEntity.ok(new PersonalTipoGastoDTO(nuevo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/editar")
    public ResponseEntity<?> updatePersonalTipoGasto(@RequestBody Map<String, String> requestBody,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            String nombreActual = requestBody.get("nombreActual").trim().replaceAll("\"", "");
            String nombreNuevo = requestBody.get("nombreNuevo").trim().replaceAll("\"", "");
            User user = userService.findByEmail(email); 
            List<Transacciones> transaccionesUser = transaccionesService.getTransaccionesByUserId(user.getId());
            //List<Transacciones> transaccionesUser = transaccionesController.getTransaccionesByUser(authentication);
            for (Transacciones transaccion : transaccionesUser) {
                String tipoGasto = transaccion.getTipoGasto();
                if (tipoGasto != null && tipoGasto.equals(nombreActual)) {
                    transaccion.setTipoGasto(nombreNuevo);
                    transaccionesController.updateTransaccion(transaccion.getId(), transaccion, authentication);
                }
            }
            PersonalTipoGasto actualizado =  personalTipoGastoService.updatePersonalTipoGasto(email, nombreActual, nombreNuevo);
            return ResponseEntity.ok(new PersonalTipoGastoDTO(actualizado));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el tipo de gasto: " + e.getMessage());
        }
    }

    // Endpoint para eliminar un PersonalTipoGasto por nombre
    @PostMapping("/eliminar")
    public ResponseEntity<Void> deletePersonalTipoGasto(@RequestBody String nombre, Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email); 
        List<Transacciones> transaccionesUser = transaccionesService.getTransaccionesByUserId(user.getId());
        //List<Transacciones> transaccionesUser = transaccionesController.getTransaccionesByUser(authentication);
        nombre = nombre.trim().replaceAll("\"", "");
        for (Transacciones transaccion : transaccionesUser) {
            String tipoGasto = transaccion.getTipoGasto();
            if (tipoGasto != null && tipoGasto.equals(nombre)) {
                transaccion.setTipoGasto("Otros");
                transaccionesController.updateTransaccion(transaccion.getId(), transaccion, authentication);
            }
        }
        personalTipoGastoService.deletePersonalTipoGastoByName(email, nombre);
        return ResponseEntity.ok().build();
    }

    // Clase interna para manejar los datos de edición (nombre actual y nuevo)
    public static class EditRequestBody {
        private String nombreActual;
        private String nombreNuevo;

        public String getNombreActual() {
            return nombreActual;
        }

        public void setNombreActual(String nombreActual) {
            this.nombreActual = nombreActual;
        }

        public String getNombreNuevo() {
            return nombreNuevo;
        }

        public void setNombreNuevo(String nombreNuevo) {
            this.nombreNuevo = nombreNuevo;
        }
    }
}