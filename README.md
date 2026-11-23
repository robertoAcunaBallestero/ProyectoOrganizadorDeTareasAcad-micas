# Sistema de Facturación con Prolog, Node.js y Neon

**Universidad Fidélitas — SC-250 Paradigmas de Programación — Grupo 5**

## Integrantes del grupo

- Melissa Solis Benavides
- Roberto Acuña Ballestero

Repositorio: https://github.com/robertoAcunaBallestero/PrologGrupo5

## 1. Descripción del proyecto

Proyecto académico que simula un sistema de facturación. La lógica de negocio se define en **SWI-Prolog**; la API utiliza **Node.js y Express**; la información se almacena en **Neon (PostgreSQL)**; y la interfaz utiliza **HTML, CSS y JavaScript**. GitHub permite trabajar en equipo y llevar control de versiones.

**Aviso:** Es una simulación educativa. No produce comprobantes electrónicos válidos ante el Ministerio de Hacienda de Costa Rica. Las tarifas de IVA utilizadas son las establecidas en el ejercicio académico.

## 2. Arquitectura

```mermaid
flowchart TD
    U[Usuario] --> W[Interfaz web: HTML, CSS y JavaScript]
    W --> A[API: Node.js y Express]
    A --> N[(Neon: PostgreSQL)]
    A --> P[Motor de reglas: SWI-Prolog]
```

La **integración completa** entre API, Neon y Prolog aún está pendiente. El diagrama representa la arquitectura objetivo, no un sistema ya terminado.

## 3. Estructura del repositorio

```text
PrologGrupo5/
├── api/
│   ├── package.json
│   ├── package-lock.json
│   ├── server.js
│   └── .env                 # se crea localmente; NO subir a GitHub
├── reglas/
│   ├── facturacion_datos.pl
│   ├── motor.pl
│   ├── pruebas.pl
│   └── demo.pl
├── sql/
│   └── facturacion_neon.sql
├── web/
│   ├── index.html
│   ├── styles.css
│   └── app.js
├── .gitignore
└── README.md
```

## 4. Requisitos de instalación

- Git, para clonar y actualizar el repositorio.
- Node.js 20 o superior y npm, para ejecutar la API.
- SWI-Prolog, para ejecutar las reglas y las pruebas.
- Una cuenta de Neon con acceso al proyecto PostgreSQL del grupo.
- Visual Studio 2022, Visual Studio Code u otro editor (opcional).

En Windows, comprobar las herramientas en una terminal:

```powershell
git --version
node --version
npm.cmd --version
swipl --version
```

En PowerShell se utiliza `npm.cmd` si la política de ejecución impide ejecutar `npm.ps1`.

## 5. Clonar el repositorio de GitHub

```powershell
git clone https://github.com/robertoAcunaBallestero/PrologGrupo5.git
cd PrologGrupo5
git remote -v
```

Si ya lo clonaste en Visual Studio, **no hace falta volver a clonarlo**. Abrí una terminal dentro de la carpeta del repositorio. Para descargar referencias remotas sin modificar archivos locales:

```powershell
git fetch origin
```

Antes de integrar cambios de otros miembros, revisá tu rama y tus modificaciones; `git fetch` no es lo mismo que `git pull`.

## 6. Preparar Neon PostgreSQL manualmente

**Antes de crear una base nueva:** confirmá si algún integrante ya creó el proyecto en Neon. Lo ideal es que todos trabajen sobre el mismo proyecto compartido y que el propietario dé los permisos correspondientes.

Si aún no existe un proyecto del grupo:

1. Entrá a https://console.neon.tech/ e iniciá sesión.
2. Elegí **Create project**.
3. Nombre sugerido: `facturacion-prolog-grupo5`.
4. Elegí una región disponible y mantené activado **Postgres database**. Los servicios adicionales no son necesarios para esta práctica.
5. Creá el proyecto.
6. En el panel, abrí **SQL Editor** y seleccioná la base de datos correcta (habitualmente `neondb`, si no fue renombrada).
7. En Visual Studio, abrí `sql/facturacion_neon.sql`; copiá **todo** su contenido.
8. Pegalo en el editor SQL de Neon y presioná **Run**.
9. Comprobá que se crearon las tablas y se insertaron los datos de ejemplo.

### Consultas para verificar la carga

Ejecutá esta consulta en **SQL Editor**:

