package pds.actividad02.escenario01;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Patrón Composite – rol COMPOSITE.
 * <p>
 * Grupo presupuestario que contiene partidas y/u otros grupos. Su valor total
 * es la suma recursiva de los valores de todos sus hijos, sin importar la
 * profundidad del árbol.
 */
public class GrupoPresupuestario extends ComponentePresupuestario {

    private final List<ComponentePresupuestario> hijos = new ArrayList<>();

    public GrupoPresupuestario(String codigo, String descripcion) {
        super(codigo, descripcion);
    }

    /** Agrega un hijo (partida o grupo). Devuelve this para encadenar llamadas. */
    public GrupoPresupuestario agregar(ComponentePresupuestario componente) {
        if (componente == null) {
            throw new IllegalArgumentException("El componente no puede ser nulo");
        }
        if (componente == this || contieneA(componente, this)) {
            throw new IllegalArgumentException(
                    "No se puede agregar " + componente.getCodigo() + ": se formaría un ciclo");
        }
        hijos.add(componente);
        return this;
    }

    public boolean eliminar(ComponentePresupuestario componente) {
        return hijos.remove(componente);
    }

    public List<ComponentePresupuestario> getHijos() {
        return Collections.unmodifiableList(hijos);
    }

    @Override
    public BigDecimal calcularValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ComponentePresupuestario hijo : hijos) {
            total = total.add(hijo.calcularValorTotal()); // delegación recursiva
        }
        return total;
    }

    @Override
    protected void mostrarEstructura(StringBuilder sb, int nivel) {
        sb.append(sangria(nivel))
          .append("+ [Grupo] ").append(getCodigo()).append(" ").append(getDescripcion())
          .append(": ").append(formatearValor(calcularValorTotal()))
          .append(System.lineSeparator());
        for (ComponentePresupuestario hijo : hijos) {
            hijo.mostrarEstructura(sb, nivel + 1);
        }
    }

    /** Indica si {@code buscado} aparece en el subárbol de {@code raiz}. */
    private static boolean contieneA(ComponentePresupuestario raiz, ComponentePresupuestario buscado) {
        if (raiz instanceof GrupoPresupuestario grupo) {
            for (ComponentePresupuestario hijo : grupo.hijos) {
                if (hijo == buscado || contieneA(hijo, buscado)) {
                    return true;
                }
            }
        }
        return false;
    }
}
