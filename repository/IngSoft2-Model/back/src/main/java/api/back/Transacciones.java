package api.back;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

@Entity
@Table(name = "transacciones") // Nombre de la tabla en la base de datos
public class Transacciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double valor;
    @NotBlank
    @Column(nullable = false)
    private String motivo;
    @NotBlank
    @Column(nullable = false)
    private String categoria;
    @NotBlank
    @Column(nullable = false)
    private String tipoGasto;
    @NotBlank
    @Column(nullable = false)
    private String monedaOriginal; 
    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Double montoOriginal;
    private String frecuenciaRecurrente;
    private LocalDate siguienteEjecucion;
    


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Relación con la entidad User
    // private Calendar fecha;

    private LocalDate fecha;

    // Constructor por defecto
    public Transacciones() {
    }

    // Constructor con parámetros (opcional)
    public Transacciones(Double valor, String motivo, LocalDate fecha,
            String categoria, String tipoGasto, String monedaOriginal ,Double montoOriginal, String frecuenciaRecurrente) {
        this.valor = valor;
        this.motivo = motivo;
        this.fecha = fecha;
        this.categoria = categoria;
        this.tipoGasto = tipoGasto;
        this. monedaOriginal = monedaOriginal;
        this.montoOriginal = montoOriginal;
        this.frecuenciaRecurrente = frecuenciaRecurrente;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTipoGasto() {
        return tipoGasto;
    }

    public void setTipoGasto(String tipoGasto) {
        this.tipoGasto = tipoGasto;
    }

    public String getMonedaOriginal() {
        return monedaOriginal;
    }

    public void setMonedaOriginal(String monedaOriginal) {
        this.monedaOriginal = monedaOriginal;
    }

    public Double getMontoOriginal() {
        return montoOriginal;
    }

    public void setMontoOriginal(Double montoOriginal) {
        this.montoOriginal = montoOriginal;
    }

    public String getFrecuenciaRecurrente() {
        return frecuenciaRecurrente;
    }

    public void setFrecuenciaRecurrente(String frecuenciaRecurrente) {
        this.frecuenciaRecurrente = frecuenciaRecurrente;
    }

    public LocalDate getSiguienteEjecucion() {
        return siguienteEjecucion;
    }

    public void setSiguienteEjecucion(LocalDate siguienteEjecucion) {
        this.siguienteEjecucion = siguienteEjecucion;
    }
}
