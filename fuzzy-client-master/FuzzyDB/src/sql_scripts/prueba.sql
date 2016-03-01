-- Ejemplo de creacion en insercion de elementos en un dominio
-- de tipo 2
CREATE FUZZY DOMAIN fuzzyint50 AS POSSIBILITY DISTRIBUTION ON INTEGER;
CREATE TABLE test(id integer, value fuzzyint50);
INSERT INTO test VALUES(1,{f (Null, Null, 10, 20)} ); 
INSERT INTO test VALUES(2,{f (10, 20, Null, Null)} ); 
INSERT INTO test VALUES(3,{f 0.25/13, 1.0/14, 0.75/15} ); 
INSERT INTO test VALUES(4,{f (0, 11, 12, 13)} ); 
INSERT INTO test VALUES(5,{f 0.75/13, 1.0/14, 0.25/15} ); 
INSERT INTO test VALUES(6,{f (14, 15, 19, 30)} ); 
INSERT INTO test VALUES(7,{f (1, 2, 5, 6)} );
INSERT INTO test VALUES(8,{f (3, 4, 7, 8)} );
-- Ejemplo de creacion en insercion de constantes y elementos en un dominio
-- de tipo 2
CREATE FUZZY DOMAIN fuzzyint AS POSSIBILITY DISTRIBUTION ON INTEGER;
CREATE TABLE persona(cedula int,nombre varchar,altura fuzzyint,peso fuzzyint);
-- Crear constantes para el atributo 'altura'
CREATE FUZZY CONSTANT bajo fuzzyint := {f (50, 55, 60, 65)};
CREATE FUZZY CONSTANT mediano fuzzyint := {f (60, 65, 70, 75)};
CREATE FUZZY CONSTANT alto fuzzyint := {f (70, 75, NULL, NULL)};
-- Crear constantes para el atributo 'peso'
CREATE FUZZY CONSTANT flaco fuzzyint := {f (NULL, NULL, 30, 55)};
CREATE FUZZY CONSTANT normal fuzzyint := {f (50, 55, 60, 75)};
CREATE FUZZY CONSTANT gordo fuzzyint := {f (70, 75, NULL, NULL)};
-- Aliasing de constantes
CREATE FUZZY CONSTANT muy_flaco fuzzyint := 'flaco';
-- ERROR: Constante no existe
CREATE FUZZY CONSTANT otra_constante fuzzyint := 'noexiste';
-- ERROR: Constante no es de tipo difuso
CREATE FUZZY CONSTANT otra_constante fuzzyint := 1;
-- Inserciones
INSERT INTO persona VALUES (1, 'persona1', 'bajo', 'gordo');
INSERT INTO persona VALUES (2, 'persona2', 'alto', 'flaco');
INSERT INTO persona VALUES (3, 'persona3', 'mediano', 'normal');
-- Actualizaciones
UPDATE persona SET peso = 'normal' WHERE cedula = 1;

-- Queries con constantes
select * from persona where altura = {f (50, 55, 60, 65)};
select * from persona where altura = 'bajo' and cedula = 1;
select * from persona where altura > 'bajo';
select * from persona where peso <= 'gordo';
select * from persona where peso > 'flaco';

-- Ejemplo de creacion en insercion de elementos en un dominio
-- de tipo 3 y de tipo 5
create fuzzy domain dominio_tipo3 as values('A', 'B', 'C', 'D') similarity{('A', 'B')/1.0, ('C', 'A')/0.5, ('C', 'D')/0.3};
CREATE FUZZY DOMAIN dominio_tipo5 AS POSSIBILITY DISTRIBUTION ON dominio_tipo3;
create table tabla_tipo5(a1 integer, a2 dominio_tipo5);
create table tabla_tipo3(att1 integer, att2 dominio_tipo3);
insert into tabla_tipo5 values (45322, {f 0.5/'A', 1.0/'B'});
insert into tabla_tipo3 values (12345, 'A');

-- Ejemplo de Pokemon con ambos dominios, tipo 2, 3 y 5.
CREATE FUZZY DOMAIN tipos_pokemon AS VALUES ('Planta','Fuego','Agua','Electrico') SIMILARITY {('Planta','Fuego')/0.4,('Planta','Agua')/0.7,('Fuego','Electrico')/0.8,('Agua','Electrico')/0.6};
CREATE FUZZY DOMAIN alturas_pokemon AS POSSIBILITY DISTRIBUTION ON INTEGER;
CREATE TABLE pokemon (Nombre VARCHAR NOT NULL PRIMARY KEY, Altura alturas_pokemon, tipo tipos_pokemon);
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Pichu', {f(5,10,15,20)}, 'Electrico');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Pikachu', {f(5,15,25,35)}, 'Electrico');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Charmander', {f(20,30,50,60)}, 'Fuego');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Squirtle', {f(10,15,35,50)}, 'Agua');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Bulbasaur', {f(10,20,30,40)}, 'Planta');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Blastoise', {f(90,120,150,180)}, 'Agua');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Venusaur', {f(80,100,140,160)}, 'Planta');
INSERT INTO pokemon(nombre, altura, tipo) VALUES ('Charizard', {f(100,130,170,200)}, 'Fuego');

