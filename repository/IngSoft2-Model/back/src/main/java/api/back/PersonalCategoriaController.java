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
@RequestMapping("/api/personal-categoria")
public class PersonalCategoriaController {

    @Autowired
    private PersonalCategoriaService personalCategoriaService;

    @GetMapping
    public List<PersonalCategoria> getPersonalCategoria(Authentication authentication) {
        String email = authentication.getName();
        return personalCategoriaService.getPersonalCategoria(email);
    }

    @PostMapping
    public PersonalCategoria addPersonalCategoria(@RequestBody CategoriaRequest categoria, Authentication authentication) {
        String email = authentication.getName();
        // Quitar las comillas dobles y las llaves del texto si es necesario
        System.out.println(categoria.getIconPath());
        categoria.setNombre(categoria.getNombre().trim().replaceAll("\"", ""));
        return personalCategoriaService.addPersonalCategoria(email, categoria.getNombre(), categoria.getIconPath());
    }
}
