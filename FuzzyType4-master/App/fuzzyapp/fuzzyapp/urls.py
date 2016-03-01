# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.conf.urls import patterns, url

from fuzzyapp import views


urlpatterns = patterns('',
    url(r'^$', views.ListaMateriasView.as_view(), name='lista-materias'),
    url(r'^agrupar$', views.AgruparMateriasView.as_view(), name='lista-materias'),
)
