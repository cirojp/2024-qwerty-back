package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

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

    public Moneda updateMonedaPorNombre(String email, String nombreActual, String nombreNuevo, Double valorNuevo) {
        Optional<Moneda> monedaOpt = monedaRepository.findByUserEmailAndNombre(email, nombreActual);
        
        if (monedaOpt.isEmpty()) {
            throw new RuntimeException("Moneda no encontrada");
        }
    
        Moneda moneda = monedaOpt.get();
        moneda.setNombre(nombreNuevo);
        moneda.setValor(valorNuevo);
        return monedaRepository.save(moneda);
    }
    
    public void deleteMonedaPorNombre(String email, String nombre) {
        Moneda moneda = monedaRepository
                .findByUserEmailAndNombre(email, nombre)
                .orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
    
        monedaRepository.delete(moneda);
    }

    public boolean isMonedaValida(String email, String nombreMoneda) {
        List<String> defaultMonedas = List.of("ARG");
        if (defaultMonedas.contains(nombreMoneda)) {
            return true;
        }
        return monedaRepository.findByUserEmailAndNombre(email, nombreMoneda).isPresent();
    }
}
