# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.views.generic import View
from django.shortcuts import render_to_response

from fuzzyapp.database import fuzzyQuery, convert_fuzzy
from fuzzyapp.models import Materia
from fuzzyapp.forms import FiltroMateriasForm, AgruparMateriasForm


class ListaMateriasView(View):

    def get(self, request):
        # Por default lista todas las materias y muestra un formulario
        # para filtrarlas y ordenarlas.
        materias = Materia.objects.all()
        form = FiltroMateriasForm()
        return render_to_response(
            "fuzzyapp/lista_materias.html",
            {"materias": list(materias), "form": form}
        )

    def post(self, request):
        # Filtra y ordena las materias si el formulario era válido
        # sino, por default hace los mismo que GET
        form = FiltroMateriasForm(request.POST)
        materias = Materia.objects.all()
        if form.is_valid():
            if form.cleaned_data["filtrar_dptos"]:
                materias.id_unidad_in(form.cleaned_data["dptos"])
            orden1 = form.cleaned_data["orden1"]
            orden2 = form.cleaned_data["orden2"]
            orden3 = form.cleaned_data["orden3"]
            if orden1 != '':
                materias.order_by(orden1, direction=form.cleaned_data["asc1"])
            if orden2 != '':
                materias.order_by(orden2, direction=form.cleaned_data["asc2"])
            if orden3 != '':
                materias.order_by(orden3, direction=form.cleaned_data["asc3"])

        return render_to_response(
            "fuzzyapp/lista_materias.html",
            {"materias": list(materias), "form": form}
        )


c = {
    "calificacion": "Calificación",
    "preparacion": "Preparación",
    "dificultad": "Dificultad"
}


class AgruparMateriasView(View):
    """
    En este view no estoy usando nuestro models, sino que uso directamente fuzzyQuery.
    La razón es que no hay manera fácil de integrar la funcionalidad de agregación a
    nuestro miniframework de materias, así que es más fácil hacerlo a mano.
    """

    def get(self, request):
        resultado = []
        form = AgruparMateriasForm()
        return render_to_response(
            "fuzzyapp/agrupar_materias.html",
            {"resultado": resultado, "form": form}
        )

    def post(self, request):
        form = AgruparMateriasForm(request.POST)
        resultado = []
        if form.is_valid():
            campo = form.cleaned_data["campo"]
            if campo == '':
                return render_to_response(
                    "fuzzyapp/agrupar_materias.html",
                    {"resultado": resultado, "form": form}
                )
            query = (
                "SELECT array_agg(a.codigo) as codigos, array_agg(a.nombre) as nombres, array_agg(af.{campo}) as campos "
                "FROM opinion.asignatura as a "
                "JOIN opinion.asignatura_fuzzy as af USING(codigo) "
                "GROUP BY af.{campo} "
            )

            query = query.format(campo=campo)
            columns = {
                "codigos": {"type": "array", "subtype_converter": unicode},
                "nombres": {"type": "array", "subtype_converter": unicode},
                "campos": {"type": "array", "subtype_converter": lambda x: convert_fuzzy(x.toString(), int)}
            }
            resultado = list(fuzzyQuery(query, columns))
            resultado = list(map(lambda x: zip(x['codigos'], x['nombres'], x['campos']), resultado))

        return render_to_response(
            "fuzzyapp/agrupar_materias.html",
            {"resultado": resultado, "form": form, "campo": c[campo]}
        )
