package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/personal-tipo-gasto")
public class PersonalTipoGastoController {

    @Autowired
    private PersonalTipoGastoService personalTipoGastoService;

    @GetMapping
    public List<PersonalTipoGasto> getPersonalTipoGastos(Authentication authentication) {
        String email = authentication.getName();
        return personalTipoGastoService.getPersonalTipoGastos(email);
    }

    @PostMapping
    public PersonalTipoGasto addPersonalTipoGasto(@RequestBody String nombre, Authentication authentication) {
        String email = authentication.getName();
        // Quitar las comillas dobles y las llaves del texto si es necesario
        nombre = nombre.trim().replaceAll("\"", "");
        return personalTipoGastoService.addPersonalTipoGasto(email, nombre);
    }
}