package pds.actividad02.escenario01;

/**
 * Patrón Composite – rol CLIENT.
 * <p>
 * Consulta y procesa el presupuesto a través de {@link ComponentePresupuestario}.
 * No usa instanceof ni conoce si recibe una partida o un grupo, por lo que
 * la estructura puede crecer con nuevos niveles sin modificar esta clase.
 */
public class ReportePresupuesto {

    public String generar(ComponentePresupuestario elemento) {
        return "Código:      " + elemento.getCodigo() + System.lineSeparator()
             + "Descripción: " + elemento.getDescripcion() + System.lineSeparator()
             + "Valor total: " + ComponentePresupuestario.formatearValor(elemento.calcularValorTotal())
             + System.lineSeparator()
             + "Estructura:" + System.lineSeparator()
             + elemento.mostrarEstructura();
    }
}
