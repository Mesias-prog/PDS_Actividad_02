"""Genera los diagramas de clases UML (SVG) de ambos escenarios.

Uso:  python docs/uml/generar_uml.py
Salida: docs/uml/escenario01_composite.svg y docs/uml/escenario02_adapter.svg
"""
from html import escape
from pathlib import Path

LH = 18          # alto de línea
PAD = 8          # margen interno
CHAR_W = 6.9     # ancho aproximado de carácter (Segoe UI 12.5px)

ROLE_COLORS = {
    "Client": ("#E8EAF6", "#3949AB"),
    "Component": ("#E3F2FD", "#1565C0"),
    "Target": ("#E3F2FD", "#1565C0"),
    "Leaf": ("#E8F5E9", "#2E7D32"),
    "Composite": ("#FFF3E0", "#E65100"),
    "Adapter": ("#FFF3E0", "#E65100"),
    "Adaptee": ("#FFEBEE", "#C62828"),
    "": ("#FAFAFA", "#616161"),
}


class Box:
    def __init__(self, x, y, w, role, name, attrs=(), methods=(), stereo=None, abstract=False):
        self.x, self.y, self.w = x, y, w
        self.role, self.name, self.attrs, self.methods = role, name, list(attrs), list(methods)
        self.stereo, self.abstract = stereo, abstract
        head_lines = 1 + (1 if role else 0) + (1 if stereo else 0)
        self.head_h = head_lines * LH + PAD
        self.attr_h = max(1, len(self.attrs)) * LH + PAD if self.attrs or self.methods else 0
        self.meth_h = len(self.methods) * LH + PAD if self.methods else 0
        self.h = self.head_h + self.attr_h + self.meth_h
        for line in [name] + self.attrs + self.methods:
            text = line[0] if isinstance(line, tuple) else line
            need = len(text) * CHAR_W + 2 * PAD
            assert need <= w + 4, f"Texto demasiado ancho en {name}: '{text}' ({need:.0f} > {w})"

    # anclajes
    def top(self, dx=0): return (self.x + self.w / 2 + dx, self.y)
    def bottom(self, dx=0): return (self.x + self.w / 2 + dx, self.y + self.h)
    def left(self, dy=0): return (self.x, self.y + self.h / 2 + dy)
    def right(self, dy=0): return (self.x + self.w, self.y + self.h / 2 + dy)

    def svg(self):
        fill, stroke = ROLE_COLORS[self.role]
        out = [f'<rect x="{self.x}" y="{self.y}" width="{self.w}" height="{self.h}" rx="3" '
               f'fill="{fill}" stroke="{stroke}" stroke-width="1.4"/>']
        cy = self.y + PAD / 2 + LH - 4
        cx = self.x + self.w / 2
        if self.role:
            out.append(f'<text x="{cx}" y="{cy}" text-anchor="middle" class="role" fill="{stroke}">'
                       f'Rol GoF: {self.role}</text>')
            cy += LH
        if self.stereo:
            out.append(f'<text x="{cx}" y="{cy}" text-anchor="middle" class="stereo">'
                       f'{escape("«" + self.stereo + "»")}</text>')
            cy += LH
        cls = "name abstract" if self.abstract else "name"
        out.append(f'<text x="{cx}" y="{cy}" text-anchor="middle" class="{cls}">{escape(self.name)}</text>')
        y = self.y + self.head_h
        for section in (self.attrs, self.methods) if (self.attrs or self.methods) else ():
            if section is self.methods and not self.methods:
                continue
            out.append(f'<line x1="{self.x}" y1="{y}" x2="{self.x + self.w}" y2="{y}" stroke="{stroke}" stroke-width="1"/>')
            ty = y + PAD / 2 + LH - 4
            for item in section:
                text, italic = (item if isinstance(item, tuple) else (item, False))
                style = ' font-style="italic"' if italic else ""
                out.append(f'<text x="{self.x + PAD}" y="{ty}" class="member"{style}>{escape(text)}</text>')
                ty += LH
            y += (max(1, len(section)) * LH + PAD)
        return "\n".join(out)


def path(points, marker_end=None, marker_start=None, dashed=False):
    d = "M " + " L ".join(f"{x:.1f} {y:.1f}" for x, y in points)
    attrs = ' stroke-dasharray="7 5"' if dashed else ""
    if marker_end:
        attrs += f' marker-end="url(#{marker_end})"'
    if marker_start:
        attrs += f' marker-start="url(#{marker_start})"'
    return f'<path d="{d}" fill="none" stroke="#37474F" stroke-width="1.4"{attrs}/>'


def label(x, y, text, anchor="start", cls="edge"):
    return f'<text x="{x}" y="{y}" text-anchor="{anchor}" class="{cls}">{escape(text)}</text>'


