package api.back;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MonedaRepository extends JpaRepository<Moneda, Long> {
    List<Moneda> findByUserEmail(String email);
}
