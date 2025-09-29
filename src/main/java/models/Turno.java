package models;
import java.time.LocalDate;
import  java.time.LocalTime;

public class Turno {
    private LocalDate Fecha;
    private LocalTime Hora;
    private Es estado;
    private int Pago;
    private LocalDate Fecha_Pago;

    public Turno(LocalDate Fecha, LocalTime Hora, Es estado, int Pago, LocalDate Fecha_Pago) {
        this.Fecha = Fecha;
        this.Hora = Hora;
        this.estado = estado;
        this.Pago = Pago;
        this.Fecha_Pago = Fecha_Pago;
    }

    public LocalDate getFecha() { return Fecha; }
    public void setFecha(LocalDate Fecha) { this.Fecha = Fecha; }

    public LocalTime getHora() { return Hora; }
    public void setHora(LocalTime Hora) { this.Hora = Hora; }

    public Es getEstado() { return estado; }
    public void setEstado(Es estado) { this.estado = estado; }

    public int getPago() { return Pago; }
    public void setPago(int Pago) { this.Pago = Pago; }

    public LocalDate getFecha_Pago() { return Fecha_Pago; }
    public void setFecha_Pago(LocalDate Fecha_Pago) { this.Fecha_Pago = Fecha_Pago; }

}