def document(width, height, title, body):
    return f'''<svg xmlns="http://www.w3.org/2000/svg" xml:space="preserve" width="{width}" height="{height}" viewBox="0 0 {width} {height}">
<defs>
  <marker id="triangle" viewBox="0 0 20 20" refX="19" refY="10" markerWidth="16" markerHeight="16" orient="auto-start-reverse" markerUnits="userSpaceOnUse">
    <path d="M 1 1 L 19 10 L 1 19 z" fill="#ffffff" stroke="#37474F" stroke-width="1.4"/>
  </marker>
  <marker id="diamond" viewBox="0 0 24 14" refX="23" refY="7" markerWidth="22" markerHeight="13" orient="auto-start-reverse" markerUnits="userSpaceOnUse">
    <path d="M 1 7 L 12 1 L 23 7 L 12 13 z" fill="#ffffff" stroke="#37474F" stroke-width="1.4"/>
  </marker>
  <marker id="arrow" viewBox="0 0 20 20" refX="19" refY="10" markerWidth="13" markerHeight="13" orient="auto-start-reverse" markerUnits="userSpaceOnUse">
    <path d="M 1 2 L 19 10 L 1 18" fill="none" stroke="#37474F" stroke-width="1.6"/>
  </marker>
</defs>
<style>
  text {{ font-family: "Segoe UI", Arial, sans-serif; fill: #212121; white-space: pre; }}
  .title {{ font-size: 17px; font-weight: 700; }}
  .name {{ font-size: 14px; font-weight: 700; }}
  .abstract {{ font-style: italic; }}
  .role {{ font-size: 11.5px; font-weight: 600; letter-spacing: .3px; }}
  .stereo {{ font-size: 12px; }}
  .member {{ font-size: 12.5px; }}
  .edge {{ font-size: 12px; fill: #37474F; }}
  .note {{ font-size: 12px; fill: #5D4037; }}
  .pkg {{ font-size: 12.5px; font-weight: 700; fill: #C62828; }}
</style>
<rect width="100%" height="100%" fill="#ffffff"/>
<text x="24" y="30" class="title">{escape(title)}</text>
{body}
</svg>
'''


def note(x, y, w, lines):
    h = len(lines) * 17 + 12
    fold = 12
    out = [f'<path d="M {x} {y} L {x + w - fold} {y} L {x + w} {y + fold} L {x + w} {y + h} L {x} {y + h} z" '
           f'fill="#FFFDE7" stroke="#A1887F" stroke-width="1.1"/>',
           f'<path d="M {x + w - fold} {y} L {x + w - fold} {y + fold} L {x + w} {y + fold}" fill="none" stroke="#A1887F" stroke-width="1.1"/>']
    for i, line in enumerate(lines):
        out.append(f'<text x="{x + 9}" y="{y + 20 + i * 17}" class="note">{escape(line)}</text>')
    return "\n".join(out), h


