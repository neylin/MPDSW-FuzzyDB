# -*- coding: utf-8 -*-
from __future__ import unicode_literals

"""
Este módulo provee algo parecido a un ORM sobre los objetos que nos
interesa representar en la base de datos difusa.

Se implementa haciendo uso de la capa de abstracción implementada en el
módulo fuzzyapp.database.

Estos NO son modelos de Django, es nuestra propia implementación de un
ORM. Sin embargo, está bastante inspirado en el API del ORM de Django,
de esta forma es más fácil de entender.
"""

from fuzzyapp.database import fuzzyQuery, fuzzyStatement


class MateriaQuerySet(object):
    """
    Obtiene de forma perezosa objetos Materia desde la base de datos.

    Inicialmente representa una consulta que no se ha ejecutado,
    la cual puede refinarse con los métodos filter() y order_by().

    Al iniciar el iterador, se ejecuta la consulta y se instancian
    objetos Materia utilizando el resultado.
    """

    def __init__(self):
        self.conditions = []
        self.order_columns = []

    def filter(self, **kwargs):
        """
        Agrega una condición para filtrar las materias en la base de datos
        Cada argumento que se le pase representa una condición.
        Actualmente soporta las siguientes condiciones:

        * <columna>__startswithany = <valores>: busca aquellos elementos cuya
            columna <columna> comience por algún string en <valores>
        * <columna>__exact = <valor>: aquellos elementos cuya columna <columna>
            sea exactamente igual a <valor>
        """
        for key, value in kwargs.items():
            # Va a explotar si pasna algo como a__b__c
            # Esto no es Django, a lo sumo soportamos algo como
            # <columna>__<operador>.
            column, operator = key.rsplit("__")
            if operator == "startswithany":
                condition_format = "(" + column + " LIKE '{value}%')"
                conditions = map(
                    lambda x: condition_format.format(value=x),
                    value
                )
                self.conditions.append("(" + " OR ".join(conditions) + ")")
            elif operator == "exact":
                self.conditions.append("({column} = '{value}')".format(
                    column=column,
                    value=value
                ))
            else:
                raise NotImplementedError(
                    "Can't filter using operator {op}".format(op=operator)
                )
        return self

    def id_unidad_in(self, ids):
        """
        Método para filtrar por aquellas materias que se relacionen con una unidad
        con id en 'ids'. Lo pongo aquí cara e tabla porque ya estoy harto de este
        proyecto y quiero salir del paso.
        """
        condition_format = "(ua.id_unidad = {value})"
        conditions = map(
            lambda x: condition_format.format(value=x),
            ids
        )
        self.conditions.append("(" + " OR ".join(conditions) + ")")

    def order_by(self, column, direction='DESC'):
        """
        Recibe una secuencia de columnas sobre las cuales ordenar el resultado.
        """
        self.order_columns += ((column, direction), )
        return self

    def _get_sql_query(self):
        """
        Construye la sentencia SQL necesaria para buscar todos los elementos
        que hacen match con las condiciones actuales de este QuerySet.
        """
        ################# TODO ##################
        # Esta es la consulta base para pedir materias
        # Estoy asumiendo la existencia de una tabla 'asignatura_fuzzy'
        # con las columnas para los difusos, el booleano 'stale' y
        # el codigo de la materia para poder hacerle join con asingatura.
        # También estoy asumiendo que todas las tablas están en un
        # schema llamado 'opinion'.
        # Hay que cambiarlo para que se ajuste a la realidad, si hace falta.
        sql = (
            "SELECT DISTINCT a.codigo, a.nombre, af.stale, af.calificacion, af.preparacion, af.dificultad "
            "FROM opinion.asignatura as a "
            "JOIN opinion.asignatura_fuzzy as af USING (codigo) "
            "LEFT OUTER JOIN opinion.unidad_asignatura as ua USING (codigo) "
        )
        ################# TODO #################

        # Agregar condiciones WHERE, si las hay
        if len(self.conditions) > 0:
            sql += " WHERE "
            sql += " AND ".join(self.conditions)

        # Agregar ORDER BY, si hace falta
        if len(self.order_columns) > 0:
            sql += " ORDER BY "
            sql += ','.join(map(lambda (col, dir): col + " " + dir, self.order_columns))

        ############# TODO ############
        # Aquí hay que crear el diccionario que se le pasa a fuzzyQuery
        # para decirle qué columnas debe extraer del resultado de la
        # consulta y cómo convertirlas a Python.
        # Entonces hay que ajustar esto a los nombres reales de las
        # columnas o que se yo. Hay que probar a ver que pasa.

        query_columns = {
            "codigo": {"type": "string"},
            "nombre": {"type": "string"},

            "stale": {"type": "boolean"},
            "calificacion": {"type": "fuzzy", "subtype_converter": int},
            "preparacion": {"type": "fuzzy", "subtype_converter": int},
            "dificultad": {"type": "fuzzy", "subtype_converter": int}
        }
        ########### TODO ##############3

        return sql, query_columns

    def __iter__(self):
        """
        Devuelve un generador que ejecuta la consulta del QuerySet y
        genera las instancias de Materia correspondientes al resultado.
        """
        sql, query_columns = self._get_sql_query()
        for row in fuzzyQuery(sql, query_columns):
            ############ TODO ############
            # Aquí hay que extraer los valores de row e instanciar
            # una Materia con ellos.
            # Luego de instanciar hay que ver si es necesario actualizar
            # los valores difusos. Si es necesario, entonces hacen las
            # consultas necesarias para calcularlo, instancian los
            # nuevos FuzzyExtension, se los asignan a los campos
            # correspondientes de la instancia de Materia, y hacen
            # .save() sobre la Materia.
            # Por ahora dejo instanciarlo sin argumentos para poder
            # ir probando otras cosas.

            # TODO: Instanciar Materia
            materia = Materia(
                codigo=row["codigo"],
                nombre=row["nombre"],
                calificacion=row["calificacion"],
                preparacion=row["preparacion"],
                dificultad=row["dificultad"],
            )
            materia._update_on_save = True

            # TODO: Chequear que los atributos fuzzy no estén stale.
            # (ese flag debería estar en el row)

            # TODO: Recalcular atrivutos difusos, si hace falta
            # TODO: Guardar los nuevos atributos difusos, si hace falta

            ############ TODO ############
            yield materia


