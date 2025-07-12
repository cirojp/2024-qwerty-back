package api.back;

import java.util.List;

public class TransaccionesResponse {
    private List<TransaccionDTO> transaccionesFiltradas;
    private List<TransaccionDTO> transaccionesSinFiltrarCat;

    public TransaccionesResponse(List<TransaccionDTO> transaccionesFiltradas, List<TransaccionDTO> transaccionesSinFiltrarCat) {
        this.transaccionesFiltradas = transaccionesFiltradas;
        this.transaccionesSinFiltrarCat = transaccionesSinFiltrarCat;
    }

    public List<TransaccionDTO> getTransaccionesFiltradas() {
        return transaccionesFiltradas;
    }

    public void setTransaccionesFiltradas(List<TransaccionDTO> transaccionesFiltradas) {
        this.transaccionesFiltradas = transaccionesFiltradas;
    }

    public List<TransaccionDTO> getTransaccionesSinFiltrarCat() {
        return transaccionesSinFiltrarCat;
    }

    public void setTransaccionesSinFiltrarCat(List<TransaccionDTO> transaccionesSinFiltrarCat) {
        this.transaccionesSinFiltrarCat = transaccionesSinFiltrarCat;
    }
}
