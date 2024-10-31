package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrupoTransaccionesService {

    @Autowired
    private GrupoTransaccionesRepository grupoTransaccionesRepository;

    public GrupoTransacciones save(GrupoTransacciones grupoTransacciones) {
        return grupoTransaccionesRepository.save(grupoTransacciones);
    }
}
