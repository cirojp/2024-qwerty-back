package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MonedaService {

    @Autowired
    private MonedaRepository monedaRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Moneda> getMonedasByEmail(String email) {
        return monedaRepository.findByUserEmail(email);
    }

    public Moneda addMoneda(String email, String nombre, Double valor) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Moneda moneda = new Moneda();
        moneda.setNombre(nombre);
        moneda.setValor(valor);
        moneda.setUser(user);
        System.out.println(moneda);
        return monedaRepository.save(moneda);
    }

    public Moneda updateMoneda(String email, Long id, String nombre, Double valor) {
        Moneda moneda = monedaRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new RuntimeException("Moneda no encontrada o no pertenece al usuario."));
    
        moneda.setNombre(nombre);
        moneda.setValor(valor);
        return monedaRepository.save(moneda);
    }
    
    public void deleteMoneda(String email, Long id) {
        Moneda moneda = monedaRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new RuntimeException("Moneda no encontrada o no pertenece al usuario."));
        
        monedaRepository.delete(moneda);
    }
}
