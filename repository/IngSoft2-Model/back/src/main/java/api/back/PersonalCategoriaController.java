package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/personal-categoria")
public class PersonalCategoriaController {

    @Autowired
    private PersonalCategoriaService personalCategoriaService;

    @GetMapping
    public List<CategoriaRequest> getPersonalCategoria(Authentication authentication) {
        String email = authentication.getName();
        List<PersonalCategoria> categorias = personalCategoriaService.getPersonalCategoria(email);

        // Mapear las categorías a CategoriaRequest
        return categorias.stream()
                .map(cat -> new CategoriaRequest(cat.getNombre(), cat.getIconPath()))
                .collect(Collectors.toList());

    }

    @PostMapping
    public PersonalCategoria addPersonalCategoria(@RequestBody CategoriaRequest categoria,
            Authentication authentication) {
        String email = authentication.getName();
        // Quitar las comillas dobles y las llaves del texto si es necesario
        System.out.println(categoria.getIconPath());
        categoria.setNombre(categoria.getNombre().trim().replaceAll("\"", ""));
        return personalCategoriaService.addPersonalCategoria(email, categoria.getNombre(), categoria.getIconPath());
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePersonalCategoria(@RequestBody CategoriaRequest categoria,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            personalCategoriaService.findAndDeleteCategoria(email, categoria.getNombre(), categoria.getIconPath());
            return ResponseEntity.ok().build();
        } catch (TransaccionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping
    public ResponseEntity<Void> editPersonalCategoria(@RequestBody CategoriaRequest categoria,
            @RequestBody CategoriaRequest newCategoria, Authentication authentication) {
        try {
            String email = authentication.getName();
            List<PersonalCategoria> categorias = personalCategoriaService.getPersonalCategoria(email);
            for (PersonalCategoria item : categorias) {
                if (item.getNombre().equals(categoria.getNombre())) {
                    System.out.println("Found: " + item);
                    item.setNombre(newCategoria.getNombre());
                    item.setIconPath(newCategoria.getIconPath());
                }
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
