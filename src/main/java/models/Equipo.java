package models;
import java.time.LocalDate;
import java.util.List;

public class Equipo {
    private int Id;
    private String Nombre;
    private int Ptos_T_Obt;
    private LocalDate Fecha_Insc;
    private Jugador jugador1;
    private Jugador jugador2;
    private Torneo torneo;
    private List<Partido>  partidosGanados;
    private List<Partido> partidosJugados;

    public Equipo(int Id, String nombre, int ptos_T_Obt, LocalDate fecha_Insc, Jugador jugador1, Jugador jugador2, Torneo torneo, List<Partido> partidosGanados, List<Partido> partidosJugados) {
        Id = Id;
        Nombre = nombre;
        Ptos_T_Obt = ptos_T_Obt;
        Fecha_Insc = fecha_Insc;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.torneo = torneo;
        this.partidosGanados = partidosGanados;
        this.partidosJugados = partidosJugados;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        Id = Id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public int getPtos_T_Obt() {
        return Ptos_T_Obt;
    }

    public void setPtos_T_Obt(int ptos_T_Obt) {
        Ptos_T_Obt = ptos_T_Obt;
    }

    public LocalDate getFecha_Insc() {
        return Fecha_Insc;
    }

    public void setFecha_Insc(LocalDate fecha_Insc) {
        Fecha_Insc = fecha_Insc;
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public void setJugador1(Jugador jugador1) {
        this.jugador1 = jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }

    public void setJugador2(Jugador jugador2) {
        this.jugador2 = jugador2;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public List<Partido> getPartidosGanados() {
        return partidosGanados;
    }

    public void setPartidosGanados(List<Partido> partidosGanados) {
        this.partidosGanados = partidosGanados;
    }

    public List<Partido> getPartidosJugados() {
        return partidosJugados;
    }

    public void setPartidosJugados(List<Partido> partidosJugados) {
        this.partidosJugados = partidosJugados;
    }
}
