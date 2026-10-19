package pds.actividad02.escenario01;

import java.math.BigDecimal;

/**
 * Patrón Composite – rol LEAF.
 * <p>
 * Partida presupuestaria individual: elemento básico sobre el que se
 * registran los recursos. Su valor total es directamente el valor asignado.
 */
public class PartidaPresupuestaria extends ComponentePresupuestario {

    private final BigDecimal valorAsignado;

    public PartidaPresupuestaria(String codigo, String descripcion, BigDecimal valorAsignado) {
        super(codigo, descripcion);
        if (valorAsignado == null || valorAsignado.signum() < 0) {
            throw new IllegalArgumentException("El valor asignado debe ser mayor o igual a cero");
        }
        this.valorAsignado = valorAsignado;
    }

    public BigDecimal getValorAsignado() {
        return valorAsignado;
    }

    @Override
    public BigDecimal calcularValorTotal() {
        return valorAsignado;
    }

    @Override
    protected void mostrarEstructura(StringBuilder sb, int nivel) {
        sb.append(sangria(nivel))
          .append("- [Partida] ").append(getCodigo()).append(" ").append(getDescripcion())
          .append(": ").append(formatearValor(calcularValorTotal()))
          .append(System.lineSeparator());
    }
}
