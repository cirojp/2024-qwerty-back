package api.back;

import java.math.BigDecimal;

public class Moneda {
    private String label; 
    private BigDecimal value; 

    // Constructor
    public Moneda(String label, BigDecimal value) {
        this.label = label;
        this.value = value;
    }

    // Getter para label
    public String getLabel() {
        return label;
    }

    // Setter para label
    public void setLabel(String label) {
        this.label = label;
    }

    // Getter para valEnPesos
    public BigDecimal getValEnPesos() {
        return value;
    }

    // Setter para valEnPesos
    public void setValEnPesos(BigDecimal value) {
        this.value = value;
    }

}
