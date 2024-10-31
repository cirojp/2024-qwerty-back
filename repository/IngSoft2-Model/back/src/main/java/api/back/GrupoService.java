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

    public Grupo crearGrupo(String nombre, User creadorGrupo) {
        Grupo grupo = new Grupo(nombre, creadorGrupo);
        grupo.setEstado(true); // defino el estado inicial como abierto

        return grupoRepository.save(grupo);
    }

    public List<Grupo> obtenerGruposPorUsuario(String usuarioEmail) {
        return grupoRepository.findByUsuariosEmail(usuarioEmail); 
    }
}
