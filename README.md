# Organizador de Tareas Académicas

Proyecto desarrollado para el curso **SC-250 Paradigmas de Programación** de la Universidad Fidélitas.

El objetivo del proyecto es aplicar de forma práctica los paradigmas de **Programación Orientada a Objetos** y **programación funcional** mediante el desarrollo de un organizador de tareas académicas.

## Descripción

El sistema permite registrar materias y tareas académicas, asociar cada tarea con una materia, establecer prioridades y fechas de entrega, consultar las tareas registradas y actualizar su estado.

Además, permite realizar búsquedas, filtros y ordenamientos sobre las tareas, utilizando características funcionales de Java como `Stream`, `filter()` y `sorted()`.

El proyecto fue desarrollado en **Java** y utiliza **Swing** para la interfaz gráfica.

## Paradigmas aplicados

### Programación Orientada a Objetos

La Programación Orientada a Objetos se utiliza para representar los principales elementos del sistema.

Entre las clases principales se encuentran:

- `Materia`: representa una materia académica.
- `Tarea`: representa una tarea con información como título, descripción, fecha de entrega, prioridad, estado y materia.
- `Organizador`: administra las materias y tareas registradas en el sistema.
- `RepositorioDatos`: se encarga de guardar y recuperar la información.
- `SelectorFecha`: permite seleccionar fechas mediante una interfaz gráfica.

También se utilizan enumeraciones para controlar valores específicos:

- `EstadoTarea`
- `Prioridad`

### Programación funcional

La programación funcional se aplica principalmente en las operaciones realizadas sobre las listas de tareas.

La clase `OperacionesFuncionales` permite:

- Filtrar tareas por materia.
- Filtrar tareas por estado.
- Filtrar tareas por prioridad.
- Ordenar tareas por fecha de entrega.
- Ordenar tareas por prioridad.
- Combinar diferentes filtros.

Estas operaciones utilizan `Stream`, expresiones lambda y referencias a métodos, evitando modificar directamente la lista original.

## Funcionalidades

El sistema permite:

- Registrar materias.
- Registrar tareas académicas.
- Asociar una tarea con una materia.
- Editar tareas.
- Eliminar tareas.
- Cambiar el estado de una tarea.
- Definir prioridad alta, media o baja.
- Seleccionar una fecha de entrega.
- Buscar tareas por texto.
- Filtrar tareas por materia.
- Filtrar tareas por estado.
- Filtrar tareas por prioridad.
- Ordenar tareas por fecha.
- Ordenar tareas por prioridad.
- Consultar tareas pendientes.
- Visualizar avisos de tareas vencidas o próximas a vencer.
- Guardar automáticamente la información de forma local.

## Interfaz gráfica

La aplicación cuenta con una interfaz desarrollada con **Java Swing**.

Desde la pantalla principal se pueden visualizar:

- Cantidad de materias registradas.
- Total de tareas.
- Tareas pendientes.
- Tareas completadas.
- Próximas entregas.
- Tabla general de tareas.
- Opciones de búsqueda, filtrado y ordenamiento.

También dispone de opciones para registrar materias, crear nuevas tareas, editar información, cambiar estados y eliminar tareas.

## Almacenamiento de datos

La aplicación guarda la información localmente en un archivo de propiedades.

No requiere:

- Base de datos.
- Conexión a Internet.
- Servicios externos.
- Librerías adicionales para almacenamiento.

Los datos se recuperan automáticamente al volver a abrir la aplicación.

## Tecnologías utilizadas

- Java
- Java Swing
- Maven
- Programación Orientada a Objetos
- Programación funcional
- Streams
- Expresiones lambda
- Archivos `.properties`

## Estructura principal del proyecto

```text
ProyectoOrganizadorTareas
│
├── Materia.java
├── Tarea.java
├── Organizador.java
├── OperacionesFuncionales.java
├── EstadoTarea.java
├── Prioridad.java
├── RepositorioDatos.java
├── SelectorFecha.java
└── ProyectoOrganizadorTareas.java
```

## Ejecución

Para ejecutar el proyecto se necesita tener Java instalado.

La clase principal del sistema es:

```text
ProyectoOrganizadorTareas.java
```

Al ejecutar la aplicación se abrirá la interfaz gráfica del **Organizador de Tareas Académicas**.

La primera vez que se utiliza el sistema se recomienda:

1. Registrar una materia.
2. Crear una nueva tarea.
3. Seleccionar la materia correspondiente.
4. Indicar la prioridad.
5. Seleccionar la fecha de entrega.
6. Guardar la tarea.
7. Utilizar los filtros y opciones de ordenamiento para consultar las actividades.

## Gestión del proyecto

El desarrollo se organiza mediante el marco de trabajo **Scrum** y se utiliza **Jira** para gestionar las actividades del equipo.

El proyecto se divide en tres Sprints:

- **Sprint 1:** Análisis y Diseño.
- **Sprint 2:** Desarrollo e Integración.
- **Sprint 3:** Cierre y Entrega Final.

## Objetivo académico

Más allá de desarrollar un organizador de tareas, el proyecto busca demostrar cómo diferentes paradigmas de programación pueden utilizarse dentro de una misma solución.

La Programación Orientada a Objetos permite estructurar las entidades y responsabilidades principales del sistema, mientras que la programación funcional facilita el procesamiento, filtrado y ordenamiento de la información.

De esta forma, el proyecto permite observar de manera práctica cómo ambos paradigmas pueden complementarse dentro de una aplicación desarrollada en Java.
