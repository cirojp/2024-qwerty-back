package api.back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacciones")
// @CrossOrigin(origins = "http://127.0.0.1:5173/")
@CrossOrigin(origins = "http://localhost:5173/")
public class TransaccionesController {

    private final TransaccionesService transaccionesService;

    @Autowired
    public TransaccionesController(TransaccionesService transaccionesService) {
        this.transaccionesService = transaccionesService;
    }

    @GetMapping
    public List<Transacciones> getAllTransacciones() {
        return transaccionesService.getAllTransacciones();
    }

    @PostMapping
    public Transacciones createTransaccion(@RequestBody Transacciones transaccion) {
        return transaccionesService.createTransaccion(transaccion);
    }

    @GetMapping("/{id}")
    public Optional<Transacciones> getTransaccionById(@PathVariable Long id) {
        return transaccionesService.getTransaccionById(id);
    }

    /*
     * @PutMapping("/{id}")
     * public Transacciones updateTransaccion(@PathVariable Long id, @RequestBody
     * Transacciones transaccionActualizada) {
     * return transaccionesService.updateTransaccion(id, transaccionActualizada);
     * }
     */

    @DeleteMapping("/{id}")
    public void deleteTransaccion(@PathVariable Long id) {
        transaccionesService.deleteTransaccion(id);
    }
}
