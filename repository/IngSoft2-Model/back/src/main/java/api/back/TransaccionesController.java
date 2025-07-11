package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/transacciones")
@CrossOrigin(origins = { "http://localhost:5173/", "http://127.0.0.1:5173" })
public class TransaccionesController {

    private final TransaccionesService transaccionesService;
    private final UserService userService;
    private final TransaccionesPendientesService transaccionesPendientesService;

    @Autowired
    private JwtUtil jwtUtil;

    public TransaccionesController(TransaccionesService transaccionesService, UserService userService,
            TransaccionesPendientesService transaccionesPendientesService) {
        this.transaccionesService = transaccionesService;
        this.userService = userService;
        this.transaccionesPendientesService = transaccionesPendientesService;
    }

    @GetMapping("/user")
    public List<TransaccionDTO> getTransaccionesByUser(Authentication authentication) {
        String email = authentication.getName(); 
        User user = userService.findByEmail(email); 
        //return transaccionesService.getTransaccionesByUserId(user.getId());           
        return transaccionesService.getTransaccionesByUserId(user.getId())
        .stream()
        .map(TransaccionDTO::new)
        .collect(Collectors.toList());                             
    }

    @GetMapping("/userTest")
    public boolean checkUserValidToken(Authentication authentication,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        boolean valid = !jwtUtil.isTokenExpired(token); // validamos si el token no esta vencido
        return valid;
    }

    @PostMapping
    public ResponseEntity<?> crearTransaccion(@RequestBody Transacciones transaccion, Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email);
            user.setTransaccionesCreadas(user.getTransaccionesCreadas() + 1);
            Transacciones nueva =  transaccionesService.createTransaccion(transaccion, email);
            TransaccionDTO transaccionDTO = new TransaccionDTO(nueva);
            return ResponseEntity.ok(transaccionDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/crearPago/{mail}")
    public ResponseEntity<?> createPago(@PathVariable String mail, @RequestBody Transacciones transaccion,
            Authentication authentication) {
        if (transaccion.getFrecuenciaRecurrente() != null && !transaccion.getFrecuenciaRecurrente().isEmpty()) {
            throw new IllegalArgumentException("El pago no puede ser recurrente.");
        }
        String email = authentication.getName();
        Transacciones transaccion2 = new Transacciones();
        transaccion2.setCategoria("Ingreso de Dinero");
        transaccion2.setFecha(transaccion.getFecha());
        transaccion2.setMotivo(transaccion.getMotivo());
        transaccion2.setTipoGasto("Tarjeta de Debito");
        transaccion2.setUser(userService.findByEmail(mail));
        transaccion2.setValor(transaccion.getValor());
        transaccion2.setMonedaOriginal(transaccion.getMonedaOriginal());
        transaccion2.setMontoOriginal(transaccion.getMontoOriginal());
        try {
            transaccionesService.createTransaccion(transaccion2, mail);
            TransaccionesPendientes cobroPendiente = new TransaccionesPendientes(transaccion.getValor(),
                    userService.findByEmail(mail), transaccion.getMotivo(), "Pago", transaccion.getFecha(), transaccion.getMonedaOriginal(), transaccion.getMontoOriginal());
            transaccionesPendientesService.save(cobroPendiente);
            Transacciones nueva = transaccionesService.createTransaccion(transaccion, email);
            TransaccionDTO transaccionDTO = new TransaccionDTO(nueva);
            return ResponseEntity.ok(transaccionDTO);
        } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
    }

    @PostMapping("/enviarPago/{mail}")
    public ResponseEntity<?> sendPago(@PathVariable String mail, @RequestBody Transacciones transaccion,
            Authentication authentication) {
        if (transaccion.getFrecuenciaRecurrente() != null && !transaccion.getFrecuenciaRecurrente().isEmpty()) {
            throw new IllegalArgumentException("El pago no puede ser recurrente.");
        }
        String email = authentication.getName();
        transaccion.setUser(userService.findByEmail(email));
        Transacciones transaccion2 = new Transacciones();
        transaccion2.setCategoria("Ingreso de Dinero");
        transaccion2.setFecha(transaccion.getFecha());
        transaccion2.setMotivo(transaccion.getMotivo());
        transaccion2.setTipoGasto("Tarjeta de Debito");
        transaccion2.setUser(userService.findByEmail(mail));
        transaccion2.setValor(transaccion.getValor());
        transaccion2.setMonedaOriginal(transaccion.getMonedaOriginal());
        transaccion2.setMontoOriginal(transaccion.getMontoOriginal());
        try {
            transaccionesService.createTransaccion(transaccion2, mail, true);
            TransaccionesPendientes pendienteCobro = new TransaccionesPendientes(transaccion.getValor(),
                    userService.findByEmail(mail), transaccion.getMotivo(), "Pago", transaccion.getFecha(), transaccion.getMonedaOriginal(), transaccion.getMontoOriginal());
            pendienteCobro.setSentByEmail(email);
            transaccionesPendientesService.save(pendienteCobro);
            Transacciones nueva = transaccionesService.createTransaccion(transaccion, email);
            TransaccionDTO transaccionDTO = new TransaccionDTO(nueva);
            return ResponseEntity.ok(transaccionDTO);
        } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaccionById(@PathVariable Long id) {
        Optional<Transacciones> transaccion = transaccionesService.getTransaccionById(id);
        return transaccion.map(t -> ResponseEntity.ok(new TransaccionDTO(t)))
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaccion(@PathVariable Long id, Authentication authentication) {
        try {
            String email = authentication.getName(); // Obtener el email del usuario autenticado
            transaccionesService.deleteTransaccion(id, email);
            return ResponseEntity.noContent().build();
        } catch (TransaccionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaccion(@PathVariable Long id, @RequestBody Transacciones transaccionActualizada,
            Authentication authentication) {
       
        try {
            String email = authentication.getName(); // Obtenemos el email del usuario autenticado
            Transacciones nueva = transaccionesService.updateTransaccion(id, transaccionActualizada, email);
            TransaccionDTO transaccionDTO = new TransaccionDTO(nueva);
            return ResponseEntity.ok(transaccionDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/user/filter")
    public TransaccionesResponse getTransaccionesByFilters(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        // Obtén transacciones filtradas
        List<TransaccionDTO> transaccionesFiltradas = transaccionesService.getTransaccionesFiltradas(user.getId(), categoria, anio, mes);

        // Obtén todas las transacciones sin filtrar
        List<TransaccionDTO> transaccionesSinFiltrarCat = transaccionesService.getTransaccionesFiltradas(user.getId(), "Todas", anio, mes);

        // Retornar ambas listas en el objeto de respuesta personalizado
        return new TransaccionesResponse(transaccionesFiltradas, transaccionesSinFiltrarCat);
    }

    @GetMapping("/user/recurrent")
    public List<TransaccionDTO> getTransaccionesRecurrentes(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        //return transaccionesService.getTransaccionesRecurrentes(user.getId());
        return transaccionesService.getTransaccionesRecurrentes(user.getId())
        .stream()
        .map(TransaccionDTO::new)
        .collect(Collectors.toList());
    }

    /*@PostMapping("/procesar-recurrentes")
    public ResponseEntity<String> procesarTransaccionesRecurrentesManual() {
        System.out.println("🚀 Se está ejecutando el job de transacciones recurrentes");
        transaccionesService.procesarTransaccionesRecurrentes();
        return ResponseEntity.ok("Transacciones recurrentes procesadas");
    }*/


}