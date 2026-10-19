package pds.actividad02.escenario02;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import pds.actividad02.escenario02.adaptador.BioTrackAsistenciaAdapter;
import pds.actividad02.escenario02.fabricante.BioTrackDevice;
import pds.actividad02.escenario02.fabricante.PunchRecord;
import pds.actividad02.escenario02.fabricante.PunchType;
import pds.actividad02.escenario02.sistema.BiometricoProveedorActual;
import pds.actividad02.escenario02.sistema.ProveedorAsistencia;
import pds.actividad02.escenario02.sistema.RegistroAsistencia;
import pds.actividad02.escenario02.sistema.SistemaAsistenciaInstitucional;

/** Programa de demostración del Escenario 02 (Adapter). */
public class AppAsistencia {

    public static final ZoneId ZONA = ZoneId.of("America/Guayaquil");
    public static final LocalDate FECHA = LocalDate.of(2026, 9, 24);

    public static BiometricoProveedorActual crearProveedorActual() {
        BiometricoProveedorActual actual = new BiometricoProveedorActual();
        actual.registrar(new RegistroAsistencia("0102030405", FECHA, LocalTime.of(8, 0), LocalTime.of(17, 0)));
        return actual;
    }

    public static BioTrackDevice crearDispositivoBioTrack() {
        BioTrackDevice equipo = new BioTrackDevice("BT-2026-0042");
        int empleado = 7781;
        equipo.storePunch(new PunchRecord(empleado, epoch(FECHA, 7, 52), PunchType.CHECK_IN));
        equipo.storePunch(new PunchRecord(empleado, epoch(FECHA, 13, 0), PunchType.BREAK_START));
        equipo.storePunch(new PunchRecord(empleado, epoch(FECHA, 14, 0), PunchType.BREAK_END));
        equipo.storePunch(new PunchRecord(empleado, epoch(FECHA, 16, 45), PunchType.CHECK_OUT));
        equipo.storePunch(new PunchRecord(empleado, epoch(FECHA, 17, 10), PunchType.CHECK_OUT));
        // Marcación de otro día: el adaptador debe ignorarla.
        equipo.storePunch(new PunchRecord(empleado, epoch(FECHA.plusDays(1), 8, 5), PunchType.CHECK_IN));
        return equipo;
    }

    public static Map<String, Integer> directorioBioTrack() {
        return Map.of("1712345678", 7781);
    }

    public static void main(String[] args) {
        ProveedorAsistencia proveedorActual = crearProveedorActual();
        ProveedorAsistencia proveedorNuevo = new BioTrackAsistenciaAdapter(
                crearDispositivoBioTrack(), directorioBioTrack(), ZONA);

        System.out.println("=== Escenario 02: Patrón Adapter ===");
        for (ProveedorAsistencia proveedor : List.of(proveedorActual, proveedorNuevo)) {
            SistemaAsistenciaInstitucional sistema = new SistemaAsistenciaInstitucional(proveedor);
            System.out.println("Proveedor: " + proveedor.getClass().getSimpleName());
            System.out.println("  " + sistema.consultarAsistencia("0102030405", FECHA));
            System.out.println("  " + sistema.consultarAsistencia("1712345678", FECHA));
        }
    }

    private static long epoch(LocalDate fecha, int hora, int minuto) {
        return fecha.atTime(hora, minuto).atZone(ZONA).toEpochSecond();
    }
}
