package models;
import java.time.LocalDate;
import  java.time.LocalTime;

public class Turno {
    private LocalDate Fecha;
    private LocalTime Hora;
    private E Estado;
    private int Pago;
    private LocalDate Fecha_Pago;

    public Turno(LocalDate Fecha, LocalTime Hora, E Estado, int Pago, LocalDate Fecha_Pago) {
        this.Fecha = Fecha;
        this.Hora = Hora;
        this.Estado = Estado;
        this.Pago = Pago;
        this.Fecha_Pago = Fecha_Pago;
    }

}


