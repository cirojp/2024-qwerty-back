package api.back;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PersonalTipoGastoRepository extends JpaRepository<PersonalTipoGasto, Long> {
    List<PersonalTipoGasto> findByUser(User user);
}