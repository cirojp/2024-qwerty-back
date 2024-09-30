package api.back;

import org.springframework.stereotype.Service;

@Service
public class TransaccionesPendientesService {

    private final TransaccionesPendientesRepository transaccionesPendientesRepository;

    public TransaccionesPendientesService(TransaccionesPendientesRepository transaccionesPendientesRepository) {
        this.transaccionesPendientesRepository = transaccionesPendientesRepository;
    }

    public TransaccionesPendientes save(TransaccionesPendientes transaccionPendiente) {
        return transaccionesPendientesRepository.save(transaccionPendiente);
    }
}
