package api.back;

import org.springframework.data.jpa.repository.JpaRepository;
import api.back.Grupo;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
}
