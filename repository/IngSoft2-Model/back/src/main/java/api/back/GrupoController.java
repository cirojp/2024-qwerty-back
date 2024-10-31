package api.back;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = { "http://localhost:5173/", "http://127.0.0.1:5173" })
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @PostMapping("/crear")
    public Grupo crearGrupo(@RequestBody Map<String, Object> payload, Authentication authentication) {
        String nombre = (String) payload.get("nombre");
        List<String> miembrosEmails = (List<String>) payload.get("miembrosEmails");
        String creadorEmail = authentication.getName(); // Email del usuario autenticado

        return grupoService.crearGrupo(nombre, miembrosEmails, creadorEmail);
    }
}
