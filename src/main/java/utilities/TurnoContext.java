package utilities;

import models.Turno;

//----ESTA CLASE SE USA PARA PASAR EL TURNO SELECCIONADO EN LA -----
//----PANTALLA LISTAR TURNO A LA PANTALLA CREAR RESERVA        -----

public class TurnoContext {
    private static Turno turnoSeleccionado;
    public static Turno getTurnoSeleccionado() {
        return turnoSeleccionado;
    }
    public static void setTurnoSeleccionado(Turno turno) {
        turnoSeleccionado = turno;
    }
    public static void limpiar() {
        turnoSeleccionado = null;
    }
}
