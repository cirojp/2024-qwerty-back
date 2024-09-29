package api.back;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "transaccionesPendientes")
public class TransaccionesPendientes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String motivo;
    private String categoria;
    private LocalDate fecha;

    public TransaccionesPendientes(Double valor, User user, String motivo, String categoria, LocalDate fecha) {
        this.valor = valor;
        this.user = user;
        this.motivo = motivo;
        this.categoria = categoria;
        this.fecha = fecha;
    }

}
