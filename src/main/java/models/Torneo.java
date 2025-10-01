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
    private Equipo[] equipos = new Equipo[8];
    private Partido[] partidos = new Partido[7];

    public Torneo(T tipo, int categoria, LocalDate fecha, String premio1, String premio2, int valor_Inscripcion, Es estados, Equipo[] equipos, Partido[] partidos) {
        Tipo = tipo;
        Categoria = categoria;
        Fecha = fecha;
        Premio1 = premio1;
        Premio2 = premio2;
        Valor_Inscripcion = valor_Inscripcion;
        Estados = estados;
        this.equipos = equipos;
        this.partidos = partidos;
    }

    public Torneo() {
        //defecto
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

    public Equipo[] getEquipos() {
        return equipos;
    }

    public void setEquipos(Equipo[] equipos) {
        this.equipos = equipos;
    }

    public Partido[] getPartidos() {
        return partidos;
    }

    public void setPartidos(Partido[] partidos) {
        this.partidos = partidos;
    }
}