```sql
SELECT 'productos' AS tabla, COUNT(*) AS filas FROM productos
UNION ALL SELECT 'clientes', COUNT(*) FROM clientes
UNION ALL SELECT 'kits', COUNT(*) FROM kits
UNION ALL SELECT 'kit_items', COUNT(*) FROM kit_items
UNION ALL SELECT 'facturas', COUNT(*) FROM facturas
UNION ALL SELECT 'lineas', COUNT(*) FROM lineas;
```

En una **base vacía** donde se ejecutó el archivo original una sola vez, los resultados esperados son:

| Tabla | Registros iniciales |
|---|---:|
| productos | 7 |
| clientes | 4 |
| kits | 2 |
| kit_items | 5 |
| facturas | 6 |
| lineas | 11 |

**Importante:** No ejecutés el script varias veces en la misma base. Aunque varias inserciones utilizan `ON CONFLICT DO NOTHING`, las inserciones de `lineas` no impiden duplicados si repetís el script. Además, en un proyecto compartido no debés reiniciar ni sobrescribir datos sin coordinarlo con el grupo.

Los seis encabezados de factura incluidos son datos iniciales: todavía no están emitidos ni tienen calculados y persistidos los totales en Neon.

## 7. Configurar la conexión desde Node.js

1. En Neon, buscá **Connect** o **Connection details** y copiá la cadena de conexión PostgreSQL del proyecto.
2. Dentro de la carpeta `api/`, creá un archivo local llamado `.env`.
3. Colocá la variable indicada abajo reemplazando el ejemplo por la cadena de Neon:

```dotenv
DATABASE_URL=postgresql://USUARIO:CONTRASENA@HOST/NOMBRE_BASE?sslmode=require
PORT=3000
```

**Nunca coloqués la conexión real en este README, en capturas públicas, en mensajes del grupo o en GitHub.** La contraseña forma parte de la URL. El archivo `.env` está excluido mediante `.gitignore`. Si una contraseña se publica, debe cambiarse en Neon.

No es necesario conectar automáticamente Neon con GitHub para que el backend lea la base de datos: la conexión la establece el servidor Node.js mediante `DATABASE_URL`.

## 8. Instalar dependencias y encender el servidor

Desde la carpeta raíz del repositorio:

```powershell
cd api
npm.cmd install
npm.cmd run check
npm.cmd start
```

El comando `npm.cmd start` inicia `server.js`. **No se ejecutará correctamente mientras falte `DATABASE_URL`**.

Si el servidor inicia, abrí:

- Interfaz: http://localhost:3000/
- Productos: http://localhost:3000/api/productos
- Clientes: http://localhost:3000/api/clientes
- Estado de conexión con Neon: http://localhost:3000/api/health

El endpoint `/api/health` verifica la conexión a PostgreSQL; **no garantiza que el motor de Prolog esté integrado o que la emisión funcione**.

Para detener el servidor desde la terminal, presioná `Ctrl + C`.

## 9. Ejecutar las pruebas de Prolog

Desde la carpeta raíz del repositorio:

```powershell
cd reglas
swipl -q -s pruebas.pl
```

La suite contiene **26 pruebas automáticas** relacionadas con tarifas, descuentos, kits, facturas, stock y reportes. Se ejecutó en el entorno local del grupo el **22 de noviembre de 2026** y las **26 pruebas pasaron**.

Esto valida los casos contemplados por la suite con los datos de ejemplo; no demuestra todavía la integración completa ni todas las operaciones de base de datos.

Para consultar los resultados de demostración de manera interactiva:

```powershell
swipl -q -s demo.pl
```

Dentro de Prolog, probá:

```prolog
ver_factura(2, Resultado).
ver_reportes(Resultado).
```

Para salir de Prolog, escribí `halt.`.

### Valores de control dados en la guía

| Factura | Subtotal | IVA | Total |
|---|---:|---:|---:|
| 2 | ₡26.400 | ₡174 | ₡26.574 |
| 3, cliente exonerado | — | ₡0 | ₡589.950 |

## 10. Rutas actuales de la API y pendientes

### Implementadas en el `server.js` entregado

| Método | Ruta | Función |
|---|---|---|
| GET | `/api/health` | Comprueba la conexión con Neon |
| GET | `/api/productos` | Lista productos |
| GET | `/api/clientes` | Lista clientes |
| GET | `/api/kits` | Lista kits y sus componentes |
| GET | `/api/facturas` | Lista encabezados de facturas |
| GET | `/api/facturas/:id/lineas` | Lista líneas de una factura |

