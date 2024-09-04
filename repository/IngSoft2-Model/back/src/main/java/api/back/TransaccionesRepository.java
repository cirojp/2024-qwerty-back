package api.back;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransaccionesRepository extends JpaRepository<Transacciones, Long> {
    List<Transacciones> findByUserId(Long userId);  // Encuentra transacciones por el ID del usuario
}
