-- forma de vaciar toda las tablas sin tener que borrar
DELETE FROM pokemon;

DELETE FROM mega_pokemon;

DELETE FROM habilidades;

-- forma de borrar toda la tabla
DROP TABLE pokemon;

DROP TABLE mega_pokemon;

DROP TABLE habilidades;

-- forma de borrar los dominios tipo 2
DROP FUZZY DOMAIN poderes_base_pokemon CASCADE;

DROP FUZZY DOMAIN ratios_de_captura_pokemon CASCADE;

-- para borrar los tipos 3 hay que hacer desde postgres drop-schema.sql y crear de nuevo el schema con create-schema.sql