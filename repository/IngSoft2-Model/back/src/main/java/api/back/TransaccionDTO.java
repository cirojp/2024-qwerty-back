package api.back;

import java.time.LocalDate;

public class TransaccionDTO {
    private Long id;
    private Double valor;
    private String motivo;
    private String categoria;
    private String tipoGasto;
    private String monedaOriginal;
    private Double montoOriginal;
    private String frecuenciaRecurrente;
    private LocalDate siguienteEjecucion;
    private LocalDate fecha;
    private String emailUsuario; // Este campo es el email del usuario asociado

    // Constructor
    public TransaccionDTO(Transacciones transaccion) {
        this.id = transaccion.getId();
        this.valor = transaccion.getValor();
        this.motivo = transaccion.getMotivo();
        this.categoria = transaccion.getCategoria();
        this.tipoGasto = transaccion.getTipoGasto();
        this.monedaOriginal = transaccion.getMonedaOriginal();
        this.montoOriginal = transaccion.getMontoOriginal();
        this.frecuenciaRecurrente = transaccion.getFrecuenciaRecurrente();
        this.siguienteEjecucion = transaccion.getSiguienteEjecucion();
        this.fecha = transaccion.getFecha();
        this.emailUsuario = transaccion.getUser() != null ? transaccion.getUser().getEmail() : null; // Manejo de null si no hay usuario asociado
    }

    // Getters y setters
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }
}
