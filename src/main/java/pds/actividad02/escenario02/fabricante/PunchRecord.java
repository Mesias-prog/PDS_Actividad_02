package pds.actividad02.escenario02.fabricante;

/**
 * Biblioteca del fabricante "BioTrack" (NO MODIFICABLE).
 * Marcación cruda: número interno de empleado, instante en segundos epoch (UTC)
 * y tipo de marcación.
 */
public final class PunchRecord {

    private final int employeeNo;
    private final long epochSeconds;
    private final PunchType type;

    public PunchRecord(int employeeNo, long epochSeconds, PunchType type) {
        this.employeeNo = employeeNo;
        this.epochSeconds = epochSeconds;
        this.type = type;
    }

    public int getEmployeeNo() {
        return employeeNo;
    }

    public long getEpochSeconds() {
        return epochSeconds;
    }

    public PunchType getType() {
        return type;
    }
}
