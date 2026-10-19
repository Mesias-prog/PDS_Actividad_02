package pds.actividad02.escenario02.adaptador;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import pds.actividad02.escenario02.fabricante.BioTrackDevice;
import pds.actividad02.escenario02.fabricante.PunchRecord;
import pds.actividad02.escenario02.fabricante.PunchType;
import pds.actividad02.escenario02.sistema.ProveedorAsistencia;
import pds.actividad02.escenario02.sistema.RegistroAsistencia;

/**
 * Patrón Adapter – rol ADAPTER (adaptador de objetos).
 * <p>
 * Implementa el contrato institucional {@link ProveedorAsistencia} y, por
 * composición, delega en {@link BioTrackDevice}. Realiza las transformaciones:
 * <ol>
 *   <li>identificación (cédula) → número interno de empleado del equipo;</li>
 *   <li>fecha local → rango [inicio, fin) en segundos epoch;</li>
 *   <li>colección de {@link PunchRecord} → un {@link RegistroAsistencia}
 *       (primer CHECK_IN como ingreso y último CHECK_OUT como salida).</li>
 * </ol>
 */
public class BioTrackAsistenciaAdapter implements ProveedorAsistencia {

    private final BioTrackDevice dispositivo;
    private final Map<String, Integer> identificacionANumeroEmpleado;
    private final ZoneId zonaHoraria;

    public BioTrackAsistenciaAdapter(BioTrackDevice dispositivo,
                                     Map<String, Integer> identificacionANumeroEmpleado,
                                     ZoneId zonaHoraria) {
        this.dispositivo = dispositivo;
        this.identificacionANumeroEmpleado = Map.copyOf(identificacionANumeroEmpleado);
        this.zonaHoraria = zonaHoraria;
    }

    @Override
    public Optional<RegistroAsistencia> obtenerAsistencia(String identificacion, LocalDate fecha) {
        Integer numeroEmpleado = identificacionANumeroEmpleado.get(identificacion);
        if (numeroEmpleado == null) {
            return Optional.empty(); // colaborador no enrolado en este dispositivo
        }

        long desde = fecha.atStartOfDay(zonaHoraria).toEpochSecond();
        long hasta = fecha.plusDays(1).atStartOfDay(zonaHoraria).toEpochSecond();
        List<PunchRecord> marcaciones = dispositivo.fetchPunches(numeroEmpleado, desde, hasta);

        Optional<LocalTime> ingreso = marcaciones.stream()
                .filter(m -> m.getType() == PunchType.CHECK_IN)
                .min(Comparator.comparingLong(PunchRecord::getEpochSeconds))
                .map(this::aHoraLocal);
        if (ingreso.isEmpty()) {
            return Optional.empty();
        }
        LocalTime salida = marcaciones.stream()
                .filter(m -> m.getType() == PunchType.CHECK_OUT)
                .max(Comparator.comparingLong(PunchRecord::getEpochSeconds))
                .map(this::aHoraLocal)
                .orElse(null);

        return Optional.of(new RegistroAsistencia(identificacion, fecha, ingreso.get(), salida));
    }

    private LocalTime aHoraLocal(PunchRecord marcacion) {
        return Instant.ofEpochSecond(marcacion.getEpochSeconds()).atZone(zonaHoraria).toLocalTime();
    }
}
