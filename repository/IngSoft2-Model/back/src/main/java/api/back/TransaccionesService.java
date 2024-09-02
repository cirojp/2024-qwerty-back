package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TransaccionesService {

    private final TransaccionesRepository transaccionesRepository;

    @Autowired
    public TransaccionesService(TransaccionesRepository transaccionesRepository) {
        this.transaccionesRepository = transaccionesRepository;
    }

    public List<Transacciones> getAllTransacciones() {
        return transaccionesRepository.findAll();
    }

    public Transacciones createTransaccion(Transacciones transaccion) {
        return transaccionesRepository.save(transaccion);
    }

    public Optional<Transacciones> getTransaccionById(Integer id) {
        return transaccionesRepository.findById(id);
    }

    public void deleteTransaccion(Integer id) {
        transaccionesRepository.deleteById(id);
    }

    public Transacciones updateTransaccion(Integer id, Transacciones transaccionActualizada) {
        return transaccionesRepository.findById(id)
                .map(transaccion -> {
                    transaccion.setValor(transaccionActualizada.getValor());
                    transaccion.setMotivo(transaccionActualizada.getMotivo());
                    return transaccionesRepository.save(transaccion);
                })
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada"));
    }
}
