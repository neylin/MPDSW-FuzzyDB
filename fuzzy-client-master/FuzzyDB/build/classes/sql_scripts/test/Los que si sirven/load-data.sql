CREATE SCHEMA test_repuestos;

INSERT INTO information_schema_fuzzy.domains(
    domain_id,
    table_schema,
    domain_name,
    domain_type,
    type3_domain_id
)       VALUES      (
    1,
    'test_repuestos',
    'ciudad',
    3,
    NULL)
;

INSERT INTO information_schema_fuzzy.labels(
    label_id,
    domain_id,
    label_name
)       VALUES
    (1,1,'Caracas'),
    (2,1,'Maracay'),
    (3,1,'Los Teques'),
    (4,1,'Valencia'),
    (5,1,'Charallave'),
    (6,1,'La Guaira'),
    (7,1,'Guarenas'),
    (8,1,'Barquisimeto'),
    (9,1,'Barcelona')
;

INSERT INTO information_schema_fuzzy.similarities(
    label1_id,
    label2_id,
    value,
    derivated
)       VALUES
    (1, 1, 1, true),
    (1,2,0.22,false),
    (1,3,0.81,false),
    (1,4,0.15,false),
    (1,5,0.52,false),
    (1,6,0.85,false),
    (1,7,0.57,false),
    (2, 1, 0.22, true),
    (2, 2, 1, true),
    (2,3,0.35,false),
    (2,4,0.20,false),
    (2,5,0.20,false),
    (3,1,0.18,false),
    (3, 2, 0.35, true),
    (3, 3, 1, true),
    (3, 4, 0.18, true),
    (3,5,0.65,false),
    (3,6,0.13,false),
    (3,7,0.14,false),
    (4, 1, 0.15, true),
    (4, 2, 0.48, true),
    (4, 3, 0.18, true),
    (4, 4, 1, true),
    (4,5,0.14,false),
    (5, 1,  0.52, true),
    (5, 2,  0.20, true),
    (5, 3,  0.65, true),
    (5, 4,  0.14, true),
    (5, 5, 1, true),
    (5,6,0.12,false),
    (5,7,0.10,false),
    (6, 1, 0.85, true),
    (6, 3, 0.13, true),
    (6, 5, 0.12, true),
    (6, 6, 1, true),
    (6,7,0.12,false),
    (7, 1, 0.57, true),
    (7, 3, 0.14, true),
    (7, 5, 0.10, true),
    (7, 6, 0.12, true),
    (7, 7, 1, true)
;

INSERT INTO information_schema_fuzzy.columns(
    table_schema,
    table_name,
    column_name,
    domain_id
)       VALUES
    ('test_repuestos','repuestos','ciudad', 1),
    ('test_repuestos','repuestos','origen', 1)
;

CREATE TABLE test_repuestos.concesionarios(
    nombre TEXT, 
    origen INTEGER, 
    dueno TEXT
);

INSERT INTO test_repuestos.concesionarios(
    nombre,
    origen,
    dueno
)       VALUES
    ('Mazda', 7, 'Andras'),
    ('Todos', 8, 'Vanessa'),
    ('Volkswagen', 9, 'Bishma'),
    ('Mazona', 1, 'Bishanva')
;

CREATE TABLE test_repuestos.repuestos(
    nombre TEXT, 
    direccion TEXT,
    telefono TEXT,
    ciudad INTEGER,
    concesionario TEXT
);

INSERT INTO test_repuestos.repuestos(
    nombre,
    direccion,
    telefono,
    ciudad,
    concesionario
)       VALUES
    ('Kansei Motor', 'Av. Andres Bello', '(0212)793.7606', 1, 'Mazda'),
    ('Reggio Cars', 'Las Acacias', '(0212)632.8325', 1, 'Todos'),
    ('Autoaccesorios Goma Cars', 'Km 27', '(0212)321.1832', 3, 'Todos'),
    ('Inversora y Promotora Don Jose', 'Simon Rodriguez', '(0251)445.9421', 8, 'Todos'),
    ('Repuestos Douglas 2007', 'Centro', '(0251)446.1321', 8, 'Volkswagen'),
    ('Repuestos Guatimotors', 'Calle Zamora Guatire', '(0212)344.5868', 7, 'Todos'),
    ('Diesel Tuy 2011', 'Centro', '(0239)414.2100', 5, 'Todos'),
    ('Direco CA', 'La Candelaria', '(0241)853.4334', 4, 'Todos'),
    ('Annarys', 'Calle Sucre', '(0281)276.8892', 9, 'Todos')
;

/* OJO: hay errores con case en las etiquetas:
    select nombre, ciudad from test_repuestos.repuestos order by ciudad starting from 'caracas'

    +--------------------------------+--------------+
    | nombre                         | ciudad       |
    +--------------------------------+--------------+
    | Kansei Motor                   | Caracas      |
    | Reggio Cars                    | Caracas      |
    | Autoaccesorios Goma Cars       | Los Teques   |
    | Inversora y Promotora Don Jose | Barquisimeto |
    | Repuestos Douglas 2007         | Barquisimeto |
    | Repuestos Guatimotors          | Guarenas     |
    | Diesel Tuy 2011                | Charallave   |
    | Direco CA                      | Valencia     |
    | Annarys                        | Barcelona    |
    +--------------------------------+--------------+
    9 rows in set

    da distinto a:

    select nombre, ciudad from test_repuestos.repuestos order by ciudad starting from 'Caracas'

    +--------------------------------+--------------+
    | nombre                         | ciudad       |
    +--------------------------------+--------------+
    | Kansei Motor                   | Caracas      |
    | Reggio Cars                    | Caracas      |
    | Autoaccesorios Goma Cars       | Los Teques   |
    | Repuestos Guatimotors          | Guarenas     |
    | Diesel Tuy 2011                | Charallave   |
    | Direco CA                      | Valencia     |
    | Annarys                        | Barcelona    |
    | Inversora y Promotora Don Jose | Barquisimeto |
    | Repuestos Douglas 2007         | Barquisimeto |
    +--------------------------------+--------------+
    9 rows in set

    ARREGLADO ! :)

    y ademas AMBOS dan distinto que en el informe. El ultimo, se acerca mas al
    informe salvo en Annarys que aparece de ultimo.

    Se puede utilizar ASC y DESC ?
*/