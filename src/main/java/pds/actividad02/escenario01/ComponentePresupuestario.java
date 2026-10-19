package pds.actividad02.escenario01;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Patrón Composite – rol COMPONENT.
 * <p>
 * Abstracción común para partidas individuales y grupos presupuestarios.
 * El cliente trabaja únicamente con este tipo, sin distinguir si el
 * elemento es una hoja (partida) o un compuesto (grupo).
 */
public abstract class ComponentePresupuestario {

    private final String codigo;
    private final String descripcion;

    protected ComponentePresupuestario(String codigo, String descripcion) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio");
        }
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** Valor total del elemento (directo en la hoja, recursivo en el compuesto). */
    public abstract BigDecimal calcularValorTotal();

    /** Devuelve la estructura del elemento como texto con sangría jerárquica. */
    public String mostrarEstructura() {
        StringBuilder sb = new StringBuilder();
        mostrarEstructura(sb, 0);
        return sb.toString();
    }

    /** Operación recursiva que cada participante implementa a su manera. */
    protected abstract void mostrarEstructura(StringBuilder sb, int nivel);

    protected static String formatearValor(BigDecimal valor) {
        return String.format(Locale.US, "$ %,.2f", valor);
    }

    protected static String sangria(int nivel) {
        return "    ".repeat(nivel);
    }
}
