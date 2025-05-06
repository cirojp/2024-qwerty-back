package api.back;

public class PersonalTipoGastoDTO {
    private Long id;
    private String nombre;
    private String emailUsuario;

    // Constructor que transforma desde la entidad
    public PersonalTipoGastoDTO(PersonalTipoGasto tipoGasto) {
        this.id = tipoGasto.getId();
        this.nombre = tipoGasto.getNombre();
        this.emailUsuario = tipoGasto.getUser() != null ? tipoGasto.getUser().getEmail() : null;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }
}
