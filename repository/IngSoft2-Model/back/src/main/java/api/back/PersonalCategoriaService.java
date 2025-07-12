package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PersonalCategoriaService {

    @Autowired
    private PersonalCategoriaRepository personalCategoriaRepository;

    @Autowired
    private UserRepository userRepository;

    public List<PersonalCategoria> getPersonalCategoria(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return personalCategoriaRepository.findByUser(user);
    }

    public PersonalCategoria addPersonalCategoria(String email, String nombre, String iconPath) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        PersonalCategoria categoria = new PersonalCategoria();
        categoria.setNombre(nombre);
        categoria.setIconPath(iconPath);
        categoria.setUser(user);
        return personalCategoriaRepository.save(categoria);
    }

    public void deletePersonalCategoria(Long id) {
        personalCategoriaRepository.deleteById(id);
    }

    public void findAndDeleteCategoria(String email, String nombre, String iconPath) {
        List<PersonalCategoria> categorias = getPersonalCategoria(email);
        for (PersonalCategoria item : categorias) {
            if (item.getNombre().equals(nombre)) {
                System.out.println("Found: " + item);
                deletePersonalCategoria(item.getId());
            }
        }
    }

    public boolean checkIfNotExist(String email, CategoriaRequest categoria) {
        List<PersonalCategoria> categorias = getPersonalCategoria(email);
        String nombreCategoria = categoria.getNombre().trim().replaceAll("\"", "");

        for (PersonalCategoria catPersonal : categorias) {
            if (catPersonal.getNombre() != null
                    && nombreCategoria.equalsIgnoreCase(catPersonal.getNombre().trim())) {
                return false;
            }
        }
        return true;
    }

    public void save(PersonalCategoria personalCategoria) {
        personalCategoriaRepository.save(personalCategoria);
    }

    public boolean isCategoriaValida(String email, String nombreCategoria) {
        List<String> defaultCategorias = List.of("Impuestos y Servicios", "Entretenimiento y Ocio", "Hogar y Mercado", "Antojos", "Electrodomesticos", "Clase", "Ingreso de Dinero", "Gasto Grupal");
        if (defaultCategorias.contains(nombreCategoria)) {
            return true;
        }
        List<PersonalCategoria> categorias = getPersonalCategoria(email);
        String nombreNormalizado = nombreCategoria.trim().replaceAll("\"", "");
    
        for (PersonalCategoria categoria : categorias) {
            if (categoria.getNombre() != null && 
                nombreNormalizado.equalsIgnoreCase(categoria.getNombre().trim())) {
                return true;
            }
        }
        return false;
    }

}
