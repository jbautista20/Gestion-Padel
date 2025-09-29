package models;
import java.time.LocalDate;

public class Equipo {
    private String Nombre;
    private int Ptos_T_Obt;
    private LocalDate Fecha_Insc;

    public Equipo(String Nombre, int Ptos_T_Obt, LocalDate Fecha_Insc) {
        this.Nombre = Nombre;
        this.Ptos_T_Obt = Ptos_T_Obt;
        this.Fecha_Insc = Fecha_Insc;
    }

    public String getNombre() { return Nombre; }
    public void setNombre(String Nombre) { this.Nombre = Nombre; }

    public int getPtos_T_Obt() { return Ptos_T_Obt; }
    public void setPtos_T_Obt(int Ptos_T_Obt) { this.Ptos_T_Obt = Ptos_T_Obt; }

    public LocalDate getFecha_Insc() { return Fecha_Insc; }
    public void setFecha_Insc(LocalDate Fecha_Insc) { this.Fecha_Insc = Fecha_Insc; }


}
