# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django import forms

from fuzzyapp.database import fuzzyQuery


campos_ordenamiento = (
    ('', "---"),
    ('calificacion', "Calificación esperada"),
    ('preparacion', "Preparación previa"),
    ('dificultad', "Dificultad del curso")
)


direccion = (
    ('ASC', "Ascendente"),
    ('DESC', "Descendente")
)


def filtros_dptos():
    query = "SELECT id_unidad, nombre FROM opinion.unidad WHERE mostrar = 'Y'"
    columns = {
        "id_unidad": {"type": "integer"},
        "nombre": {"type": "string"}
    }
    result = []
    for row in fuzzyQuery(query, columns):
        result.append((row['id_unidad'], row['nombre']))
    return result


class FiltroMateriasForm(forms.Form):
    """
    Formulario para filtrar y ordenar las materias de opinión estudiantil.
    Permite elegir cuáles departamentos mostrar (basado en sus códigos)
    y hasta 3 campos sobre los cuales ordenar el resultado.
    """
    filtrar_dptos = forms.BooleanField(initial=False, required=False, label="¿Filtrar unidades?")
    dptos = forms.MultipleChoiceField(choices=filtros_dptos(), required=False, label="Unidades")
    orden1 = forms.ChoiceField(choices=campos_ordenamiento, required=False, label="Primer campo para ordenar")
    asc1 = forms.ChoiceField(choices=direccion, required=False, initial='DESC', label="Dirección")
    orden2 = forms.ChoiceField(choices=campos_ordenamiento, required=False, label="Segundo campo para ordenar")
    asc2 = forms.ChoiceField(choices=direccion, required=False, initial='DESC', label="Dirección")
    orden3 = forms.ChoiceField(choices=campos_ordenamiento, required=False, label="Tercer campo para ordenar")
    asc3 = forms.ChoiceField(choices=direccion, required=False, initial='DESC', label="Dirección")


class AgruparMateriasForm(forms.Form):
    campo = forms.ChoiceField(choices=campos_ordenamiento)
