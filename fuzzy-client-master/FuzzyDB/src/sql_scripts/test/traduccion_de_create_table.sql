CREATE TABLE esquema.nombre (
  col_name
    data_type
      [NOT NULL | NULL] -- NO CAMBIA
      [DEFAULT default_value] -- CAMBIA
      [AUTO_INCREMENT] -- INVALIDO
      [UNIQUE [KEY] | [PRIMARY] KEY] -- NO CAMBIA
      [COMMENT 'string'] -- NO CAMBIA
      [COLUMN_FORMAT {FIXED|DYNAMIC|DEFAULT}] -- NO CAMBIA
      [STORAGE {DISK|MEMORY|DEFAULT}] -- NO CAMBIA
      reference_definition -- Falta analizar pero creo que no cambia
)

-- Si el tipo de dato no es un dato basico y existe un dominio cuyo nombre es
-- igual que el tipo especificado, entonces el tipo se cambia a entero y se modifica
-- el reference_definition para agregar algunas restricciones. Entonces:

  data_type               -> INTEGER
  DEFAULT default_value   -> DEFAULT id de la etiqueta default_value en el dominio data_type

-- Se agrega al reference_definition

  FOREIGN KEY (col_name) REFERENCES information_schema_fuzzy.domains (domain_id)
  
-- Se ejecuta una consulta adicional

  INSERT INTO information_schema_fuzzy.columns
  VALUES (
    table_schema, 
    table_name,
    column_name,
    domain_id
  )