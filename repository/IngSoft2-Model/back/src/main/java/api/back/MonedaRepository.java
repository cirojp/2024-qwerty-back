package api.back;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MonedaRepository extends JpaRepository<Moneda, Long> {
    List<Moneda> findByUserEmail(String email);

    Optional<Moneda> findByIdAndUserEmail(Long id, String email);

    Optional<Moneda> findByUserEmailAndNombre(String email, String nombre);
    
}
