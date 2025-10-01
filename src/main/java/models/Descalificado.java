package models;
import java.time.LocalDate;

public class Descalificado {
    private int Id;
    private String Motivo;
    private LocalDate Fecha;
    private Equipo equipo;

    public Descalificado(int Id, String motivo, LocalDate fecha, Equipo equipo) {
        this.Id = Id;
        Motivo = motivo;
        Fecha = fecha;
        this.equipo = equipo;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getMotivo() {
        return Motivo;
    }

    public void setMotivo(String motivo) {
        Motivo = motivo;
    }

    public LocalDate getFecha() {
        return Fecha;
    }

    public void setFecha(LocalDate fecha) {
        Fecha = fecha;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }
}
