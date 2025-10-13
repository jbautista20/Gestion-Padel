package models;
import java.util.List;

public class Cancha {
    private int Numero;
    private List<Turno> turnos;     // Todos los turnos reservados
    private List<Partido> partidos; // Todos los partidos jugados

    public Cancha(int numero, List<Turno> turnos, List<Partido> partidos) {
        Numero = numero;
        this.turnos = turnos;
        this.partidos = partidos;
    }

    public Cancha() {
        //defecto
    }



    public List<Turno> getTurnos() {
        return turnos;
    }

    public void setTurnos(List<Turno> turnos) {
        this.turnos = turnos;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public void setPartidos(List<Partido> partidos) {
        this.partidos = partidos;
    }

    @Override
    public String toString(){
        return String.valueOf(Numero);
    }
    public int getNumero() {
        return Numero;
    }

    public void setNumero(int numero) {
        Numero = numero;
    }


}
