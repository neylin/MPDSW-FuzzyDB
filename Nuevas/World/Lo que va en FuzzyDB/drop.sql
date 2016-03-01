-- forma de vaciar toda las tablas sin tener que borrar
DELETE FROM city;

DELETE FROM country;

DELETE FROM countrylanguage;

-- forma de borrar toda la tabla
DROP TABLE city;

DROP TABLE country;

DROP TABLE countrylanguage;

-- forma de borrar los dominios tipo 2
DROP FUZZY DOMAIN poblacion CASCADE;

-- para borrar los tipos 3 hay que hacer desde postgres drop-schema.sql y crear de nuevo el schema con create-schema.sql