def escenario01():
    comp = Box(470, 60, 400, "Component", "ComponentePresupuestario", stereo="abstract", abstract=True,
               attrs=["- codigo: String", "- descripcion: String"],
               methods=["+ getCodigo(): String",
                        "+ getDescripcion(): String",
                        ("+ calcularValorTotal(): BigDecimal", True),
                        "+ mostrarEstructura(): String",
                        ("# mostrarEstructura(sb: StringBuilder, nivel: int)", True)])
    client = Box(40, 95, 330, "Client", "ReportePresupuesto",
                 methods=["+ generar(elemento: ComponentePresupuestario)", "      : String"])
    leaf = Box(150, 440, 360, "Leaf", "PartidaPresupuestaria",
               attrs=["- valorAsignado: BigDecimal"],
               methods=["+ getValorAsignado(): BigDecimal",
                        "+ calcularValorTotal(): BigDecimal",
                        "# mostrarEstructura(sb: StringBuilder, nivel: int)"])
    composite = Box(590, 440, 450, "Composite", "GrupoPresupuestario",
                    attrs=["- hijos: List<ComponentePresupuestario>"],
                    methods=["+ agregar(c: ComponentePresupuestario): GrupoPresupuestario",
                             "+ eliminar(c: ComponentePresupuestario): boolean",
                             "+ getHijos(): List<ComponentePresupuestario>",
                             "+ calcularValorTotal(): BigDecimal",
                             "# mostrarEstructura(sb: StringBuilder, nivel: int)"])
    body = [b.svg() for b in (comp, client, leaf, composite)]

    # Generalización (árbol con un solo triángulo)
    bus_y = 380
    cx = comp.bottom()[0] - 60
    body.append(path([(cx, bus_y), (cx, comp.y + comp.h)], marker_end="triangle"))
    body.append(path([(leaf.top()[0], leaf.y), (leaf.top()[0], bus_y), (cx, bus_y)]))
    body.append(path([(composite.top(-80)[0], composite.y), (composite.top(-80)[0], bus_y), (cx, bus_y)]))

    # Agregación recursiva: Composite ◇—> Component (hijos 0..*)
    ax = composite.x + composite.w - 40
    ry = comp.y + 60
    body.append(path([(ax, composite.y), (ax, ry), (comp.x + comp.w, ry)],
                     marker_start="diamond", marker_end="arrow"))
    body.append(label(comp.x + comp.w + 8, ry - 8, "hijos"))
    body.append(label(comp.x + comp.w + 8, ry + 20, "0..*"))
    body.append(label(ax + 8, composite.y - 12, "1"))

    # Dependencia del cliente
    dy = client.y + client.h / 2
    body.append(path([(client.x + client.w, dy), (comp.x, dy)], marker_end="arrow", dashed=True))
    body.append(label((client.x + client.w + comp.x) / 2, dy - 8, "«use»", anchor="middle"))

    note_y = composite.y + composite.h + 40
    n, nh = note(composite.x + 40, note_y, 320, ["calcularValorTotal() en el Composite:",
                                                 "  total = 0",
                                                 "  para cada hijo en hijos:",
                                                 "      total += hijo.calcularValorTotal()",
                                                 "  return total"])
    body.append(n)
    body.append(path([(composite.x + 120, note_y), (composite.x + 120, composite.y + composite.h)], dashed=True))
    n2, _ = note(leaf.x, leaf.y + leaf.h + 40, 300, ["calcularValorTotal() en la hoja:",
                                                    "  return valorAsignado"])
    body.append(n2)
    body.append(path([(leaf.x + 120, leaf.y + leaf.h + 40), (leaf.x + 120, leaf.y + leaf.h)], dashed=True))
    return document(1080, int(note_y + nh + 30), "Escenario 01 – Patrón Composite: estructura jerárquica del presupuesto", "\n".join(body))


