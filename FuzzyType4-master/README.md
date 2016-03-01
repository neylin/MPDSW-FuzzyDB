# FuzzyDB

Prototipo para definir, almacenar y consultar una base de datos relacional con
atributos difusos. Se implementa como un traductor de SQL extendido con atributos
difusos, a SQL entendido por PostgreSQL.

### Dependencias

- PostgreSQL 9.3 or más reciente.
- Java 1.7 o mejor.
- JavaCC.
- Ant.
- Bash para ejecutar el script para compilar el cliente.
  Es posible compilarlo usando ant y moviendo algunos archivos a mano.

Todas las librerías (.jar) necesarios están incluidas en el repositorio.

### Modificar el código

El proyecto está dividido en 3 subproyectos:

- Parser
  Un fork de JSqlParser, extendido con atributos difusos.
  Su trabajo es generar el árbol de sintaxis y proveer las interfaces para manipularlo.
- FuzzyDB
  El cliente que realiza la traducción de las consultas y las ejecuta en PostgreSQL.
- App
  Una aplicación web escrita en Python/Django para demostrar algunas funcionalidades del proyecto.

En la carpeta doc/ se encuentra un manual explicando el funcionamiento interno de los subproyectos
y algunos consejos sobre cómo continuar el desarrollo.
