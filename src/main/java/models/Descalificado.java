package models;
import java.time.LocalDate;

public class Descalificado {
    private String Motivo;
    private LocalDate Fecha;
    private Equipo equipo;

    public Descalificado(String motivo, LocalDate fecha, Equipo equipo) {
        Motivo = motivo;
        Fecha = fecha;
        this.equipo = equipo;
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
