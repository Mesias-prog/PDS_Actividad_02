package pds.actividad02.escenario02.sistema;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Información normalizada de asistencia que espera el sistema institucional:
 * identificación del colaborador, fecha, hora de ingreso y hora de salida.
 * La hora de salida puede ser nula si el colaborador aún no ha marcado salida.
 */
public record RegistroAsistencia(String identificacion,
                                 LocalDate fecha,
                                 LocalTime horaIngreso,
                                 LocalTime horaSalida) {

    public RegistroAsistencia {
        if (identificacion == null || fecha == null || horaIngreso == null) {
            throw new IllegalArgumentException("identificación, fecha y hora de ingreso son obligatorias");
        }
    }

    /** Horas trabajadas; cero si todavía no existe marcación de salida. */
    public Duration tiempoTrabajado() {
        return horaSalida == null ? Duration.ZERO : Duration.between(horaIngreso, horaSalida);
    }
}
