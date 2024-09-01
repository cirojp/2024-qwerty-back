package api.back;


import org.junit.jupiter.api.Test;
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
}
