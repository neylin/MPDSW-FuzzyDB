README

Cómo montar y ejecutar la aplicación difusa para este proyecto.



Montar la BD en Postgres:

- Instalar Postgres.
- Configurarlo según el documento en el Drive. Al final debería existir
  una base de datos 'fuzzy', un usuario 'fuzzy' con clave 'fuzzy'.
  NOTA: hagan que el usuario 'fuzzy' sea superusuario en Postgres, o
  CREATE FUZZY DOMAIN no va a servir.



Compilar el parser y el traductor:

- Instalar el Java JDK.
- Ejecutar el script 'parser.sh' en la raíz del repositorio.
- En este punto pueden probar el traductor ejecutando el comando
  java -jar "nosequeverga/FuzzyDB.jar" que sale al final de la corrida
  de parser.sh.
  Si explota, no configuraron bien Postgres. Si funciona, pueden usar
  la consola del traductor tal cual lo mostré en la presentación para
  los profesores y así ven que tal.



Montar el gateway para conectar Django con el traductor en Java:

- Compilar y ejecutar el gateway:

    $ cd FuzzyGateway
    $ ant clean run

    Esto va a ejecutar un servidor corriendo en no-se-que-puerto.
    Déjenlo corriendo ahí, o Django no va a correr.
    Si explota, probablemente es porque no se pudo conectar a
    Postgres, o porque no compilaron el parser/traductor.



Montar el app en Django:

- Instalar el paquete python-virtualenv:

  # apt-get install python-virtualenv

- Crear el entorno virtual para python en alguna carpeta del universo.
  Recomiendo que sea la carpeta fuzzyapp dentro de este proyecto.

  $ cd alguna_carpeta_del_universo
  $ virtualenv env_fuzzyapp

- Activar el environment e instalar django y py4j:

  $ source env_fuzzyapp/bin/activate
  $ pip install -r requirements.txt

- Configurar la base de datos según la sección 'Base de datos' más adelante.

- Probar si se conecta al gateway:

  $ python manage.py shell

  Si los deja en una consola de Python sin errores, todo está de pinga.
  Si explotó, puede ser porque no activaron el environment (paso anterior)
  o no estaba corriendo el gateway (sección anterior)

- BONUS POINT:
  Usen el API fuzzyQuery y fuzzyStatement para hacer cosas en la BD
  difusa desde el shell de Django, por ejemplo:

  $ python manage.py shell
  $ >> from fuzzyapp.database import fuzzyQuery, fuzzyStatement
  $ >> fuzzyStatement("CREATE FUZZY DOMAIN bla AS .........")
  $ >> result = fuzzyQuery("SELECT * FROM personas", columns= .......)
  $ >> for i in result:
  $ >>     print i

  Con cada consulta que hagan desde Django podrán ver que la consola
  del gateway muestra las consultas que está ejecutando.

- Si llegaron hasta aquí todo funciona y tiene la BD cargada. Entonces solo falta calcular los
  atributos difusos de cada materia:

  $ python manage.py updatefuzzy

------------------

API para hacer consultas dentro de Python:

Revisen fuzzyapp/database.py, allí se aplica bien el API. En fuzzyapp/models.py pueden ver como
se trató de imitar el estilo del ORM de Django utilizando el api en database.py.

-------------------

Base de datos de prueba

Utilizamos la base de datos de encuesta de opinión estudiantil. De ella tomamos las siguientes
tablas, las cuales modificamos como sea para que cargaran en el traductor:

- asignatura
- unidad
- unidad_asignatura
- historial
- respuesta
- profesor_encuesta

Estas tablas la colocamos en un schema denominado 'opinion', la cual se debe hacer a mano en el
cliente de Postgres, pues JSqlParser no entiende 'CREATE SCHEMA' AFAIK.

- CREATE SCHEMA opinion

Luego creen la tabla para almacenar los atributos difusos adicionales para las materias:

CREATE TABLE asignatura_fuzzy (codigo text NOT NULL, calificacion fuzzyint NOT NULL, preparacion fuzzyint NOT NULL, dificultad fuzzyint NOT NULL, stale boolean NOT NULL);

Pueden mentalmente ignorar el campo 'stale', pero asegúrense de colocarlo.

Luego pueden cargar los datos utilizando los scripts de carga de EOE. Un feature interesante es
que el traductor lee las cosas desde entrada estándar, así que pueden hacer lo siguiente para
cargar las cosas rápidamente:

- Borren todo lo que esté en el header del archivo, de forma que la primera línea sea un INSERT.
- Coloquen como primera línea 'USE opinion;'
- Asegúrense que la última línea del archivo sea una línea en blanco. Esto capaz no hace falta,
  pero porsia.
- Carguen el script haciendo:

    $ java -jar FuzzyDB.jar < script.sql

El '<' le dice a bash que sustituya la entrada estándar por el archivo dado. Entonces lo que va
a pasar es que bash va a pasar todo el script al traductor y éste lo va a separar por los ';' y
saltos de línea y ejecutará cada consulta individualmente. Sin tiene más experiencia en bash podrían
encadenar varios archivos y cargar todo con un solo comando.
WARNING: asegúrense que el archivo tenga puros caracteres UTF-8 válidos, o Java revienta ungracefully.

----------------------

Aplicación actual

Todo está bien documentado. Hay algunos #### TODO ####, pero realmente los pueden ignorar.

Lo importante que se puede sacar a futuro de esta App/ es ver cómo conectar Django con una base
de datos accedida a través de una librería en Java. Con esto de base pueden empezar right away
con las aplicaciones de otros talleres directamente en Django sin tener que andar investigando
sobre py4j y tal.