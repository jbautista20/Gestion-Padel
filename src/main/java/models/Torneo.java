package models;
import java.time.LocalDate;

public class Torneo {
    private T Tipo;
    private int Categoria;
    private LocalDate Fecha;
    private String Premio1;
    private String Premio2;
    private int Valor_Inscripcion;
    private Es Estados;

    public Torneo(T Tipo, int Categoria,  LocalDate Fecha,  String Premio1,  String Premio2, int Valor_Inscripcion, Es Estados) {
        this.Tipo = Tipo;
        this.Categoria = Categoria;
        this.Fecha = Fecha;
        this.Premio1 = Premio1;
        this.Premio2 = Premio2;
        this.Valor_Inscripcion = Valor_Inscripcion;
        this.Estados = Estados;
    }

    public T getTipo() {
        return Tipo;
    }

    public void setTipo(T tipo) {
        Tipo = tipo;
    }

    public int getCategoria() {
        return Categoria;
    }

    public void setCategoria(int categoria) {
        Categoria = categoria;
    }

    public LocalDate getFecha() {
        return Fecha;
    }

    public void setFecha(LocalDate fecha) {
        Fecha = fecha;
    }

    public String getPremio1() {
        return Premio1;
    }

    public void setPremio1(String premio1) {
        Premio1 = premio1;
    }

    public String getPremio2() {
        return Premio2;
    }

    public void setPremio2(String premio2) {
        Premio2 = premio2;
    }

    public int getValor_Inscripcion() {
        return Valor_Inscripcion;
    }

    public void setValor_Inscripcion(int valor_Inscripcion) {
        Valor_Inscripcion = valor_Inscripcion;
    }

    public Es getEstados() {
        return Estados;
    }

    public void setEstados(Es estados) {
        Estados = estados;
    }
}
