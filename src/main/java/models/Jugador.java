package models;
import java.time.LocalDate;

public class Jugador extends Persona {
    private int Categoria;
    private int Sexo;
    private int Puntos;
    private LocalDate Año_Nac;

    public Jugador(String Nombre, String Apellido, String Telefono, String Direccion, int Categoria, int Sexo, int Puntos, LocalDate Año_Nac) {
        super(Nombre, Apellido, Telefono, Direccion);
        this.Categoria = Categoria;
        this.Sexo = Sexo;
        this.Puntos = Puntos;
        this.Año_Nac = Año_Nac;
    }

    public int getCategoria() { return Categoria; }
    public void setCategoria(int Categoria) { this.Categoria = Categoria; }

    public int getSexo() { return Sexo; }
    public void setSexo(int Sexo) { this.Sexo = Sexo; }

    public int getPuntos() { return Puntos; }
    public void setPuntos(int Puntos) { this.Puntos = Puntos; }

    public LocalDate getAño_Nac() { return Año_Nac; }
    public void setAño_Nac(LocalDate Año_Nac) { this.Año_Nac = Año_Nac; }


}