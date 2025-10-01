package models;

import java.util.List;

public class Persona {
    private String Nombre;
    private String Apellido;
    private String Telefono;
    private String Direccion;
    private List<Turno> turnos; // reservas

    public Persona(String nombre, String apellido, String telefono, String direccion, List<Turno> turnos) {
        Nombre = nombre;
        Apellido = apellido;
        Telefono = telefono;
        Direccion = direccion;
        this.turnos = turnos;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public List<Turno> getTurnos() {
        return turnos;
    }

    public void setTurnos(List<Turno> turnos) {
        this.turnos = turnos;
    }
}
