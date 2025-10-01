package models;
import java.time.LocalDate;

public class Cancelacion {
    private int Id;
    private LocalDate Fecha;
    private int Reintegro;
    private Turno turno;

    public Cancelacion(int Id, LocalDate fecha, int reintegro, Turno turno) {
        this.Id = Id;
        Fecha = fecha;
        Reintegro = reintegro;
        this.turno = turno;
    }

    public Cancelacion() {
        //defecto
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
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
