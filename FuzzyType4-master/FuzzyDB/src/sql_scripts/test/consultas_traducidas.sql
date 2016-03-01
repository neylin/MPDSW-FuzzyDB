-- CONSULTA 1 TABLA SIN TRADUCIR

SELECT nombre, telefono, ciudad
FROM repuestos;

-- CONSULTA 1 TABLA TRADUCIDA

SELECT nombre, telefono, l_1.label_name AS ciudad
FROM repuestos AS t_1
LEFT JOIN information_schema_fuzzy.labels AS l_1 ON (t_1.ciudad = l_1.label_id);

-- CONSULTA 1 TABLA CON DISTINCT SIN TRADUCIR

SELECT DISTINCT ciudad
FROM repuestos;

-- CONSULTA 1 TABLA CON DISTINCT TRADUCIDA

SELECT DISTINCT l_1.label_name AS ciudad
FROM repuestos AS t_1
LEFT JOIN information_schema_fuzzy.labels AS l_1 ON (t_1.ciudad = l_1.label_id);

-- CONSULTA 1 TABLA CON WHERE SIN TRADUCIR

SELECT *
FROM repuestos
WHERE telefono LIKE '(0212)%' AND
  ciudad IN ('Caracas', 'Guarenas') OR
  ciudad = 'Charallave';

-- CONSULTA 1 TABLA CON WHERE TRADUCIDA

SELECT nombre, direccion, telefono, l_1.label_name AS ciudad, concesionario
FROM repuestos AS t_1
LEFT JOIN information_schema_fuzzy.labels AS l_1 ON (t_1.ciudad = l_1.label_id)
WHERE telefono LIKE '(0212)%' AND
  l_1.label_name IN ('Caracas', 'Guarenas') OR
  l_1.label_name = 'Charallave';

-- CONSULTA 2 TABLAS SIN TRADUCIR

SELECT r.nombre, ciudad, concesionarios.nombre, origen, dueno
FROM repuestos AS r JOIN concesionarios ON (concesionario = concesionarios.nombre);

-- CONSULTA 2 TABLAS TRADUCIDA

SELECT t_1.nombre, l_1.label_name AS ciudad, t_2.nombre, l_2.label_name AS origen, t_2.dueno
FROM (repuestos AS t_1
LEFT JOIN information_schema_fuzzy.labels AS l_1 ON (t_1.ciudad = l_1.label_id))
JOIN (concesionarios AS t_2
LEFT JOIN information_schema_fuzzy.labels AS l_2 ON (t_2.origen = l_2.label_id))
  ON (concesionario = t_2.nombre);

-- CONSULTA 2 TABLAS CON WHERE SIN TRADUCIR

SELECT r.nombre, ciudad, concesionarios.nombre, origen, dueno
FROM repuestos AS r JOIN concesionarios ON (concesionario = concesionarios.nombre)
WHERE telefono LIKE '(0212)%' AND
  ciudad IN ('Caracas', 'Guarenas') OR
  ciudad = 'Charallave' AND
  origen LIKE ('Bar%');

-- CONSULTA 2 TABLAS CON WHERE TRADUCIDA

SELECT t_1.nombre, l_1.label_name AS ciudad, t_2.nombre, l_2.label_name AS origen, t_2.dueno
FROM (repuestos AS t_1
LEFT JOIN information_schema_fuzzy.labels AS l_1 ON (t_1.ciudad = l_1.label_id))
JOIN (concesionarios AS t_2
LEFT JOIN information_schema_fuzzy.labels AS l_2 ON (t_2.origen = l_2.label_id))
  ON (concesionario = t_2.nombre)
WHERE telefono LIKE '(0212)%' AND
  l_1.label_name IN ('Caracas', 'Guarenas') OR
  l_1.label_name = 'Charallave' AND
  l_2.label_name LIKE ('Bar%');

-- CONSULTA 1 TABLA ORDER BY SIN TRADUCIR

SELECT nombre, telefono, ciudad
FROM repuestos
ORDER BY SIMILARITY ON ciudad STARTING FROM 'Caracas';

-- CONSULTA 1 TABLA ORDER BY TRADUCIDA

SELECT t_1.nombre, t_1.telefono, l_1.label_name AS ciudad, l_2.label_name, S1.value
FROM repuestos AS t_1
LEFT JOIN information_schema_fuzzy.labels AS l_1 ON (t_1.ciudad = l_1.label_id)
LEFT JOIN information_schema_fuzzy.domains AS d_1 ON (d_1.domain_name = 'ciudad')
LEFT JOIN information_schema_fuzzy.labels AS l_2 ON (l_2.label_name = 'Caracas' AND l_2.domain_id = d_1.domain_id)
LEFT JOIN information_schema_fuzzy.similarities S1 ON (S1.label1_id = l_2.label_id AND S1.label2_id = t_1.ciudad)
WHERE S1.value IS NOT NULL
ORDER BY S1.value DESC;

 