def escenario02():
    client = Box(30, 60, 400, "Client", "SistemaAsistenciaInstitucional",
                 attrs=["- proveedor: ProveedorAsistencia"],
                 methods=["+ consultarAsistencia(identificacion: String,",
                          "      fecha: LocalDate): String"])
    target = Box(520, 60, 420, "Target", "ProveedorAsistencia", stereo="interface",
                 methods=[("+ obtenerAsistencia(identificacion: String,", True),
                          ("      fecha: LocalDate): Optional<RegistroAsistencia>", True)])
    registro = Box(1030, 60, 310, "", "RegistroAsistencia", stereo="record",
                   attrs=["identificacion: String", "fecha: LocalDate",
                          "horaIngreso: LocalTime", "horaSalida: LocalTime"],
                   methods=["+ tiempoTrabajado(): Duration"])
    actual = Box(30, 330, 400, "", "BiometricoProveedorActual",
                 attrs=["- registros: Map<String, RegistroAsistencia>"],
                 methods=["+ registrar(registro: RegistroAsistencia): void",
                          "+ obtenerAsistencia(identificacion: String,",
                          "      fecha: LocalDate): Optional<RegistroAsistencia>"])
    adapter = Box(490, 330, 480, "Adapter", "BioTrackAsistenciaAdapter",
                  attrs=["- dispositivo: BioTrackDevice",
                         "- identificacionANumeroEmpleado: Map<String, Integer>",
                         "- zonaHoraria: ZoneId"],
                  methods=["+ BioTrackAsistenciaAdapter(dispositivo, directorio, zona)",
                           "+ obtenerAsistencia(identificacion: String,",
                           "      fecha: LocalDate): Optional<RegistroAsistencia>",
                           "- aHoraLocal(marcacion: PunchRecord): LocalTime"])
    adaptee = Box(490, 690, 480, "Adaptee", "BioTrackDevice",
                  attrs=["- serialNumber: String", "- memory: List<PunchRecord>"],
                  methods=["+ fetchPunches(employeeNo: int, fromEpoch: long,",
                           "      toEpoch: long): List<PunchRecord>",
                           "+ storePunch(punch: PunchRecord): void"])
    punch = Box(1050, 690, 290, "", "PunchRecord",
                attrs=["- employeeNo: int", "- epochSeconds: long", "- type: PunchType"],
                methods=["+ getEmployeeNo(): int", "+ getEpochSeconds(): long", "+ getType(): PunchType"])
    ptype = Box(1050, 880, 290, "", "PunchType", stereo="enumeration",
                attrs=["CHECK_IN", "CHECK_OUT", "BREAK_START", "BREAK_END"])

    body = []
    # Paquete del fabricante (no modificable)
    body.append('<rect x="465" y="650" width="900" height="380" rx="6" fill="#FFF8F8" stroke="#C62828" '
                'stroke-width="1.2" stroke-dasharray="9 6"/>')
    body.append('<text x="1352" y="672" text-anchor="end" class="pkg">fabricante  «biblioteca de terceros – no modificable»</text>')
    body += [b.svg() for b in (client, target, registro, actual, adapter, adaptee, punch, ptype)]

    # Cliente -> Target (asociación)
    y = client.y + 50
    body.append(path([(client.x + client.w, y), (target.x, y)], marker_end="arrow"))
    body.append(label(target.x - 8, y - 8, "proveedor  1", anchor="end"))

    # Realizaciones hacia Target
    bus_y = 270
    tx = target.bottom()[0]
    body.append(path([(tx, bus_y), (tx, target.y + target.h)], marker_end="triangle", dashed=True))
    body.append(path([(actual.top()[0], actual.y), (actual.top()[0], bus_y), (tx, bus_y)], dashed=True))
    body.append(path([(adapter.top()[0], adapter.y), (adapter.top()[0], bus_y), (tx, bus_y)], dashed=True))

    # Target ..> RegistroAsistencia
    ry = target.y + 45
    body.append(path([(target.x + target.w, ry), (registro.x, ry)], marker_end="arrow", dashed=True))
    body.append(label((target.x + target.w + registro.x) / 2, ry - 8, "«use»", anchor="middle"))
    # Adapter ..> RegistroAsistencia «create»
    ax0 = adapter.x + adapter.w
    ay0 = adapter.y + 40
    rx = registro.x + 110
    body.append(path([(ax0, ay0), (rx, ay0), (rx, registro.y + registro.h)], marker_end="arrow", dashed=True))
    body.append(label(rx + 8, ay0 - 8, "«create»"))

    # Adapter -> Adaptee (composición de objetos / delegación)
    x = adaptee.top()[0]
    body.append(path([(x, adapter.y + adapter.h), (x, adaptee.y)], marker_end="arrow"))
    body.append(label(x + 10, adaptee.y - 16, "dispositivo  1"))
    body.append(label(x + 10, adapter.y + adapter.h + 22, "delega en fetchPunches(...)", cls="note"))

    # Adaptee -> PunchRecord, PunchRecord -> PunchType
    py = punch.y + 60
    body.append(path([(adaptee.x + adaptee.w, py), (punch.x, py)], marker_end="arrow"))
    body.append(label(punch.x - 8, py - 8, "0..*", anchor="end"))
    body.append(path([(punch.bottom()[0], punch.y + punch.h), (punch.bottom()[0], ptype.y)], marker_end="arrow"))
    body.append(label(punch.bottom()[0] + 8, ptype.y - 10, "type  1"))

    n, nh = note(30, 600, 410, ["Transformaciones del adaptador:",
                                "1. cédula → employeeNo (directorio)",
                                "2. fecha → [inicio, fin) en segundos epoch",
                                "3. List<PunchRecord> → RegistroAsistencia:",
                                "    ingreso = primer CHECK_IN",
                                "    salida  = último CHECK_OUT",
                                "4. epoch → LocalTime (zona America/Guayaquil)"])
    body.append(n)
    body.append(path([(235, 600), (235, 490), (adapter.x, 490)], dashed=True))
    return document(1380, 1050, "Escenario 02 – Patrón Adapter (de objetos): integración de biométricos", "\n".join(body))


def exportar_png(svg_path):
    """Convierte el SVG a PNG (escala 2x) con Microsoft Edge en modo headless, si está instalado."""
    import re
    import shutil
    import subprocess
    edge = shutil.which("msedge") or r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe"
    if not Path(edge).exists():
        print("Edge no encontrado; se omite la exportación a PNG")
        return
    svg = svg_path.read_text(encoding="utf-8")
    w, h = re.search(r'width="(\d+)" height="(\d+)"', svg).groups()
    subprocess.run([edge, "--headless=new", "--disable-gpu", "--hide-scrollbars",
                    "--force-device-scale-factor=2", f"--window-size={w},{h}",
                    f"--screenshot={svg_path.with_suffix('.png')}", svg_path.as_uri()],
                   check=True, capture_output=True)


if __name__ == "__main__":
    out = Path(__file__).resolve().parent
    for nombre, generador in (("escenario01_composite.svg", escenario01),
                              ("escenario02_adapter.svg", escenario02)):
        destino = out / nombre
        destino.write_text(generador(), encoding="utf-8")
        exportar_png(destino)
    print("Diagramas generados en", out)
