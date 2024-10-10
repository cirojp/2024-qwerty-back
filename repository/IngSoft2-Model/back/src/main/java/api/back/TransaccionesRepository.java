package api.back;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TransaccionesRepository extends JpaRepository<Transacciones, Long> {
    List<Transacciones> findByUserId(Long userId); // Encuentra transacciones por el ID del usuario

    Optional<Transacciones> findByIdAndUserId(Long id, Long userId);

    List<Transacciones> findByUserIdOrderByFechaDesc(Long userId);

    Optional<Transacciones> findByIdAndUserEmail(Long id, String email);

    List<Transacciones> findByUserIdAndCategoriaOrderByFechaDesc(Long userId, String categoria);

    @Query("SELECT t FROM Transacciones t WHERE t.user.id = :userId " +
       "AND (:categoria IS NULL OR t.categoria = :categoria) " +
       "AND (:anio IS NULL OR YEAR(t.fecha) = :anio) " +
       "AND (:mes IS NULL OR MONTH(t.fecha) = :mes)")
    List<Transacciones> findTransaccionesByFilters(
        @Param("userId") Long userId, 
        @Param("categoria") String categoria, 
        @Param("anio") Integer anio, 
        @Param("mes") Integer mes);


}
