package api.back;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@EnableScheduling
@Service
public class TransaccionesService {

    private final TransaccionesRepository transaccionesRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    private PersonalTipoGastoService personalTipoGastoService;
    @Autowired
    private PersonalCategoriaService personalCategoriaService;

    public TransaccionesService(TransaccionesRepository transaccionesRepository, UserRepository userRepository,
            UserService userService) {
        this.transaccionesRepository = transaccionesRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public List<Transacciones> getTransaccionesByUserId(Long userId) {
        for (Transacciones t : transaccionesRepository.findByUserIdOrderByFechaDesc(userId)) {
            System.out.println("ID: " + t.getId() + " Frecuencia: '" + t.getFrecuenciaRecurrente() + "'");
        }
        return transaccionesRepository.findByUserIdOrderByFechaDesc(userId)
            .stream()
            .filter(t -> t.getFrecuenciaRecurrente() == null || t.getFrecuenciaRecurrente().isEmpty() || "".equals(t.getFrecuenciaRecurrente()))
            .collect(Collectors.toList());
    }

    public Transacciones createTransaccion(Transacciones transaccion, String email) {
        // Validaciones
        if (transaccion.getMotivo() == null || transaccion.getMotivo().trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo no puede estar vacío.");
        }
        if (transaccion.getValor() == null || transaccion.getValor() < 0) {
            throw new IllegalArgumentException("El valor no puede ser nulo ni menor a cero.");
        }
        if (transaccion.getMontoOriginal() == null || transaccion.getMontoOriginal() < 0) {
            throw new IllegalArgumentException("El monto original no puede ser nulo ni menor a cero.");
        }
        if (transaccion.getTipoGasto() == null || transaccion.getTipoGasto().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de gasto no puede estar vacío.");
        }
        if (transaccion.getCategoria() == null || transaccion.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría no puede estar vacía.");
        }
        if (transaccion.getMonedaOriginal() == null || transaccion.getMonedaOriginal().trim().isEmpty()) {
            throw new IllegalArgumentException("La moneda original no puede estar vacía.");
        }
        // Acá deberías también verificar que  categoría y moneda existan:
        // ejemplo: tipoGastoService.existsByNombre(transaccion.getTipoGasto())
        //         if (!existe) throw new IllegalArgumentException("El tipo de gasto no existe.");
        if (!personalTipoGastoService.isTipoGastoValido(email, transaccion.getTipoGasto())) {
            throw new IllegalArgumentException("El tipo de gasto no existe.");
        }
        if (!personalCategoriaService.isCategoriaValida(email, transaccion.getCategoria())) {
            System.out.println("la categoría es:    " + transaccion.getCategoria());
            throw new IllegalArgumentException("La categoría no existe.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        transaccion.setUser(user);

        // Si no se proporciona una fecha, usamos la fecha actual
        if (transaccion.getFecha() == null) {
            transaccion.setFecha(LocalDate.now());
        } 

        // Calcular siguiente ejecución si es recurrente
        if (transaccion.getFrecuenciaRecurrente() != null && !transaccion.getFrecuenciaRecurrente().isEmpty()) {
            Transacciones copia = new Transacciones();
            copia.setUser(transaccion.getUser());
            copia.setValor(transaccion.getValor());
            copia.setCategoria(transaccion.getCategoria());
            copia.setMotivo(transaccion.getMotivo());
            copia.setFecha(transaccion.getFecha());
            copia.setTipoGasto(transaccion.getTipoGasto());
            copia.setFrecuenciaRecurrente(null);
            copia.setMonedaOriginal(transaccion.getMonedaOriginal());
            copia.setMontoOriginal(transaccion.getMontoOriginal());
    
            transaccionesRepository.save(copia);

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
        User user = userService.findByEmail(email);
        Transacciones transaccion = transaccionesRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Transacción no encontrada o no pertenece al usuario"));
        transaccion.setMotivo(transaccionActualizada.getMotivo());
        transaccion.setValor(transaccionActualizada.getValor());
        transaccion.setFecha(transaccionActualizada.getFecha());
        transaccion.setCategoria(transaccionActualizada.getCategoria());
        transaccion.setTipoGasto(transaccionActualizada.getTipoGasto());
        transaccion.setMonedaOriginal(transaccionActualizada.getMonedaOriginal());
        transaccion.setMontoOriginal(transaccionActualizada.getMontoOriginal());
        if ((transaccionActualizada.getFrecuenciaRecurrente() != null && !transaccionActualizada.getFrecuenciaRecurrente().isEmpty()) && (transaccion.getFrecuenciaRecurrente() == null || transaccion.getFrecuenciaRecurrente().isEmpty() || "".equals(transaccion.getFrecuenciaRecurrente()))) {
            Transacciones copia = new Transacciones();
            copia.setUser(transaccion.getUser());
            copia.setValor(transaccionActualizada.getValor());
            copia.setCategoria(transaccionActualizada.getCategoria());
            copia.setMotivo(transaccionActualizada.getMotivo());
            copia.setFecha(transaccionActualizada.getFecha());
            copia.setTipoGasto(transaccionActualizada.getTipoGasto());
            copia.setFrecuenciaRecurrente(null);
            copia.setMonedaOriginal(transaccionActualizada.getMonedaOriginal());
            copia.setMontoOriginal(transaccionActualizada.getMontoOriginal());
            transaccionesRepository.save(copia);
            LocalDate siguienteEjecucion = calcularSiguienteEjecucion(transaccionActualizada.getFecha(), transaccionActualizada.getFrecuenciaRecurrente());
            transaccion.setSiguienteEjecucion(siguienteEjecucion);
        }
        if (transaccionActualizada.getFrecuenciaRecurrente()!= null) {
            transaccion.setFrecuenciaRecurrente(transaccionActualizada.getFrecuenciaRecurrente());
            transaccion.setSiguienteEjecucion(calcularSiguienteEjecucion(transaccionActualizada.getFecha(), transaccionActualizada.getFrecuenciaRecurrente()));
        }
        return transaccionesRepository.save(transaccion);
    }

    public List<Transacciones> getTransaccionesByUserIdAndCategory(Long userId, String categoria) {
        return transaccionesRepository.findByUserIdAndCategoriaOrderByFechaDesc(userId, categoria);
    }

    public List<Transacciones> getTransaccionesFiltradas(Long userId, String categoria, Integer anio, Integer mes) {
        LocalDate startDate = null;
        LocalDate endDate = null;
        List<Transacciones> transacciones;
        if (anio == null && mes != null) {
            anio = 2025;
        }
        // Si la categoría no es null o "Todas" y anio y mes son null
        if (categoria != null && !categoria.equals("Todas") && anio == null && mes == null) {
            transacciones =  transaccionesRepository.findByUserIdAndCategoriaOrderByFechaDesc(userId, categoria);
        }
        // Si la categoría no es null ni "Todas" y además mes o anio no son null
        else if (categoria != null && !categoria.equals("Todas") && (anio != null || mes != null)) {
            if (anio != null && mes != null) {
                // Si ambos son proporcionados, calcula el rango de fechas
                startDate = LocalDate.of(anio, mes, 1);
                endDate = startDate.plusMonths(1).minusDays(1);
                transacciones =  transaccionesRepository.findByUserIdAndCategoriaAndFechaBetween(userId, categoria, startDate, endDate);
            } else if (anio != null) {
                // Si solo el año es proporcionado, establece el rango de fechas para todo el año
                startDate = LocalDate.of(anio, 1, 1);
                endDate = LocalDate.of(anio, 12, 31);
                transacciones =  transaccionesRepository.findByUserIdAndCategoriaAndFechaBetween(userId, categoria, startDate, endDate);
            } else {
                transacciones = List.of();
            }
        }
        // Si la categoría es null o "Todas" pero anio o mes no son null
        else if ((categoria == null || categoria.equals("Todas")) && (anio != null || mes != null)) {
            if (anio != null && mes != null) {
                // Si ambos son proporcionados, calcula el rango de fechas
                startDate = LocalDate.of(anio, mes, 1);
                endDate = startDate.plusMonths(1).minusDays(1);
                transacciones =  transaccionesRepository.findByUserIdAndFechaBetween(userId, startDate, endDate);
            } else if (anio != null) {
                // Si solo el año es proporcionado, establece el rango de fechas para todo el año
                startDate = LocalDate.of(anio, 1, 1);
                endDate = LocalDate.of(anio, 12, 31);
                transacciones =  transaccionesRepository.findByUserIdAndFechaBetween(userId, startDate, endDate);
            } else {
                transacciones = List.of();
            }
        } else {
            transacciones = transaccionesRepository.findByUserIdOrderByFechaDesc(userId);
        }
        return transacciones.stream()
            .filter(t -> t.getFrecuenciaRecurrente() == null || t.getFrecuenciaRecurrente().trim().isEmpty())
            .collect(Collectors.toList());
        }
    
        //@Scheduled(cron = "0 0 0 * * ?") // Se ejecuta todos los días a medianoche
        //@Scheduled(cron = "0 * * * * ?") // Se ejecuta todos los minutos
        //@Scheduled(cron = "0 */5 * * * ?") //se ejecuta cada 5 minutos
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
