package api.back;


import java.time.LocalDate;

public class TransaccionesPendientesDTO {
    private Long id;
    private Double valor;
    private String motivo;
    private String id_reserva;
    private LocalDate fecha;
    private String sentByEmail;
    private Long grupoId;
    private String monedaOriginal;
    private Double montoOriginal;
    private String emailUsuario;

    public TransaccionesPendientesDTO(TransaccionesPendientes transaccion) {
        this.id = transaccion.getId();
        this.valor = transaccion.getValor();
        this.motivo = transaccion.getMotivo();
        this.id_reserva = transaccion.getId_reserva();
        this.fecha = transaccion.getFecha();
        this.sentByEmail = transaccion.getSentByEmail();
        this.grupoId = transaccion.getGrupoId();
        this.monedaOriginal = transaccion.getMonedaOriginal();
        this.montoOriginal = transaccion.getMontoOriginal();
        this.emailUsuario = transaccion.getUser().getEmail();
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

    public String getId_reserva() {
        return id_reserva;
    }

    public void setId_reserva(String id_reserva) {
        this.id_reserva = id_reserva;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getSentByEmail() {
        return sentByEmail;
    }

    public void setSentByEmail(String sentByEmail) {
        this.sentByEmail = sentByEmail;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
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

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    

}
