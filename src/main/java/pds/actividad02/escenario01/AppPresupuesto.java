package pds.actividad02.escenario01;

import java.math.BigDecimal;

/** Programa de demostración del Escenario 01 (Composite). */
public class AppPresupuesto {

    public static GrupoPresupuestario construirPresupuesto() {
        GrupoPresupuestario comunicaciones = new GrupoPresupuestario("1.1.2.3", "Comunicaciones")
                .agregar(new PartidaPresupuestaria("1.1.2.3.1", "Enlaces de internet", new BigDecimal("18000.00")))
                .agregar(new PartidaPresupuestaria("1.1.2.3.2", "Equipos de red", new BigDecimal("9500.00")));

        GrupoPresupuestario infraestructura = new GrupoPresupuestario("1.1.2", "Infraestructura")
                .agregar(new PartidaPresupuestaria("1.1.2.1", "Servidores", new BigDecimal("45000.00")))
                .agregar(new PartidaPresupuestaria("1.1.2.2", "Almacenamiento", new BigDecimal("22000.00")))
                .agregar(comunicaciones);

        GrupoPresupuestario tecnologias = new GrupoPresupuestario("1.1", "Tecnologías de la Información")
                .agregar(new PartidaPresupuestaria("1.1.1", "Licenciamiento", new BigDecimal("30000.00")))
                .agregar(infraestructura)
                .agregar(new PartidaPresupuestaria("1.1.3", "Servicios", new BigDecimal("12500.00")));

        GrupoPresupuestario talentoHumano = new GrupoPresupuestario("1.2", "Talento Humano")
                .agregar(new PartidaPresupuestaria("1.2.1", "Capacitación", new BigDecimal("8000.00")));

        return new GrupoPresupuestario("1", "Presupuesto Institucional 2026")
                .agregar(tecnologias)
                .agregar(talentoHumano);
    }

    public static void main(String[] args) {
        GrupoPresupuestario presupuesto = construirPresupuesto();
        ReportePresupuesto reporte = new ReportePresupuesto();

        System.out.println("=== Escenario 01: Patrón Composite ===");
        System.out.println(reporte.generar(presupuesto));

        // El mismo cliente procesa un grupo interno y una partida suelta sin distinguirlos.
        ComponentePresupuestario tecnologias = presupuesto.getHijos().get(0);
        System.out.println(reporte.generar(tecnologias));
        System.out.println(reporte.generar(new PartidaPresupuestaria("9.9", "Partida suelta", new BigDecimal("150.00"))));
    }
}
