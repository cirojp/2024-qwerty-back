package api.back;


/*import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TransaccionesRepositoryTest {

    @Autowired
    private TransaccionesRepository transaccionesRepository;

    @Test
    @Rollback(false)
    public void testGuardarTransaccion() {
        // Crear una nueva entidad Transacciones
        Transacciones transaccion = new Transacciones(500, "Pago");

        // Guardar la entidad en la base de datos
        Transacciones transaccionGuardada = transaccionesRepository.save(transaccion);

        // Verificar que se ha guardado correctamente y tiene un ID asignado
        assertThat(transaccionGuardada.getId()).isNotNull();
        assertThat(transaccionGuardada.getValor()).isEqualTo(500);
        assertThat(transaccionGuardada.getMotivo()).isEqualTo("Pago");
    }
}*/
/* 
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test") // Asegura que se use el perfil 'test'
public class TransaccionesRepositoryTest {

    @Autowired
    private TransaccionesRepository transaccionesRepository;

    @Test
    public void testGuardarTransaccion() {
        // Crear una nueva entidad Transacciones
        Transacciones transaccion = new Transacciones(500, "Pago");

        // Guardar la entidad en la base de datos
        Transacciones transaccionGuardada = transaccionesRepository.save(transaccion);

        // Verificar que se ha guardado correctamente y tiene un ID asignado
        assertThat(transaccionGuardada.getId()).isNotNull();
        assertThat(transaccionGuardada.getValor()).isEqualTo(500);
        assertThat(transaccionGuardada.getMotivo()).isEqualTo("Pago");
    }

    @Test
    public void testEncontrarTransaccionPorId() {
        // Crear y guardar una entidad Transacciones
        Transacciones transaccion = new Transacciones(300, "Compra");
        Transacciones transaccionGuardada = transaccionesRepository.save(transaccion);

        // Buscar la entidad por ID
        Optional<Transacciones> transaccionEncontrada = transaccionesRepository.findById(transaccionGuardada.getId());

        // Verificar que la entidad encontrada coincide con la guardada
        assertThat(transaccionEncontrada).isPresent();
        assertThat(transaccionEncontrada.get().getValor()).isEqualTo(300);
        assertThat(transaccionEncontrada.get().getMotivo()).isEqualTo("Compra");
    }

    @Test
    public void testEliminarTransaccion() {
        // Crear y guardar una entidad Transacciones
        Transacciones transaccion = new Transacciones(400, "Venta");
        Transacciones transaccionGuardada = transaccionesRepository.save(transaccion);

        // Eliminar la entidad
        transaccionesRepository.deleteById(transaccionGuardada.getId());

        // Verificar que la entidad ha sido eliminada
        Optional<Transacciones> transaccionEliminada = transaccionesRepository.findById(transaccionGuardada.getId());
        assertThat(transaccionEliminada).isNotPresent();
    }
}
*/