-- Base de datos Pokemon.
-- Miniproyecto de desarrollo oct-dic 2014
-- Integrantes: Luis Manuel Garcia 
--				Patricia Zambrano 
--				Andreina Marcano
--				Vanessa Balleste

-- Organizacion de este archivo:
-- 1- Creacion de dos (2) tipos difusos tipo 2.
-- 2- Creacion de tres (3) tipos difusos tipo 3 y sus relaciones de similitud.
-- 3- Creación de la tabla pokemon. 

-- El primer Dominio difuso tipo 2 "poderes_base_pokemon" nos muestra un estimado
-- del poder de un pokemon tomando como base sus estadisticas base iniciales
-- de ataque, defensa, ataque especial y defensa especial, formando trapecios
-- difusos con estos valores ordenados de menor a mayor.
CREATE FUZZY DOMAIN poderes_base_pokemon AS POSSIBILITY DISTRIBUTION ON INTEGER;

-- El segundo Dominio difuso tipo 2 "ratios_de_captura_pokemon" nos muestra un 
-- estimado de que tan facil/dificil es capturar a un pokemon segun la pokebola
-- que se tenga (pokeball, superball, ultraball) formando trapecios difusos 
-- uniformes de la siguiente forma: {f (cp/2, cp, cs, cu)} donde cp = capturado
-- con pokeball, cs = capturado por superball y cu = capturado por ultraball
CREATE FUZZY DOMAIN ratios_de_captura_pokemon AS POSSIBILITY DISTRIBUTION ON INTEGER;

-- EL primer Dominio difuso tipo 3 "tipos_pokemon" nos muestra un estimado
-- de una relacion de similitud entre los 18 tipos elementales a los que 
-- puede pertenecer un pokemon. 
-- RELACION DE SIMILITUD:
----------------------------------------------------------------------------------------------------
---------|plan|fueg|agua|elec|bich|norm|vola|luch|vene|tier|roca|acer|drag|fant|hiel|psiq|sini|hada|
----------------------------------------------------------------------------------------------------
--Tplanta| 1  | 0.1| 0.5| 0  | 0.7| 0  | 0  | 0  | 0.2| 0.9| 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  |
--Tfuego |    | 1  | 0  | 0.6| 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0.3| 0.8| 0  | 0.2| 0  | 0  | 0  |
--Tagua  |         | 1  | 0.8| 0  | 0  | 0  | 0  | 0  | 0.1| 0.3| 0  | 0  | 0  | 0.9| 0  | 0  | 0  |
--Telectr|              | 1  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0.3| 0  | 0  | 0  | 0  | 0  |
--Tbicho |                   | 1  | 0  | 0.6| 0  | 0.4| 0.3| 0  | 0  | 0  | 0  | 0  | 0.2| 0  | 0  | 
--Tnormal|                        | 1  | 0  | 0.4| 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  |
--Tvolado|                             | 1  | 0  | 0  | 0  | 0  | 0  | 0.9| 0.7| 0  | 0.2| 0  | 0.8|
--Tlucha |                                  | 1  | 0  | 0  | 0.5| 0.6| 0  | 0  | 0  | 0.3| 0  | 0  |
--Tveneno|                                       | 1  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0.7| 0  |
--Ttierra|                                            | 1  | 0.9| 0.1| 0  | 0  | 0  | 0  | 0  | 0  |
--Troca  |                                                 | 1  | 0  | 0  | 0  | 0  | 0  | 0  | 0  |
--Tacero |                                                      | 1  | 0  | 0  | 0  | 0.1| 0  | 0  |
--Tdragon|                                                           | 1  | 0  | 0  | 0  | 0.3| 0.5|
--Tfantas|                                                                | 1  | 0  | 0.5| 0.9| 0  |
--Thielo |                                                                     | 1  | 0  | 0  | 0  |
--Tpsiqui|                                                                          | 1  | 0.3| 0  |
--Tsinies|                                                                               | 1  | 0  |
--Thada  |                                                                                    | 1  |
----------------------------------------------------------------------------------------------------
CREATE FUZZY DOMAIN tipos_pokemon AS VALUES ('Tipo planta', 'Tipo fuego', 'Tipo agua', 'Tipo electrico', 'Tipo bicho', 'Tipo normal', 'Tipo volador', 'Tipo lucha', 'Tipo veneno', 'Tipo tierra', 'Tipo roca', 'Tipo acero', 'Tipo dragon', 'Tipo fantasma', 'Tipo hielo', 'Tipo psiquico', 'Tipo siniestro', 'Tipo hada') SIMILARITY {('Tipo planta','Tipo fuego')/0.1, ('Tipo planta','Tipo agua')/0.5, ('Tipo planta','Tipo bicho')/0.7, ('Tipo planta','Tipo veneno')/0.2, ('Tipo planta','Tipo tierra')/0.9, ('Tipo fuego','Tipo electrico')/0.6, ('Tipo fuego','Tipo acero')/0.3, ('Tipo fuego','Tipo dragon')/0.8, ('Tipo fuego','Tipo hielo')/0.2, ('Tipo agua','Tipo electrico')/0.8, ('Tipo agua','Tipo tierra')/0.1, ('Tipo agua','Tipo roca')/0.3, ('Tipo agua','Tipo hielo')/0.9, ('Tipo electrico','Tipo dragon')/0.3, ('Tipo bicho','Tipo volador')/0.6, ('Tipo bicho','Tipo veneno')/0.4, ('Tipo bicho','Tipo tierra')/0.3, ('Tipo bicho','Tipo psiquico')/0.2, ('Tipo normal','Tipo lucha')/0.4, ('Tipo volador','Tipo dragon')/0.9, ('Tipo volador','Tipo fantasma')/0.7, ('Tipo volador','Tipo psiquico')/0.2, ('Tipo volador','Tipo hada')/0.8, ('Tipo lucha','Tipo roca')/0.5, ('Tipo lucha','Tipo acero')/0.6, ('Tipo lucha','Tipo psiquico')/0.3, ('Tipo veneno','Tipo siniestro')/0.7, ('Tipo tierra','Tipo roca')/0.9, ('Tipo tierra','Tipo acero')/0.1, ('Tipo acero','Tipo psiquico')/0.1, ('Tipo dragon','Tipo siniestro')/0.3, ('Tipo dragon','Tipo hada')/0.5, ('Tipo fantasma','Tipo psiquico')/0.5, ('Tipo fantasma','Tipo siniestro')/0.9, ('Tipo psiquico','Tipo siniestro')/0.3};

