package controllers;

import models.Torneo;

public class DataManager {
    private static DataManager instance;
    private Torneo torneoSeleccionado;

    private DataManager() {}

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public Torneo getTorneoSeleccionado() {
        return torneoSeleccionado;
    }

    public void setTorneoSeleccionado(Torneo torneo) {
        this.torneoSeleccionado = torneo;
    }
}
