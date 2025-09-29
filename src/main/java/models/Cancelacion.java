package models;
import java.time.LocalDate;

public class Cancelacion {
    private LocalDate Fecha;
    private int Reintegro;

    public Cancelacion(LocalDate Fecha, int Reintegro) {
        this.Fecha = Fecha;
        this.Reintegro = Reintegro;
    }

    public LocalDate getFecha() { return Fecha; }
    public void setFecha(LocalDate Fecha) { this.Fecha = Fecha; }

    public int getReintegro() { return Reintegro; }
    public void setReintegro(int Reintegro) { this.Reintegro = Reintegro; }


}
