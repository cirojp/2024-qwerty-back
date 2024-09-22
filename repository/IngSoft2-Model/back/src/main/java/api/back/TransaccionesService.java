package api.back;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransaccionesService {

    private final TransaccionesRepository transaccionesRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public TransaccionesService(TransaccionesRepository transaccionesRepository, UserRepository userRepository,
            UserService userService) {
        this.transaccionesRepository = transaccionesRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public List<Transacciones> getTransaccionesByUserId(Long userId) {
        return transaccionesRepository.findByUserIdOrderByFechaDesc(userId);
    }

    public Transacciones createTransaccion(Transacciones transaccion, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        transaccion.setUser(user);

        // Si no se proporciona una fecha, usamos la fecha actual
        if (transaccion.getFecha() == null) {
            transaccion.setFecha(LocalDate.now());
        }

        return transaccionesRepository.save(transaccion);
    }

    public Optional<Transacciones> getTransaccionById(Long id) {
        return transaccionesRepository.findById(id);
    }

    public void deleteTransaccion(Long id, String email) {
        System.out.println("Intentando eliminar transacción con ID: " + id + " para el usuario: " + email);

        Optional<Transacciones> optionalTransaccion = transaccionesRepository.findByIdAndUserEmail(id, email);
        if (optionalTransaccion.isEmpty()) {
            throw new TransaccionNotFoundException("Transacción no encontrada o no pertenece al usuario");
        }

        Transacciones transaccion = optionalTransaccion.get();
        System.out.println("Transacción encontrada: " + transaccion);

        transaccionesRepository.delete(transaccion);
        System.out.println("Transacción eliminada");
    }

    public Transacciones updateTransaccion(Long id, Transacciones transaccionActualizada, String email) {
        // Obtener el usuario autenticado por email
        User user = userService.findByEmail(email);

        // Buscar la transacción por id y asegurarse de que pertenezca al usuario
        Transacciones transaccion = transaccionesRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada o no pertenece al usuario"));

        // Actualizar los campos de la transacción
        transaccion.setMotivo(transaccionActualizada.getMotivo());
        transaccion.setValor(transaccionActualizada.getValor());
        transaccion.setFecha(transaccionActualizada.getFecha());
        transaccion.setDescripcion(transaccionActualizada.getDescripcion());
        transaccion.setTipoGasto(transaccionActualizada.getTipoGasto());

        // Guardar los cambios en la base de datos
        return transaccionesRepository.save(transaccion);
    }

}
