package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransaccionesService {

    private final TransaccionesRepository transaccionesRepository;
    private final UserRepository userRepository;

    @Autowired
    public TransaccionesService(TransaccionesRepository transaccionesRepository, UserRepository userRepository) {
        this.transaccionesRepository = transaccionesRepository;
        this.userRepository = userRepository;
    }

    public List<Transacciones> getTransaccionesByUserId(Long userId) {
        return transaccionesRepository.findByUserId(userId);
    }

    public Transacciones createTransaccion(Transacciones transaccion, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        transaccion.setUser(user);

        // Si no se proporciona una fecha, usamos la fecha actual
        if (transaccion.getFecha() == null) {
            transaccion.setFecha(LocalDateTime.now());
        }

        return transaccionesRepository.save(transaccion);
    }

    public Optional<Transacciones> getTransaccionById(Long id) {
        return transaccionesRepository.findById(id);
    }

    public void deleteTransaccion(Long id) {
        transaccionesRepository.deleteById(id);
    }

    public Transacciones updateTransaccion(Long id, Transacciones transaccionActualizada) {
        return transaccionesRepository.findById(id)
                .map(transaccion -> {
                    transaccion.setValor(transaccionActualizada.getValor());
                    transaccion.setMotivo(transaccionActualizada.getMotivo());
                    transaccion.setFecha(transaccionActualizada.getFecha());  // Asegúrate de actualizar la fecha también
                    return transaccionesRepository.save(transaccion);
                })
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));
    }
}