-- EL segundo Dominio difuso tipo 3 "grupos_huevo_pokemon" nos muestra 
-- un estimado de una relacion de similitud entre los 15 grupos huevo del
-- videojuego pokemon. 
-- RELACION DE SIMILITUD:
-------------------------------------------------------------------------------------
---------|Ning|Ditt|Plan|Bich|Vola|Huma|Amor|Mine|Camp|Ag 1|Ag 2|Ag 3|Mons|Hada|Drag|
-------------------------------------------------------------------------------------
--Ninguno| 1  | 1  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  |
--Ditto  |    | 1  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  |
--Planta |         | 1  | 0.7| 0  | 0  | 0  | 0.5| 0.8| 0  | 0  | 0  | 0  | 0  | 0  |
--Bicho  |              | 1  | 0.6| 0  | 0  | 0  | 0.7| 0  | 0  | 0  | 0  | 0  | 0  |
--Volador|                   | 1  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0  | 0.8| 0.9| 
--Humanoi|                        | 1  | 0  | 0  | 0  | 0  | 0  | 0  | 0.2| 0  | 0  |
--Amorfo |                             | 1  | 0.3| 0  | 0  | 0  | 0  | 0.8| 0  | 0  |
--Mineral|                                  | 1  | 0.6| 0  | 0  | 0  | 0  | 0  | 0  |
--Campo  |                                       | 1  | 0  | 0  | 0  | 0  | 0  | 0  |
--Agua 1 |                                            | 1  | 0.9| 0.8| 0  | 0  | 0  |
--Agua 2 |                                                 | 1  | 0.9| 0  | 0  | 0  |
--Agua 3 |                                                      | 1  | 0  | 0  | 0  |
--Monstru|                                                           | 1  | 0  | 0.6|
--Hada   |                                                                | 1  | 0.5|
--Dragon |                                                                     | 1  |
-------------------------------------------------------------------------------------
CREATE FUZZY DOMAIN grupos_huevo_pokemon AS VALUES('Ninguno', 'Ditto', 'Planta', 'Bicho', 'Volador', 'Humanoide', 'Amorfo', 'Mineral', 'Campo', 'Agua 1', 'Agua 2', 'Agua 3', 'Monstruo', 'Hada', 'Dragon') SIMILARITY {('Ninguno','Ditto')/1, ('Planta','Bicho')/0.7, ('Planta','Mineral')/0.5, ('Planta','Campo')/0.8, ('Bicho','Volador')/0.6, ('Bicho','Campo')/0.7, ('Volador','Hada')/0.8, ('Volador','Dragon')/0.9, ('Amorfo','Mineral')/0.3, ('Amorfo','Monstruo')/0.8, ('Mineral','Campo')/0.6, ('Agua 1','Agua 2')/0.9, ('Agua 1','Agua 3')/0.8, ('Agua 2','Agua 3')/0.9, ('Monstruo','Dragon')/0.6, ('Hada','Dragon')/0.5};

