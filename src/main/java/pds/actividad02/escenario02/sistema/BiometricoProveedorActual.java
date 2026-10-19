package pds.actividad02.escenario02.sistema;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Dispositivo del proveedor que la universidad usa desde hace años. Ya cumple
 * el contrato {@link ProveedorAsistencia}, por lo que no necesita adaptador.
 * (Simulado en memoria.)
 */
public class BiometricoProveedorActual implements ProveedorAsistencia {

    private final Map<String, RegistroAsistencia> registros = new HashMap<>();

    public void registrar(RegistroAsistencia registro) {
        registros.put(clave(registro.identificacion(), registro.fecha()), registro);
    }

    @Override
    public Optional<RegistroAsistencia> obtenerAsistencia(String identificacion, LocalDate fecha) {
        return Optional.ofNullable(registros.get(clave(identificacion, fecha)));
    }

    private static String clave(String identificacion, LocalDate fecha) {
        return identificacion + "@" + fecha;
    }
}
