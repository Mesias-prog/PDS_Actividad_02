package pds.actividad02.escenario02.sistema;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Patrón Adapter – rol TARGET.
 * <p>
 * Contrato estable que utiliza el sistema institucional para solicitar la
 * asistencia de un colaborador en una fecha determinada. Este contrato NO
 * cambia cuando se incorporan dispositivos de otros fabricantes.
 */
public interface ProveedorAsistencia {

    /**
     * @param identificacion identificación institucional del colaborador (cédula)
     * @param fecha          día consultado
     * @return el registro normalizado, o vacío si no existen marcaciones ese día
     */
    Optional<RegistroAsistencia> obtenerAsistencia(String identificacion, LocalDate fecha);
}
