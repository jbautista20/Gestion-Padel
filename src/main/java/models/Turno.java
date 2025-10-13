package models;
import java.time.LocalDate;
import  java.time.LocalTime;

public class Turno {
    private int Id;
    private LocalDate Fecha;
    private LocalTime Hora;
    private E Estado;
    private int Pago;
    private LocalDate Fecha_Pago;
    private Persona persona; // 1..* relación
    private Cancha cancha;          // 1 cancha asignada
    private LocalDate Fecha_Cancelacion;
    private String Reintegro_Cancelacion;

    public Turno(int Id, LocalDate fecha, LocalTime hora, E estado, int pago, LocalDate fecha_Pago, Persona persona, Cancha cancha, LocalDate Fecha_Cancelacion, String Reintegro_Cancelacion) {
        this.Id = Id;
        Fecha = fecha;
        Hora = hora;
        Estado = estado;
        Pago = pago;
        Fecha_Pago = fecha_Pago;
        this.persona = persona;
        this.cancha = cancha;
        this.Fecha_Cancelacion = Fecha_Cancelacion;
        this.Reintegro_Cancelacion = Reintegro_Cancelacion;
    }

    public Turno() {
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

    public LocalTime getHora() {
        return Hora;
    }

    public void setHora(LocalTime hora) {
        Hora = hora;
    }

    public E getEstado() {
        return Estado;
    }

    public void setEstado(E estado) {
        Estado = estado;
    }

    public int getPago() {
        return Pago;
    }

    public void setPago(int pago) {
        Pago = pago;
    }

    public LocalDate getFecha_Pago() {
        return Fecha_Pago;
    }

    public void setFecha_Pago(LocalDate fecha_Pago) {
        Fecha_Pago = fecha_Pago;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Cancha getCancha() {
        return cancha;
    }

    public void setCancha(Cancha cancha) {
        this.cancha = cancha;
    }

    public LocalDate getFecha_Cancelacion() {
        return Fecha_Cancelacion;
    }

    public void setFecha_Cancelacion(LocalDate fecha_Cancelacion) {
        Fecha_Cancelacion = fecha_Cancelacion;
    }

    public String getReintegro_Cancelacion() {
        return Reintegro_Cancelacion;
    }

    public void setReintegro_Cancelacion(String reintegro_Cancelacion) {
        Reintegro_Cancelacion = reintegro_Cancelacion;
    }
}


