from django.core.management.base import BaseCommand

from fuzzyapp.database import fuzzyQuery, fuzzyStatement


DATA_AGG_Q = """
SELECT codigo, id_pregunta, opcion, count(*) as count
FROM historial as h
JOIN profesor_encuesta as p USING (id_prof_encuesta)
JOIN respuesta as r USING (id_respuesta)
WHERE (id_pregunta = 24 OR id_pregunta = 30 OR id_pregunta = 22)
   AND (opcion = '1' OR opcion = '2' OR opcion = '3' OR opcion = '4' or opcion = '5')
GROUP BY codigo, opcion, id_pregunta
ORDER BY codigo, id_pregunta, opcion
"""

DATA_AGG_COLS = {
    "codigo": {"type": "string"},
    "id_pregunta": {"type": "integer"},
    "opcion": {"type": "string"},
    "count": {"type": "integer"}
}

# 22 = Preparacion
# 30 = Dificultad
# 24 = Calificacion


class Command(BaseCommand):
    args = ''

    def do_insert(self, codigo, data):
        if codigo is None:
            return

        for id_p in (22, 24, 30):
            op = data.get(id_p, {})
            counts = (op.get('1', 0), op.get('2', 0), op.get('3', 0), op.get('4', 0), op.get('5', 0))
            total = sum(counts)
            if total == 0:
                print("Skipped {}".format(codigo))
                return
            else:
                poss = map(lambda x: float(x) / total, counts)
                max_poss = max(poss)
                poss = map(lambda x: 0.0 if x == 0 else x / max_poss, poss)
                data[id_p] = "{{f {}/1, {}/2, {}/3, {}/4, {}/5 }}".format(*poss)

        query = "INSERT INTO asignatura_fuzzy VALUES ('{}', {}, {}, {}, 'f')".format(
            codigo, data[24], data[22], data[30]
        )
        fuzzyStatement(query)

    def handle(self, *args, **kwargs):
        fuzzyStatement("DELETE FROM asignatura_fuzzy")

        query = fuzzyQuery(DATA_AGG_Q, DATA_AGG_COLS)

        materia_actual = None
        data = {}
        for row in query:
            if row["codigo"] != materia_actual:
                self.do_insert(row["codigo"], data)
                materia_actual = row["codigo"]
                data = {}
            preg_dict = data.get(row["id_pregunta"], {})
            preg_dict[row["opcion"]] = row["count"]
            data[row["id_pregunta"]] = preg_dict

        self.do_insert(materia_actual, data)
