package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate; // Importa LocalDate
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = { "http://localhost:5173/", "http://127.0.0.1:5173" })
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransaccionesPendientesService transaccionesPendientesService; // Asegúrate de tener este servicio inyectado

    @PostMapping("/crear")
    public Grupo crearGrupo(@RequestBody Map<String, Object> payload, Authentication authentication) {
        String nombre = (String) payload.get("nombre");
        List<String> miembrosEmails = (List<String>) payload.get("usuarios");
        String creadorEmail = authentication.getName(); // Email del usuario autenticado

        if (miembrosEmails == null) {
            miembrosEmails = new ArrayList<>(); // Asegúrate de que la lista no sea nula
        }

        // Crea el grupo
        User usuarioCreador = userService.findByEmail(creadorEmail);
        Grupo grupo = grupoService.crearGrupo(nombre, usuarioCreador);

        // Crea una transacción pendiente para cada miembro del grupo
        for (String email : miembrosEmails) {
            User usuario = userService.findByEmail(email);
            LocalDate fechaHoy = LocalDate.now();
            TransaccionesPendientes transaccionPendiente = new TransaccionesPendientes(0.0, usuario, "", "Grupo", fechaHoy);
            // Establecer el correo del creador como sentByEmail
            transaccionPendiente.setSentByEmail(creadorEmail);
        }
        return grupo;
    }

    @GetMapping("/mis-grupos")
    public List<Grupo> obtenerGruposDelUsuario(Authentication authentication) {
        String usuarioEmail = authentication.getName(); // Email del usuario autenticado
        return grupoService.obtenerGruposPorUsuario(usuarioEmail);
    }
}
