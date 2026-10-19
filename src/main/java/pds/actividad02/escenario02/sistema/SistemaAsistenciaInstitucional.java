package pds.actividad02.escenario02.sistema;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Patrón Adapter – rol CLIENT.
 * <p>
 * Sistema institucional de asistencia. Solo depende de {@link ProveedorAsistencia};
 * desconoce qué fabricante de biométrico está detrás.
 */
public class SistemaAsistenciaInstitucional {

    private final ProveedorAsistencia proveedor;

    public SistemaAsistenciaInstitucional(ProveedorAsistencia proveedor) {
        this.proveedor = proveedor;
    }

    public String consultarAsistencia(String identificacion, LocalDate fecha) {
        Optional<RegistroAsistencia> registro = proveedor.obtenerAsistencia(identificacion, fecha);
        if (registro.isEmpty()) {
            return identificacion + " | " + fecha + " | sin marcaciones";
        }
        RegistroAsistencia r = registro.get();
        Duration t = r.tiempoTrabajado();
        return String.format("%s | %s | ingreso %s | salida %s | %dh %02dm",
                r.identificacion(), r.fecha(), r.horaIngreso(),
                r.horaSalida() == null ? "--:--" : r.horaSalida(),
                t.toHours(), t.toMinutesPart());
    }
}
