package pds.actividad02;

import pds.actividad02.escenario01.AppPresupuesto;
import pds.actividad02.escenario02.AppAsistencia;

/** Ejecuta las demostraciones de ambos escenarios. */
public class Main {

    public static void main(String[] args) {
        AppPresupuesto.main(args);
        System.out.println();
        AppAsistencia.main(args);
    }
}
