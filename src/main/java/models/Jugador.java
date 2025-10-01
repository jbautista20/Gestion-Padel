package models;
import java.time.LocalDate;
import java.util.List;

public class Jugador extends Persona {
    private int Id;
    private int Categoria;
    private int Sexo;
    private int Puntos;
    private LocalDate Anio_Nac;
    private List<Equipo> equipos;

    public Jugador(int Idpersona, int Id, String nombre, String apellido, String telefono, String direccion, List<Turno> turnos, int id, int categoria, int sexo, int puntos, LocalDate anio_Nac, List<Equipo> equipos) {
        super(Idpersona, nombre, apellido, telefono, direccion, turnos);
        this.Id = id;
        Categoria = categoria;
        Sexo = sexo;
        Puntos = puntos;
        Anio_Nac = anio_Nac;
        this.equipos = equipos;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        Id = Id;
    }

    public int getCategoria() {
        return Categoria;
    }

    public void setCategoria(int categoria) {
        Categoria = categoria;
    }

    public int getSexo() {
        return Sexo;
    }

    public void setSexo(int sexo) {
        Sexo = sexo;
    }

    public int getPuntos() {
        return Puntos;
    }

    public void setPuntos(int puntos) {
        Puntos = puntos;
    }

    public LocalDate getAnio_Nac() {
        return Anio_Nac;
    }

    public void setAnio_Nac(LocalDate anio_Nac) {
        Anio_Nac = anio_Nac;
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
    }
}