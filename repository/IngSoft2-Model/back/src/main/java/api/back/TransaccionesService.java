package api.back;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
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

        // Calcular siguiente ejecución si es recurrente
        if (transaccion.getFrecuenciaRecurrente() != null && !transaccion.getFrecuenciaRecurrente().isEmpty()) {
            LocalDate siguienteEjecucion = calcularSiguienteEjecucion(transaccion.getFecha(), transaccion.getFrecuenciaRecurrente());
            transaccion.setSiguienteEjecucion(siguienteEjecucion);
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
        transaccion.setCategoria(transaccionActualizada.getCategoria());
        transaccion.setTipoGasto(transaccionActualizada.getTipoGasto());
        transaccion.setMonedaOriginal(transaccionActualizada.getMonedaOriginal());
        transaccion.setMontoOriginal(transaccionActualizada.getMontoOriginal());
        if (transaccionActualizada.getFrecuenciaRecurrente()!= null) {
            transaccion.setFrecuenciaRecurrente(transaccionActualizada.getFrecuenciaRecurrente());
            transaccion.setSiguienteEjecucion(calcularSiguienteEjecucion(transaccionActualizada.getFecha(), transaccionActualizada.getFrecuenciaRecurrente()));
        }

        // Guardar los cambios en la base de datos
        return transaccionesRepository.save(transaccion);
    }

    public List<Transacciones> getTransaccionesByUserIdAndCategory(Long userId, String categoria) {
        return transaccionesRepository.findByUserIdAndCategoriaOrderByFechaDesc(userId, categoria);
    }

    public List<Transacciones> getTransaccionesFiltradas(Long userId, String categoria, Integer anio, Integer mes) {
        LocalDate startDate = null;
        LocalDate endDate = null;
        if (anio == null && mes != null) {
            anio = 2024;
        }
        // Si la categoría no es null o "Todas" y anio y mes son null
        if (categoria != null && !categoria.equals("Todas") && anio == null && mes == null) {
            return transaccionesRepository.findByUserIdAndCategoriaOrderByFechaDesc(userId, categoria);
        }
        // Si la categoría no es null ni "Todas" y además mes o anio no son null
        else if (categoria != null && !categoria.equals("Todas") && (anio != null || mes != null)) {
            if (anio != null && mes != null) {
                // Si ambos son proporcionados, calcula el rango de fechas
                startDate = LocalDate.of(anio, mes, 1);
                endDate = startDate.plusMonths(1).minusDays(1);
                return transaccionesRepository.findByUserIdAndCategoriaAndFechaBetween(userId, categoria, startDate, endDate);
            } else if (anio != null) {
                // Si solo el año es proporcionado, establece el rango de fechas para todo el año
                startDate = LocalDate.of(anio, 1, 1);
                endDate = LocalDate.of(anio, 12, 31);
                return transaccionesRepository.findByUserIdAndCategoriaAndFechaBetween(userId, categoria, startDate, endDate);
            } else if (mes != null) {
                // Si solo el mes es proporcionado, debes decidir cómo manejarlo
                // Aquí puedes decidir no realizar ninguna consulta o lanzar una excepción
            }
        }
        // Si la categoría es null o "Todas" pero anio o mes no son null
        else if ((categoria == null || categoria.equals("Todas")) && (anio != null || mes != null)) {
            if (anio != null && mes != null) {
                // Si ambos son proporcionados, calcula el rango de fechas
                startDate = LocalDate.of(anio, mes, 1);
                endDate = startDate.plusMonths(1).minusDays(1);
                return transaccionesRepository.findByUserIdAndFechaBetween(userId, startDate, endDate);
            } else if (anio != null) {
                // Si solo el año es proporcionado, establece el rango de fechas para todo el año
                startDate = LocalDate.of(anio, 1, 1);
                endDate = LocalDate.of(anio, 12, 31);
                return transaccionesRepository.findByUserIdAndFechaBetween(userId, startDate, endDate);
            } else if (mes != null) {
                // Si solo el mes es proporcionado, puedes decidir no realizar ninguna consulta o lanzar una excepción
            }
        }
        return transaccionesRepository.findByUserIdOrderByFechaDesc(userId);
    
        // Si no se cumplen condiciones, puedes devolver una lista vacía o lanzar una excepción
        }
    
        //@Scheduled(cron = "0 0 0 * * ?") // Se ejecuta todos los días a medianoche
        @Scheduled(cron = "0 * * * * ?") // Se ejecuta todos los días a medianoche
        public void procesarTransaccionesRecurrentes() {
            
            LocalDate hoy = LocalDate.now();
            List<Transacciones> transaccionesRecurrentes = transaccionesRepository.findBySiguienteEjecucion(hoy);
            System.out.println(hoy);
            System.out.println(transaccionesRecurrentes.isEmpty());
            for (Transacciones transaccion : transaccionesRecurrentes) {
                
                // Crear nueva transacción con los mismos datos
                Transacciones nuevaTransaccion = new Transacciones();
                nuevaTransaccion.setCategoria(transaccion.getCategoria());
                nuevaTransaccion.setFecha(hoy);
                nuevaTransaccion.setMotivo(transaccion.getMotivo());
                nuevaTransaccion.setTipoGasto(transaccion.getTipoGasto());
                nuevaTransaccion.setUser(transaccion.getUser());
                nuevaTransaccion.setValor(transaccion.getValor());
                nuevaTransaccion.setMonedaOriginal(transaccion.getMonedaOriginal());
                nuevaTransaccion.setMontoOriginal(transaccion.getMontoOriginal());
                
                transaccionesRepository.save(nuevaTransaccion);
                System.out.println("🚀 🚀 🚀 🚀 🚀 🚀 🚀 ");
                System.out.println(transaccion);
                System.out.println("🚀 🚀 🚀 🚀 🚀 🚀 🚀 ");
                System.out.println(nuevaTransaccion);
                // Calcular nueva fecha de ejecución
                LocalDate nuevaFecha = calcularSiguienteEjecucion(hoy, transaccion.getFrecuenciaRecurrente());
                transaccion.setSiguienteEjecucion(nuevaFecha);
                transaccionesRepository.save(transaccion);
            }
        }

        private LocalDate calcularSiguienteEjecucion(LocalDate fecha, String frecuencia) {
            switch (frecuencia.toLowerCase()) {
                case "diariamente":
                    return fecha.plusDays(1);
                case "semanalmente":
                    return fecha.plusWeeks(1);
                case "mensualmente":
                    return fecha.plusMonths(1);
                case "anualmente":
                    return fecha.plusYears(1);
                default:
                    throw new IllegalArgumentException("Frecuencia no válida: " + frecuencia);
            }
        }
        
        public List<Transacciones> getTransaccionesRecurrentes(Long userId) {
            return transaccionesRepository.findByUserIdAndFrecuenciaRecurrenteIsNotNull(userId);
        }
        

}
