package api.back;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UserService userService;

    public Grupo crearGrupo(String nombre, List<String> miembrosEmails, String creadorEmail) {
        Grupo grupo = new Grupo();
        grupo.setNombre(nombre);
        grupo.setEstado(true); // Puedes definir el estado inicial como abierto

        // Agregar creador y miembros
        User creador = userService.findByEmail(creadorEmail);
        grupo.getUsuarios().add(creador); // Agregar al creador al grupo

        for (String email : miembrosEmails) {
            User usuario = userService.findByEmail(email);
            grupo.getUsuarios().add(usuario);
        }

        return grupoRepository.save(grupo);
    }
}
