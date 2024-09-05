package api.back;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Sort;


public interface TransaccionesRepository extends JpaRepository<Transacciones, Long> {
    List<Transacciones> findByUserId(Long userId);  // Encuentra transacciones por el ID del usuario
    Optional<Transacciones> findByIdAndUserId(Long id, Long userId);
    List<Transacciones> findByUserIdOrderByFechaDesc(Long userId);
    Optional<Transacciones> findByIdAndUserEmail(Long id, String email);
}
