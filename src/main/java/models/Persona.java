package models;

public class Persona {
    private String Nombre;
    private String Apellido;
    private String Telefono;
    private String Direccion;

    public Persona( String Nombre, String Apellido, String Telefono, String Direccion) {
        this.Nombre = Nombre;
        this.Apellido = Apellido;
        this.Telefono = Telefono;
        this.Direccion = Direccion;
    }

    public String getNombre() { return Nombre; }
    public void setNombre(String Nombre) { this.Nombre = Nombre; }

    public String getApellido() { return Apellido; }
    public void setApellido(String Apellido) { this.Apellido = Apellido; }

    public String getTelefono() { return Telefono; }
    public void setTelefono(String Telefono) { this.Telefono = Telefono; }

    public String getDireccion() { return Direccion; }
    public void setDireccion(String Direccion) { this.Direccion = Direccion; }

}
