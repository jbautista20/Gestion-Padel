package models;
import java.time.LocalDate;
import java.time.LocalTime;

public class Partido {
    private LocalTime Hora;
    private int Instancia;
    private int Puntos;

    public Partido(LocalTime Hora, int Instancia, int Puntos) {
        this.Hora = Hora;
        this.Instancia = Instancia;
        this.Puntos = Puntos;
    }

    public LocalTime getHora() { return Hora; }
    public void setHora(LocalTime Hora) { this.Hora = Hora; }

    public int getInstancia() { return Instancia; }
    public void setInstancia(int Instancia) { this.Instancia = Instancia; }

    public int getPuntos() { return Puntos; }
    public void setPuntos(int Puntos) { this.Puntos = Puntos; }


}