-- ORDER BY, GROUP BY tipo2
SELECT nombre, altura FROM pokemon ORDER BY altura;
SELECT altura, count(*) FROM pokemon GROUP BY altura ORDER BY altura;

-- ORDER BY tipo3
SELECT nombre, tipo FROM pokemon ORDER BY tipo STARTING FROM 'Electrico';
SELECT nombre, tipo FROM pokemon ORDER BY tipo STARTING FROM 'Electrico', nombre;
SELECT nombre, tipo FROM pokemon ORDER BY SIMILARITY ON tipo STARTING FROM 'Electrico';
SELECT nombre, tipo FROM pokemon ORDER BY SIMILARITY ON tipo STARTING 'Electrico', nombre; -- No funciona por la gramatica
SELECT nombre, tipo FROM pokemon ORDER BY SIMILARITY ON tipo START 'Electrico';
SELECT nombre, tipo FROM pokemon ORDER BY SIMILARITY ON tipo START 'Electrico', nombre;

-- GROUP BY tipo3
SELECT nombre, count(*) FROM pokemon GROUP BY nombre;
SELECT tipo, count(*) FROM pokemon GROUP BY SIMILAR tipo;
SELECT tipo, count(*) FROM pokemon GROUP BY tipo;
SELECT tipo, nombre, count(*) FROM pokemon GROUP BY nombre, tipo;
SELECT tipo, nombre, count(*) FROM pokemon GROUP BY SIMILAR tipo, nombre;

SELECT tipo, count(*) FROM pokemon GROUP BY SIMILAR tipo ORDER BY tipo STARTING FROM 'Electrico';

create fuzzy domain Color as values('Rojo', 'Rosado', 'Amarillo') similarity{('Rojo', 'Rosado')/0.8, ('Rosado', 'Rojo')/0.8}
create table flores(Flor TEXT, Color Color);
-- insert into flores (Flor, Color) values ('Rosa', 'Rojo');
insert into flores (Flor, Color) values ('Rosa', 'Rojo'), ('Geranio', 'Rosado'), ('Cayena', 'Rosado'), ('Girasol', NULL); -- NO funciona lista de values: da error de sintaxis

-- Query Mixto
SELECT nombre, altura, tipo FROM pokemon ORDER BY tipo STARTING FROM 'Electrico';

-- Create fuzzy domain tipo 3:

---- Sintaxis alterna 1: ** OJO: ver lo que sucede y cuidar este caso en tipo 5 **
create table tabla_prueba_tipo3_1(id integer, etiquetas TEXT);
insert into tabla_prueba_tipo3_1 values (1, 'A'); insert into tabla_prueba_tipo3_1 values (2, 'B'); insert into tabla_prueba_tipo3_1 values (3, 'C');
create fuzzy domain dominio_prueba_tipo3_1 as values from tabla_prueba_tipo3_1.etiquetas; -- no funciona, null en deparser --> arreglado

---- Sintaxis alterna 2:
create table tabla_prueba_tipo3_2(id integer, etiquetas TEXT);
insert into tabla_prueba_tipo3_2 values (1, 'A'); insert into tabla_prueba_tipo3_2 values (2, 'B'); insert into tabla_prueba_tipo3_2 values (3, 'C');
create fuzzy domain dominio_prueba_tipo3_2 as select etiquetas from tabla_prueba_tipo3_2; -- implementado.

-- Alter Fuzzy Domain tipo 3:
create fuzzy domain dominio_prueba_alter_tipo3 as values('A', 'B', 'C', 'D');
alter fuzzy domain dominio_prueba_alter_tipo3 add similarity {('A', 'B')/1.0, ('C', 'A')/0.5, ('C', 'D')/0.3}
alter fuzzy domain dominio_prueba_alter_tipo3 drop values('A');
alter fuzzy domain dominio_prueba_alter_tipo3 add values('E');
alter fuzzy domain dominio_prueba_alter_tipo3 drop values('E');
alter fuzzy domain dominio_prueba_alter_tipo3 add values('E');
alter fuzzy domain dominio_prueba_alter_tipo3 add similarity {('D', 'E')/1.0} -- la inserta como derivada :o
alter fuzzy domain dominio_prueba_alter_tipo3 drop similarity {('D', 'E')}