-- EL tercer Dominio difuso tipo 3 "colores_pokemon" nos muestra 
-- un estimado de una relacion de similitud entre los 10 colores en
-- los que se pueden encontrar clasificados a los pokemon, segun
-- su apariencia. 
-- RELACION DE SIMILITUD:
------------------------------------------------------------
---------|Azul|Amar|Blan|Gris|Marr|Mora|Negr|Rojo|Rosa|Verd|
------------------------------------------------------------
--Azul   | 1  | 0  | 0  | 0  | 0  | 0.8| 0  | 0  | 0  | 0.8|
--Amarill|    | 1  | 0.2| 0  | 0  | 0  | 0  | 0  | 0  | 0.8|
--Blanco |         | 1  | 1  | 0  | 0  | 0  | 0  | 0.7| 0  |
--Gris   |              | 1  | 0  | 0  | 1  | 0  | 0  | 0  |
--Marron |                   | 1  | 0.3| 0.5| 0  | 0  | 0  |
--Morado |                        | 1  | 0  | 0.8| 0  | 0  |
--Negro  |                             | 1  | 0  | 0  | 0  |
--Rojo   |                                  | 1  | 0.7| 0  |
--Rosa   |                                       | 1  | 0  |
--Verde  |                                            | 1  |
------------------------------------------------------------
CREATE FUZZY DOMAIN colores_pokemon AS VALUES('Azul', 'Amarillo', 'Blanco', 'Gris', 'Marron', 'Morado', 'Negro', 'Rojo', 'Rosa', 'Verde') SIMILARITY {('Azul','Morado')/0.8, ('Azul','Verde')/0.8, ('Amarillo','Blanco')/0.2, ('Amarillo','Verde')/0.8, ('Blanco','Gris')/1, ('Blanco','Rosa')/0.7, ('Gris','Negro')/1, ('Marron','Morado')/0.3, ('Marron','Negro')/0.5, ('Morado','Rojo')/0.8, ('Rojo','Rosa')/0.7};


-- La Tabla pokemon esta basada en un famoso videojuego con este mismo
-- nombre. Los atributos que presenta esta tabla son los siguientes:
-- El "codigo_pokedex_nacional" hace referencia al numero del pokemon
-- desde el punto de vista de la pokedex nacional. El "nombre" se
-- refiere al nombre del pokemon. El "tipo_1" y el "tipo_2" hacen referencia
-- a los tipos de los pokemon, siendo el "tipo_1" el tipo primario del 
-- pokemon y el "tipo_2" su tipo secundario. El "grupo_huevo_1" y el 
-- "grupo_huevo_2" hacen referencia a los grupos en los que un pokemon
-- puede tener crias. El "poder_base_estimado" hace referencia al poder
-- estimado que puede tener un pokemon en cuanto a su ataque base, su ataque 
-- especial base, su defensa base y su defensa especial base. La "region_origen"
-- se refiere a la generacion en la cual aparecio por primera vez el pokemon.
-- El "ratio_de_captura" es que tan facil/dificil es el pokemon de capturar 
-- segun las 4 pokebolas principales en el videojuego, y por ultimo el "color"
-- hace referencia a el color primario por el que esta clasificado el pokemon. 
-- y por ultimo las "habilidad_1", "habilidad_2" y "habilidad_oculta"
-- se refieren a las habilidades que puede tener un pokémon. 
CREATE TABLE pokemon (codigo_pokedex_nacional varchar not null primary key, nombre varchar not null, tipo_1 tipos_pokemon not null, tipo_2 tipos_pokemon, grupo_huevo_1 grupos_huevo_pokemon not null, grupo_huevo_2 grupos_huevo_pokemon, poder_base_estimado poderes_base_pokemon not null, region_origen varchar not null, ratio_de_captura ratios_de_captura_pokemon not null, color colores_pokemon not null, habilidad_1 varchar not null, habilidad_2 varchar, habilidad_oculta varchar);

