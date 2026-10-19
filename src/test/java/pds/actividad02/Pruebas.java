package pds.actividad02;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import pds.actividad02.escenario01.AppPresupuesto;
import pds.actividad02.escenario01.ComponentePresupuestario;
import pds.actividad02.escenario01.GrupoPresupuestario;
import pds.actividad02.escenario01.PartidaPresupuestaria;
import pds.actividad02.escenario02.AppAsistencia;
import pds.actividad02.escenario02.adaptador.BioTrackAsistenciaAdapter;
import pds.actividad02.escenario02.sistema.ProveedorAsistencia;
import pds.actividad02.escenario02.sistema.RegistroAsistencia;

/**
 * Pruebas sin dependencias externas (no requiere JUnit).
 * Termina con código de salida 1 si alguna verificación falla.
 */
public class Pruebas {

    private static int ok = 0;
    private static int fallos = 0;

    public static void main(String[] args) {
        // ---------- Escenario 01: Composite ----------
        GrupoPresupuestario presupuesto = AppPresupuesto.construirPresupuesto();
        verificar("Total general recursivo", new BigDecimal("145000.00"), presupuesto.calcularValorTotal());

        ComponentePresupuestario tecnologias = presupuesto.getHijos().get(0);
        verificar("Total de Tecnologías de la Información", new BigDecimal("137000.00"), tecnologias.calcularValorTotal());

        ComponentePresupuestario partida = new PartidaPresupuestaria("X", "Partida", new BigDecimal("10.50"));
        verificar("Total de una partida = valor asignado", new BigDecimal("10.50"), partida.calcularValorTotal());

        verificar("Grupo vacío suma cero", BigDecimal.ZERO, new GrupoPresupuestario("G", "Vacío").calcularValorTotal());

        GrupoPresupuestario nuevoNivel = new GrupoPresupuestario("1.3", "Nuevo nivel")
                .agregar(new GrupoPresupuestario("1.3.1", "Subnivel")
                        .agregar(new PartidaPresupuestaria("1.3.1.1", "Partida profunda", new BigDecimal("5000.00"))));
        presupuesto.agregar(nuevoNivel);
        verificar("Nuevo nivel se suma sin cambiar el cliente", new BigDecimal("150000.00"), presupuesto.calcularValorTotal());

        verificar("Estructura muestra hojas anidadas", true,
                presupuesto.mostrarEstructura().contains("1.1.2.3.2 Equipos de red"));

        GrupoPresupuestario a = new GrupoPresupuestario("A", "A");
        GrupoPresupuestario b = new GrupoPresupuestario("B", "B");
        a.agregar(b);
        verificar("Se rechazan ciclos", true, lanzaExcepcion(() -> b.agregar(a)));
        verificar("Se rechaza agregarse a sí mismo", true, lanzaExcepcion(() -> a.agregar(a)));

        // ---------- Escenario 02: Adapter ----------
        LocalDate fecha = AppAsistencia.FECHA;
        ProveedorAsistencia adaptador = new BioTrackAsistenciaAdapter(
                AppAsistencia.crearDispositivoBioTrack(), AppAsistencia.directorioBioTrack(), AppAsistencia.ZONA);

        Optional<RegistroAsistencia> r = adaptador.obtenerAsistencia("1712345678", fecha);
        verificar("Adapter devuelve registro", true, r.isPresent());
        verificar("Identificación conservada", "1712345678", r.get().identificacion());
        verificar("Ingreso = primer CHECK_IN", LocalTime.of(7, 52), r.get().horaIngreso());
        verificar("Salida = último CHECK_OUT", LocalTime.of(17, 10), r.get().horaSalida());
        verificar("Colaborador no enrolado -> vacío", true, adaptador.obtenerAsistencia("9999999999", fecha).isEmpty());
        verificar("Día sin marcaciones -> vacío", true, adaptador.obtenerAsistencia("1712345678", fecha.minusDays(1)).isEmpty());
        verificar("Marcación del día siguiente no se mezcla", LocalTime.of(8, 5),
                adaptador.obtenerAsistencia("1712345678", fecha.plusDays(1)).get().horaIngreso());

        ProveedorAsistencia actual = AppAsistencia.crearProveedorActual();
        verificar("Proveedor actual sigue funcionando", LocalTime.of(8, 0),
                actual.obtenerAsistencia("0102030405", fecha).get().horaIngreso());

        System.out.printf("%nResultado: %d correctas, %d fallidas%n", ok, fallos);
        if (fallos > 0) {
            System.exit(1);
        }
    }

    private static void verificar(String nombre, Object esperado, Object obtenido) {
        boolean iguales = (esperado instanceof BigDecimal e && obtenido instanceof BigDecimal o)
                ? e.compareTo(o) == 0
                : esperado.equals(obtenido);
        if (iguales) {
            ok++;
            System.out.println("[OK]    " + nombre);
        } else {
            fallos++;
            System.out.println("[FALLA] " + nombre + " -> esperado " + esperado + ", obtenido " + obtenido);
        }
    }

    private static boolean lanzaExcepcion(Runnable accion) {
        try {
            accion.run();
            return false;
        } catch (IllegalArgumentException ex) {
            return true;
        }
    }
}