class MateriaManager(object):
    """
    Permite obtener un MateriaQuerySet inicial con el cual comenzar
    a refinar una consulta.
    """

    def all(self):
        """
        Devuelve un QuerySet que hace match con todas las Materias en la
        base de datos.
        """
        return MateriaQuerySet()

    def get(self, **kwargs):
        """
        Devuelve un QuerySet de todas las Materias, filtrado según los
        kwargs que se le pasen.
        """
        return MateriaQuerySet().filter(**kwargs)


class Materia(object):
    """
    Representa un objeto de tipo Materia con todos sus campos.

    Puede instanciarse directamente para luego insertar nuevos objetos
    de este tipo en base de datos, o pueden ser generados por
    MateriaQuerySet para representar objetos existentes.

    Campos:
    codigo -- string
    nombre -- string
    calificacion -- un FuzzyExtension (la clase en database.py)
    preparacion -- un FuzzyExtension
    dificultad -- un FuzzyExtension

    FIXME: Otros campos que sean útiles?
    """

    objects = MateriaManager()

    def __init__(self, codigo, nombre, calificacion, preparacion, dificultad):
        ########## TODO ##########
        # Acomodar si hace falta

        self.codigo = codigo  # Clave primaria
        self.nombre = nombre
        self.calificacion = calificacion
        self.preparacion = preparacion
        self.dificultad = dificultad
        self._update_on_save = False

        ########### TODO #########

    def save(self):
        """
        Actualiza en base de datos los registros correspondientes a este
        objeto. Si no existe, se crea uno nuevo.
        """
        ############### TODO #############
        # Implementar esto
        # La idea es generar las consultas
        if self._update_on_save:
            pass
            # Hacer consultas UPDATE en vez de INSERT
        else:
            pass
            # Hacer consultas INSERT
            # Marcar esta instancia _update_on_save = True, pues luego
            # de un INSERT el próximo save() tiene que ser un update.
            # Realmente no creo que vayamos a usar esto, pero lo pongo
            # por completitud del API.
        ################ TODO ############
