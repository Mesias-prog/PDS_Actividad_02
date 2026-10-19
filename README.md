# PDS – Actividad 02: Patrones estructurales

Maestría en Software · Patrones de Diseño de Software · Grupo XX

Implementación en **Java 21** (sin dependencias externas) de los dos escenarios de la actividad:

| Escenario | Problema | Patrón | Paquete |
|---|---|---|---|
| 01 – Presupuesto institucional | Jerarquía parte-todo de partidas y grupos que el cliente debe tratar de forma uniforme | **Composite** | `pds.actividad02.escenario01` |
| 02 – Asistencia con biométricos | Biblioteca de un fabricante con interfaz incompatible con el contrato institucional | **Adapter** (de objetos) | `pds.actividad02.escenario02` |

## Estructura

```
src/main/java/pds/actividad02/
├── Main.java                              # ejecuta ambas demostraciones
├── escenario01/                           # COMPOSITE
│   ├── ComponentePresupuestario.java      # Component (abstracta)
│   ├── PartidaPresupuestaria.java         # Leaf
│   ├── GrupoPresupuestario.java           # Composite
│   ├── ReportePresupuesto.java            # Client
│   └── AppPresupuesto.java                # demo
└── escenario02/                           # ADAPTER
    ├── sistema/                           # lado institucional (estable)
    │   ├── ProveedorAsistencia.java       # Target
    │   ├── RegistroAsistencia.java        # dato normalizado (record)
    │   ├── SistemaAsistenciaInstitucional.java  # Client
    │   └── BiometricoProveedorActual.java # proveedor que ya cumple el contrato
    ├── fabricante/                        # biblioteca de terceros (NO modificable)
    │   ├── BioTrackDevice.java            # Adaptee
    │   ├── PunchRecord.java
    │   └── PunchType.java
    ├── adaptador/
    │   └── BioTrackAsistenciaAdapter.java # Adapter
    └── AppAsistencia.java                 # demo
src/test/java/pds/actividad02/Pruebas.java # 16 verificaciones sin JUnit
docs/uml/                                  # diagramas de clases (SVG/PNG) y su generador
```

## Compilar y ejecutar

Requiere JDK 17 o superior.

Linux / macOS / Git Bash:

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out pds.actividad02.Main
java -cp out pds.actividad02.Pruebas
```

Windows PowerShell:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out pds.actividad02.Main
java -cp out pds.actividad02.Pruebas
```

## Diagramas UML

- `docs/uml/escenario01_composite.png`
- `docs/uml/escenario02_adapter.png`

Se regeneran con `python docs/uml/generar_uml.py` (usa Microsoft Edge en modo headless para exportar el PNG).

## Correspondencia UML ↔ código

| Rol GoF | Escenario 01 | Escenario 02 |
|---|---|---|
| Client | `ReportePresupuesto` | `SistemaAsistenciaInstitucional` |
| Component / Target | `ComponentePresupuestario` | `ProveedorAsistencia` |
| Leaf / Adaptee | `PartidaPresupuestaria` | `BioTrackDevice` |
| Composite / Adapter | `GrupoPresupuestario` | `BioTrackAsistenciaAdapter` |
