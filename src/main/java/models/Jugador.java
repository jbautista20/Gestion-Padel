package models;
import java.time.LocalDate;

public class Jugador extends Persona {
    private int Categoria;
    private int Sexo;
    private int Puntos;
    private LocalDate Anio_Nac;

    public Jugador(String Nombre, String Apellido, String Telefono, String Direccion, int Categoria, int Sexo, int Puntos, LocalDate Anio_Nac) {
        super(Nombre, Apellido, Telefono, Direccion);
        this.Categoria = Categoria;
        this.Sexo = Sexo;
        this.Puntos = Puntos;
        this.Anio_Nac = Anio_Nac;
    }

    public int getCategoria() { return Categoria; }
    public void setCategoria(int Categoria) { this.Categoria = Categoria; }

    public int getSexo() { return Sexo; }
    public void setSexo(int Sexo) { this.Sexo = Sexo; }

    public int getPuntos() { return Puntos; }
    public void setPuntos(int Puntos) { this.Puntos = Puntos; }

    public LocalDate getAnio_Nac() { return Anio_Nac; }
    public void setAnio_Nac(LocalDate Anio_Nac) { this.Anio_Nac = Anio_Nac; }


}