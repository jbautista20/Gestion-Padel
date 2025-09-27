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

    public String getTipo() { return Tipo; }
    public void setTipo( String Tipo) { this.Tipo = Tipo; }

    public int getCategoria() { return Categoria; }
    public void setCategoria(int Categoria) { this.Categoria = Categoria; }

    public LocalDate getFecha() { return Fecha; }
    public void setFecha(LocalDate Fecha) { this.Fecha = Fecha; }

    public String getPremio1() { return Premio1; }
    public void setPremio1(String Premio1) { this.Premio1 = Premio1; }

    public String getPremio2() { return Premio2; }
    public void setPremio2(String Premio2) { this.Premio2 = Premio2; }

    public int getValor_Inscripcion() { return Valor_Inscripcion; }
    public void setValor_Inscripcion(int Valor_Inscripcion) { this.Valor_Inscripcion = Valor_Inscripcion; }

    public String getEstados() { return Estados; }
    public void setEstados(String Estados) { this.Estados = Estados; }


}
