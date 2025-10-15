package models;
import java.time.LocalDate;
import java.util.List;

public class Jugador extends Persona {

    private int idJugador;
    private int categoria;
    private int sexo;
    private int puntos;
    private int anioNac;
    private List<Equipo> equipos;

    public Jugador(int idPersona, int idJugador, String nombre, String apellido, String telefono, String direccion,
                   List<Turno> turnos, int categoria, int sexo, int puntos,
                   int anioNac, List<Equipo> equipos) {

        super(idPersona, nombre, apellido, telefono, direccion, turnos);
        this.idJugador = idJugador;
        this.categoria = categoria;
        this.sexo = sexo;
        this.puntos = puntos;
        this.anioNac = anioNac;
        this.equipos = equipos;
    }

    // Getters y Setters

    public int getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(int categoria) {
        this.categoria = categoria;
    }

    public int getSexo() {
        return sexo;
    }

    public void setSexo(int sexo) {
        this.sexo = sexo;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getAnioNac() {
        return anioNac;
    }

    public void setAnioNac(int anioNac) {
        this.anioNac = anioNac;
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
    }
}
