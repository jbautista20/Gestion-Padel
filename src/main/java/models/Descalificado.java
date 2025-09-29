package models;
import java.time.LocalDate;

public class Descalificado {
    private String Motivo;
    private LocalDate Fecha;

    public Descalificado(String Motivo, LocalDate Fecha) {
        this.Motivo = Motivo;
        this.Fecha = Fecha;
    }

    public String getMotivo() { return Motivo; }
    public void setMotivo(String Motivo) { this.Motivo = Motivo; }

    public LocalDate getFecha() { return Fecha; }
    public void setFecha(LocalDate Fecha) { this.Fecha = Fecha; }


}