### Pendientes de implementar/integrar

| Método | Ruta | Función |
|---|---|---|
| GET | `/api/facturas/:id` | Detalle completo de factura |
| POST | `/api/facturas` | Crear factura y líneas |
| POST | `/api/facturas/:id/emitir` | Validar, calcular, emitir y persistir con Prolog |
| GET | `/api/reportes` | Reportes de ventas e IVA |

**Incompatibilidad por resolver:** el `web/app.js` entregado llama a `POST /api/facturas/emitir`, pero esa ruta no existe en el servidor actual. Hay que acordar una sola ruta e implementar el flujo de creación/emisión sin duplicar las reglas de negocio fuera de Prolog.

## 11. Funciones del motor de reglas

Las reglas de `reglas/motor.pl` incluyen:

- IVA educativo por categoría: general 13 %, medicamento 2 %, canasta 1 %, exento 0 %.
- Kits y kits anidados con descuento del 5 % en el precio de sus componentes.
- Descuento por volumen: 5 % a partir de 10 unidades; 10 % a partir de 50.
- Descuento por nivel de cliente: oro 10 %, plata 5 %, regular 0 %; aplicado después del descuento por volumen.
- IVA 0 para clientes exonerados.
- Validaciones por cantidad, existencia del ítem, stock, ausencia de líneas y restricciones de crédito.
- Reportes calculados sobre las facturas válidas/emitidas según los datos cargados en Prolog.

Al emitir en Neon, el servidor deberá manejar de manera transaccional la inserción, actualización de estado, consecutivo y descuento de inventario, para no guardar operaciones a medias.

## 12. Estado actual del desarrollo

| Componente | Estado comprobado |
|---|---|
| Repositorio GitHub | Clonado y `git fetch origin` ejecutado sin errores |
| Node.js y npm | Instalados; dependencias de `api` instaladas |
| SWI-Prolog | Instalado; 26/26 pruebas aprobadas |
| Scripts SQL | Entregados; ejecución en Neon aún no verificada |
| Conexión API–Neon | Pendiente de configurar y probar |
| Integración API–Prolog | Pendiente |
| Emisión y descuento de stock desde la web | Pendiente |
| Reportes accesibles desde la web | Pendiente |
| Documentación y pruebas integrales | Pendientes de completar |

## 13. Trabajo colaborativo y entregables

Según la guía del curso, el equipo debe llevar Issues con historias de usuario, criterios de aceptación, ramas y Pull Requests revisados, historial de commits, tres historias nuevas propias del grupo, bitácora de IA (`BITACORA_IA.md`) y por lo menos dos retos adicionales del nivel 4. También se solicitan una colección de Postman de al menos **12 peticiones y 25 pruebas**, validaciones manuales y una presentación final.

Flujo recomendado para trabajar sin modificar directamente `main`:

```powershell
git status
git fetch origin
git switch -c nombre/descripcion-tarea
# Editar, probar y revisar cambios
git add .
git commit -m "Documenta configuración de Neon y ejecución local"
git push -u origin nombre/descripcion-tarea
```

Después se crea un **Pull Request** en GitHub para revisión del equipo. Antes de subir, revisar `git status` y asegurarse de no incluir `.env` ni credenciales.

## 14. Solución de problemas frecuentes

- **`npm.ps1` bloqueado en PowerShell:** usar `npm.cmd install` y `npm.cmd start`.
- **`swipl` no se reconoce:** instalar SWI-Prolog y agregar su carpeta `bin` al PATH; abrir una nueva terminal.
- **`cd reglas` no encuentra la carpeta:** primero entrar a la raíz del repositorio clonado, no a `C:\Users\...` sin el proyecto.
- **`Falta configurar DATABASE_URL`:** crear `api/.env` con una URL válida de Neon.
- **`/api/health` devuelve error:** revisar permisos, conexión y datos de Neon sin publicar la contraseña.
- **La página no emite facturas o no muestra reportes:** las rutas necesarias aún no están implementadas en el backend entregado.
- **Se duplican líneas de facturas de ejemplo:** no volver a ejecutar el SQL original sobre una base ya inicializada; coordinar cualquier corrección con el grupo.

---

**Universidad Fidélitas · SC-250 · Grupo 5**  
**Estado:** proyecto académico en integración y pruebas.
