package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PersonalTipoGastoService {
    
    @Autowired
    private PersonalTipoGastoRepository personalTipoGastoRepository;

    @Autowired
    private UserRepository userRepository;

    public List<PersonalTipoGasto> getPersonalTipoGastos(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return personalTipoGastoRepository.findByUser(user);
    }

    public PersonalTipoGasto addPersonalTipoGasto(String email, String nombre) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        PersonalTipoGasto tipoGasto = new PersonalTipoGasto();
        tipoGasto.setNombre(nombre);
        tipoGasto.setUser(user);
        return personalTipoGastoRepository.save(tipoGasto);
    }

    public void deletePersonalTipoGasto(Long id) {
        personalTipoGastoRepository.deleteById(id);
    }
    
    // si dsps necesitamos, agregamos editar
}
