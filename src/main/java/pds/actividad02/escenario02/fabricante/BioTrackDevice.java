package pds.actividad02.escenario02.fabricante;

import java.util.ArrayList;
import java.util.List;

/**
 * Patrón Adapter – rol ADAPTEE.
 * <p>
 * Biblioteca del fabricante "BioTrack" (NO MODIFICABLE). Su interfaz es
 * incompatible con el sistema institucional: identifica al empleado con un
 * número entero, recibe un rango de tiempo en segundos epoch y devuelve una
 * colección de marcaciones crudas en lugar de un registro diario.
 * (Simulada en memoria para la demostración.)
 */
public class BioTrackDevice {

    private final String serialNumber;
    private final List<PunchRecord> memory = new ArrayList<>();

    public BioTrackDevice(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    /** Simula una marcación realizada en el equipo. */
    public void storePunch(PunchRecord punch) {
        memory.add(punch);
    }

    /** Devuelve las marcaciones del empleado en el intervalo [fromEpoch, toEpoch). */
    public List<PunchRecord> fetchPunches(int employeeNo, long fromEpoch, long toEpoch) {
        List<PunchRecord> result = new ArrayList<>();
        for (PunchRecord p : memory) {
            if (p.getEmployeeNo() == employeeNo
                    && p.getEpochSeconds() >= fromEpoch
                    && p.getEpochSeconds() < toEpoch) {
                result.add(p);
            }
        }
        return result;
    }
}
