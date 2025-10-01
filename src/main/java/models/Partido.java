package models;
import java.time.LocalDate;
import java.time.LocalTime;

public class Partido {
    private int Id;
    private LocalTime Hora;
    private int Instancia;
    private int Puntos;
    private Resultado resultado;
    private Cancha cancha;  // Relación 1..1 con Cancha
    private Equipo equipo1;
    private Equipo equipo2;
    private Equipo ganador;
    private Torneo torneo;

    public Partido(int Id, LocalTime hora, int instancia, int puntos, Resultado resultado, Cancha cancha, Equipo equipo1, Equipo equipo2, Equipo ganador, Torneo torneo) {
        Hora = hora;
        Id = Id;
        Instancia = instancia;
        Puntos = puntos;
        this.resultado = resultado;
        this.cancha = cancha;
        this.equipo1 = equipo1;
        this.equipo2 = equipo2;
        this.ganador = ganador;
        this.torneo = torneo;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        Id = Id;
    }

    public LocalTime getHora() {
        return Hora;
    }

    public void setHora(LocalTime hora) {
        Hora = hora;
    }

    public int getInstancia() {
        return Instancia;
    }

    public void setInstancia(int instancia) {
        Instancia = instancia;
    }

    public int getPuntos() {
        return Puntos;
    }

    public void setPuntos(int puntos) {
        Puntos = puntos;
    }

    public Resultado getResultado() {
        return resultado;
    }

    public void setResultado(Resultado resultado) {
        this.resultado = resultado;
    }

    public Cancha getCancha() {
        return cancha;
    }

    public void setCancha(Cancha cancha) {
        this.cancha = cancha;
    }

    public Equipo getEquipo1() {
        return equipo1;
    }

    public void setEquipo1(Equipo equipo1) {
        this.equipo1 = equipo1;
    }

    public Equipo getEquipo2() {
        return equipo2;
    }

    public void setEquipo2(Equipo equipo2) {
        this.equipo2 = equipo2;
    }

    public Equipo getGanador() {
        return ganador;
    }

    public void setGanador(Equipo ganador) {
        this.ganador = ganador;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }
}
