package api.back;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionesPendientesRepository extends JpaRepository<TransaccionesPendientes, Long> {
}
