package api.back;


import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class TransaccionesController {

    private final TransaccionesRepository transaccionesRepository;

    public TransaccionesController(TransaccionesRepository transaccionesRepository) {
        this.transaccionesRepository = transaccionesRepository;
    }

    @GetMapping
    public List<Transacciones> getAllTransacciones() {
        return transaccionesRepository.findAll();
    }

    @PostMapping
    public Transacciones createTransacciones(@RequestBody Transacciones transacciones) {
        return transaccionesRepository.save(transacciones);
    }
}