-- La primera es la tabla funcional por el momento, pero la siguiente
-- es la tabla correcta:
--CREATE TABLE pokemon (
--	  codigo_pokedex_nacional varchar(5) not null primary key, 
--	  nombre varchar(20) unique not null, 
--	  tipo_1 tipos_pokemon not null, 
--	  tipo_2 tipos_pokemon, 
--	  grupo_huevo_1 grupos_huevo_pokemon not null, 
--	  grupo_huevo_2 grupos_huevo_pokemon, 
--	  poder_base_estimado poderes_base_pokemon not null, 
--	  region_origen varchar(7) not null 
--		  CHECK(region_origen IN ('Kanto','Johto','Hoenn','Sinnoh','Teselia','Kalos')), 
--	  ratio_de_captura ratios_de_captura_pokemon not null, 
--	  color colores_pokemon not null,
--	  habilidad_1 varchar(30) not null references habilidades(nombre),
--	  habilidad_2 varchar(30) references habilidades(nombre),
--	  habilidad_oculta varchar(30) references habilidades(nombre)
--  	  	
--);

-- La Tabla mega-pokemon esta basada en las formas mega evolucionadas de
-- algunos pokemon, que pueden hacerse con este proceso para ser mas
-- fuertes. Los atributos que presenta esta tabla son los siguientes:
-- El "codigo_pokedex_nacional" hace referencia al numero del pokemon (no mega)
-- desde el punto de vista de la pokedex nacional. El "nombre" se
-- refiere al nombre del mega-pokemon. El "tipo_1" y el "tipo_2" hacen referencia
-- a los tipos de los mega-pokemon, siendo el "tipo_1" el tipo primario del 
-- mega-pokemon y el "tipo_2" su tipo secundario. El "poder_base_estimado" 
-- hace referencia al poder estimado que puede tener un mega-pokemon en cuanto a su ataque base,
-- su ataque especial base, su defensa base y su defensa especial base. La "region_origen"
-- se refiere a la generacion en la cual aparecio por primera vez el pokemon (no mega).
-- el "color" hace referencia a el color primario por el que esta clasificado el 
-- mega-pokemon y por ultimo la "habilidad" se refiere a la habilidad que tiene el
-- mega-pokemon. 
CREATE TABLE mega_pokemon (codigo_pokedex_nacional varchar not null, nombre varchar not null primary key, tipo_1 tipos_pokemon not null, tipo_2 tipos_pokemon, poder_base_estimado poderes_base_pokemon not null, region_origen varchar not null, color colores_pokemon not null, habilidad varchar not null);

-- La primera es la tabla funcional por el momento, pero la siguiente
-- es la tabla correcta:
--CREATE TABLE pokemon (
--	  codigo_pokedex_nacional varchar(3) not null, 
--	  nombre varchar(20) unique not null, 
--	  tipo_1 tipos_pokemon not null, 
--	  tipo_2 tipos_pokemon, 
--	  poder_base_estimado poderes_base_pokemon not null, 
--	  region_origen varchar(7) not null 
--		  CHECK(region_origen IN ('Kanto','Johto','Hoenn','Sinnoh','Teselia','Kalos')), 
--	  color colores_pokemon not null,
--        habilidad varchar not null references habilidades(nombre),
--        primary key(codigo_pokedex_nacional,nombre)
--);


-- La Tabla habilidades esta basada en las habilidades que puede tener un
-- pokemon, que los ayuda en batallas o fuera de las mismas, Una habilidad
-- es inherente a un pokémon, el nace con ella y no se le puede cambiar.
-- Los atributos que presenta esta tabla son los siguientes:
-- El "nombre" hace referencia al nombre de la habilidad, y el "efecto"
-- hace referencia a el efecto que tiene la habilidad.
CREATE TABLE habilidades (nombre varchar not null primary key, efecto varchar not null );

-- La primera es la tabla funcional por el momento, pero la siguiente
-- es la tabla correcta:
--CREATE TABLE habilidades (
--	nombre varchar(30) not null primary key, 
--	efecto varchar not null 
--);


-- Este es un tercer modelo de la base de datos de Pokemon.
-- Posibles mejoras:
----- 1- Agregar a la tabla pokemon un atributo tipo 2 por extension.
----- 2- Agregar una tabla movimientos(o ataques) que contenga todos los ataques que 
----- pueden aprender los pokemon, y por ende agregar una tabla aprende que tenga 
----- dos claves foraneas: una a pokemon y otra a movimientos(o ataques) para 
----- almacenar los ataques que puede aprender un pokemon en particular.


