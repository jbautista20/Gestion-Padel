package models;
import java.time.LocalDate;

public class Cancelacion {
    private LocalDate Fecha;
    private int Reintegro;
    private Turno turno;

    public Cancelacion(LocalDate fecha, int reintegro, Turno turno) {
        Fecha = fecha;
        Reintegro = reintegro;
        this.turno = turno;
    }

    public LocalDate getFecha() {
        return Fecha;
    }

    public void setFecha(LocalDate fecha) {
        Fecha = fecha;
    }

    public int getReintegro() {
        return Reintegro;
    }

    public void setReintegro(int reintegro) {
        Reintegro = reintegro;
    }

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }
}
