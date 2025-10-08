package utilities;

import java.time.LocalDate;

public interface TurnoObserver {
    void onFiltrosCambiados(String cancha, LocalDate fecha);
}
