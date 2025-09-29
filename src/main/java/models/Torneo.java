package models;
import java.time.LocalDate;

public class Torneo {

    private int Categoria;
    private LocalDate Fecha;
    private String Premio1;
    private String Premio2;
    private int Valor_Inscripcion;
    private Es Estados;

    public Torneo( int Categoria,  LocalDate Fecha,  String Premio1,  String Premio2, int Valor_Inscripcion, Es Estados) {

        this.Categoria = Categoria;
        this.Fecha = Fecha;
        this.Premio1 = Premio1;
        this.Premio2 = Premio2;
        this.Valor_Inscripcion = Valor_Inscripcion;
        this.Estados = Estados;
    }

}
