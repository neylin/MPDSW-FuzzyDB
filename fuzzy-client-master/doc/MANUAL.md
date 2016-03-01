# Manual de desarrollo

## Introducción

Este proyecto está hecho fundamentalmente en Java, y hay una buena excusa: hacía falta un parser
de SQL medio decente que pudiera ser extendido, y esto era lo que había. Les prometo que si se
esfuerzan por meterse en el código existente las cuestiones van a facilitarse bastante. Empezar
de cero es definitivamente una muy mala idea, es mucho mejor ir refactorizando poco a poco a medida
que se desarrollen funcionalidades nuevas.

## JSqlParser

Este es un fork de JSqlParser, cuyo sitio oficial es http://jsqlparser.sourceforge.net/.

Actualmente no parece estar siendo mantenido, sin embargo, hay un fork en Github
(https://github.com/JSQLParser/JSqlParser) que si parece estar siendo mantenido. (01/06/2014)

#### Dependencias

- Java JDK
- JavaCC
- Ant

#### Compilación

Desde el directorio principal hacer:

    $ ant clean jar

#### Estructura interna

La definición de la gramática se encuentra en src/net/sf/jsqlparser/parser/JSqlParserCC.jj

Las clases en expression/, schema/ y statement/ contienen las clases que conforman el árbol
de sintaxis. La mejor manera de entender cómo encaja todo es leer la gramática e identificar
qué clases se utilizan para construir los árboles de las principales sentencias SQL.

Para recorrer el árbol de sintaxis generado, JSqlParser implementa el patrón visitante
(http://en.wikipedia.org/wiki/Visitor_pattern). Las principales interfaces Visitor que interesa
conocer son statement/StatementVisitor.java, y expression/ExpressionVisitor.java.

En el directorio util/deparser/ se encuentran una serie de clases que implementan un
Visitor para el árbol de sintaxis, cuyo resultado es la representación en String del árbol.

#### Modificación

Es necesario modificar este proyecto cuando sea necesario cambiar o agregar elementos de sintaxis
a SQL. Para ello es necesario modificar JSqlParserCC.jj para cambiar la gramática, y cambiar o
crear clases para el árbol de sintaxis.

Cuando se creen clases nuevas, especialmente aquellas que hereden de Expression o Statement, será
necesario alterar la interfaz en StatementVisitor.java o ExpressionVisitor.java para agregar
la nueva clase. Esto luego va hacer necesario actualizar cualquier implementación (dentro o fuera de
JSqlParser) de estos Visitor.
Por ejemplo, hará falta actualizar util/deparser/ para que tome en cuenta la nueva clase.

#### TODO / Wishlist

No tengo idea de cuál fue la versión base de este fork de JSqlParser, pero lo que si es seguro
es que no es la más reciente. Una mejora interesante sería actualizar este fork con los cambios
upstream.

Eso probablemente será complicado porque se han hecho bastantes cambios. Una mejora aún más
interesante entonces sería refactorizar este fork de forma que sea fácil mezclar continuamente
los cambios que se hagan upstream.

Esto es importante porque, hasta donde vi, los cambios upstream agregan elementos a la sintaxis
bastante importantes, tal como poder declarar restricciones FOREIGN KEY en la creación de tablas.

Otro aspecto que puede considerarse es hacer una nueva interfaz de Visitor que en vez de devolver
void devuelva un tipo parametrizado (<E>). Esto facilitaría en gran medida la modificación del
árbol porque al procesar un nodo, la función podría devolver un nuevo nodo con el cual reemplazar
el que se visitó. Actualmente para lograr el mismo efecto, es necesario mantener una variable
global en el Visitor, guardar el reemplaza en esta variable, y luego tener cuidado de reemplazar
el nodo que se visitó con lo que sea que esté en esta variable. Es horrible.


## FuzzyDB

Este es el cliente que recibe las consultas en SQL extendido, las traduce y luego
ejecuta en PostgreSQL. Puede usarse como un cliente via terminal, o como una librería de Java.

#### Dependencias

- PostgreSQL 9.3 o más reciente, configurado apropiadamente
- Java JDK
- QBossSqlParser (JSqlParser)
- Todos los .jar varios que se incluyen en libraries/, entre ellos:
  - El driver JDBC para Postgres
  - JUnit
  - JCommander

#### Compilación y ejecución

Si es posible, ejecutar el script parser.sh en la raíz del repositorio. Este script
compila JSqlParser y mueve el .jar generado a la carpeta libraries/ de FuzzyDB. Si no se puede,
es posible compilarlo con ant y mover el .jar manualmente.

En FuzzyDB/, ejecutar

    $ ant clean jar

La salida de ant dice el comando para ejecutar el proyecto. Por lo general será algo como:

    $ java -jar dist/FuzzyDB.jar

Es posible pasar el flag '-h' para mostrar las opciones que acepta el cliente. Por default
intentará conectarse a una base de datos llamada 'fuzzy', ubicada en 127.0.0.1,
con el usuario 'fuzzy', con contraseña 'fuzzy'.


#### Configuración de PostgreSQL

Para que el cliente funcione correctamente, se debe haber cargado en la base de datos el script
src/sql_scripts/create-schema.sql.

Este script crea un schema denominado 'information_schema_fuzzy', el cual contiene las tablas
y LOS COMPARADORES (I cannot stress this enough)

Debido a que se utiliza la sentencia CREATE OPERATOR CLASS al momento de definir atributos difusos
de tipo 2, es necesario que el usuario que realice la sentencia
CREATE FUZZY DOMAIN .. AS POSSIBILITY DISTRIBUTION, sea superusuario. PostgreSQL establece esta
restricción porque un OPERATOR CLASS mal definido podría hacer que el manejador aborte.

#### Estructura interna

Las clases fundamentales son fuzzy.Client y fuzzy.database.Connector.

Connector es la clase que abstrae la conexión a la base de datos y ofrece métodos para realizar
consultas. execute() recibe una sentencia SQL, lo traduce, lo ejecuta y
devuelve el resultado. También ofrece métodos executeRaw() que reciben un SQL y lo ejecutan
directamente en PostgreSQL sin traducir.

El método execute() es el que importa cuando se usa FuzzyDB a modo de librería, la familia
executeRaw() es útil principalmente para detalles internos de implementación de la traducción.

Client es la que contiene el main() y por lo tanto es lo que se ejecuta cuando se invoca FuzzyDB
como cliente via terminal. Se encarga de parsear los flags, instancia un Connector y luego entra
en un ciclo donde recibe consultas y se las pasa a execute() para traducir y ejecutar.

El proceso de traducción ocurre dentro de Connector en el método translate(), y ocurre en varias
fases.

La primera fase consiste en parsear el SQL y convertirlo en un AST (árbol de sintaxis) utilizando
JSqlParser.

La segunda fase consiste en realizar las traducciones para los atributos difusos tipo 3, lo
que corresponde al trabajo que realizó el equipo anterior.

La tercera fase consiste en realizar las traducciones para los atributos difusos tipo 2, lo que
corresponde al trabajo de este equipo.

Cada fase de traducción devuelve un AST modificado y una lista de operaciones adicionales a ejecutar.
Por ejemplo, al traducir un CREATE TABLE, se emiten operaciones adicionales para actualizar el
catálogo de columnas difusas.

Las operaciones heredan de la clase fuzzy.common.operations.Operation. Cada Operation debe
implementar un método execute().

Al finalizar la traducción, se posee un AST para la consulta traducida (el cual debe consistir
únicamente de SQL entendible por PostgreSQL), y una lista de operaciones adicionales a ejecutar.
El método execute() de Connector entonces invoca el método execute() de cada Operation en la lista,
luego se invoca el deparser de JSqlParser para reducir el AST a una consulta SQL en String, y se
ejecuta. Todo esto se realiza dentro del contexto de una transacción, de esta forma si alguna
operación falla, la base de datos vuelve a su estado original.

En conclusión, el proceso completo es el siguiente:

- Se instancia Connector, via el Cliente o via otro programa que lo haya invocado como librería.
- Se invoca execute() en una instancia de Connector, con una consulta SQL en forma de String.
- Se invoca translate() con la consulta en forma de String
    - Se invoca JSqlParser con la consulta, se obtiene un AST.
    - Se invoca fuzzy.type3.translator.StatementTranslator con el AST, se obtiene un AST traducido
      y una lista de Operation adicionales.
    - Se invoca fuzzy.type2.translator.StatementType2Translator con el AST, se obtiene un AST traducido
      y una lista de Operation adicionales.
- Se ejecutan cada una de las Operation generadas.
- Se ejecuta la consulta traducida.
- Se devuelve el resultado.

Cada fase de traducción se implementa como un Visitor utilizando las interfaces expuestas para ello
por JSqlParser.

Las clases en fuzzy.helpers cumplen funcionalidades sencillas como imprimir cosas. Sin embargo,
hay una clase muy importante denomina fuzzy.helpers.Memory, la cual funciona como una cache que
guarda qué columnas en qué tablas son difusas y de qué tipo, y provee métodos para determinar
si cierta columna es o no difusa.


#### Modificación y otros gotchas


En primer lugar, es necesario admitir que el código está en un estado terrible. Sin embargo, en mi
defensa, el código que se nos dió como resultado del trabajo anterior era aún más terrible. Fue
posible entender y refactorizar el código original poco a poco, y en este punto es muchísimo más
fácil continuar refactorizando poco a poco que empezar todo de cero.

Lo primero que debe hacerse es leer el código trazando la ejecución completa de una traducción.
Por ejemplo, tomen algo como 'SELECT * FROM tabla' y vean como fluye la ejecución desde que Client
invoca execute() en Connector, hasta que imprime el resultado. Gracias a este manual al menos ya
tendrán una idea de donde revisar, cuáles son las piezas y cómo se conectan entre sí.

Si lo que se quiere es agregar un nuevo tipo de dato difuso, se puede seguir el ejemplo de lo que
se hizo con los tipo 2. Se crea una nuevo visitor para el AST y se agrega como una fase adicional
de traducción. Probablemente será necesario crear nuevas clases para el AST, lo cual romperá los
traductores tipo 2 y 3; en ese caso bastaría con agregar las nuevas interfaces y hacer que
esos visitor ignoren las nuevas clases.

La fase de traducción de los tipo 3 además de traducir las cuestiones particulares de su tipo
difuso, también expande todos los '*' en un SELECT por las columnas de las tablas que corresponda.
Además también toma aquellas columnas que no hayan sido calificadas y las califica (es decir,
coloca la tabla a la que pertenecen). Esto es necesario porque luego hace falta poder determinar
si una columna es difusa o no, y para ello es necesario saber a qué tabla pertenece un 'Column'
arbitrario en el AST.


#### TODO / Wishlist

- Sería EXCELENTE si antes de hacer cualquier cosa se pudiera refactorizar la traducción de los
  tipo 3 para separarlo en dos fases: preprocesamiento y traducción.
  La fase de preprocesamiento consistiría únicamente de análisis de la consulta, calificar todas
  las columnas que aparezcan en cualquier expresión, expandir los *, anotar cada columna y/o tabla
  con información adicional (por ejemplo, si es difusa y de qué tipo).
  Esto sería bueno porque además así se pueden atrapar algunos bugs que hay por ahí. Por ejemplo,
  actualment eno es posible colocar una subconsulta en el FROM, y el código es tan feo que no tengo
  idea de cómo comenzar a arreglar eso.
- Si se implementa un Visitor con tipo de retorno parametrizado en JSqlParser, se puede refactorizar
  fuzzy.type2.translator.FuzzyType2ExpTranslator para que deje de usar variables 'globales'.
  (el this.replacement)
- fuzzy.helpers.Memory hay que o refactorizarlo, eliminarlo o lo que sea. Lo que está ahorita
  ha traído más bugs que otra cosa.


## App

La propia carpeta App/ tiene un README que explica en bastante detalle qué está pasando. Además
el propio código es bastante corto (unas ~700 líneas en Python), bien documentado,
y será muy familiar para alguien que conozca Django.


## Odds and ends

El código trata de seguir las convenciones usuales de Java. Sin embargo, básicamente a todo le
falta Javadocs. Además los comentarios son inconsistentes, algunas cosas fueron comentadas en inglés
(engrish más bien...) y otras están en español. Espero que con cada iteración de taller de
desarrollo esto vaya mejorando?
