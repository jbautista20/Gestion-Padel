package utilities;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TurnoSubject {
    private List<TurnoObserver> observers = new ArrayList<>();
    private String canchaSeleccionada;
    private LocalDate fechaSeleccionada;

    public void addObserver(TurnoObserver observer) {
        observers.add(observer);
    }


    public void setCancha(String cancha) {
        this.canchaSeleccionada = cancha;
        notificarObservers();
    }

    public void setFecha(LocalDate fecha) {
        this.fechaSeleccionada = fecha;
        notificarObservers();
    }

    private void notificarObservers() {
        for (TurnoObserver observer : observers) {
            observer.onFiltrosCambiados(canchaSeleccionada, fechaSeleccionada);
        }
    }

}
