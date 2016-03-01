CREATE SCHEMA IF NOT EXISTS information_schema_fuzzy; -- CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS information_schema_fuzzy.domains (
  domain_id SERIAL  PRIMARY KEY,
  table_schema      VARCHAR(64) NOT NULL,
  domain_name       VARCHAR(64) NOT NULL,

  UNIQUE (table_schema, domain_name)
);

CREATE TABLE IF NOT EXISTS information_schema_fuzzy.labels (
  label_id SERIAL   PRIMARY KEY,
  domain_id         INTEGER NOT NULL,
  label_name        VARCHAR(64) NOT NULL,

  UNIQUE (domain_id, label_name),
  FOREIGN KEY (domain_id) REFERENCES information_schema_fuzzy.domains(domain_id)
    ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS information_schema_fuzzy.similarities (
  label1_id     INTEGER NOT NULL,
  label2_id     INTEGER NOT NULL,
  value         DECIMAL(31,30) NOT NULL,
  derivated     BOOLEAN NOT NULL DEFAULT false,

  PRIMARY KEY (label1_id, label2_id),
  FOREIGN KEY (label1_id) REFERENCES information_schema_fuzzy.labels (label_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (label2_id) REFERENCES information_schema_fuzzy.labels (label_id)
    ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS information_schema_fuzzy.columns (
  table_schema  VARCHAR(64) NOT NULL,
  table_name    VARCHAR(64) NOT NULL,
  column_name   VARCHAR(64) NOT NULL,
  domain_id     INTEGER NOT NULL,

  PRIMARY KEY (table_schema, table_name, column_name),
  FOREIGN KEY (domain_id) REFERENCES information_schema_fuzzy.domains (domain_id)
    ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS information_schema_fuzzy.domains2 (
  id SERIAL     PRIMARY KEY,
  table_schema  VARCHAR(64) NOT NULL,
  domain_name   VARCHAR(64) NOT NULL,
  type          VARCHAR(64) NOT NULL,
  start         VARCHAR(64) NULL,
  finish        VARCHAR(64) NULL,

  UNIQUE (table_schema, domain_name)
);

CREATE TABLE IF NOT EXISTS information_schema_fuzzy.columns2 (
  table_schema  VARCHAR(64) NOT NULL,
  table_name    VARCHAR(64) NOT NULL,
  name          VARCHAR(64) NOT NULL,
  domain_id     INTEGER NOT NULL,

  PRIMARY KEY (table_schema, table_name, name),
  FOREIGN KEY (domain_id) REFERENCES information_schema_fuzzy.domains2 (id)
    ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS information_schema_fuzzy.current_orderings2 (
  ordering_id       INTEGER NOT NULL,
  ordering          INTEGER NOT NULL
);

INSERT INTO information_schema_fuzzy.current_orderings2 VALUES (1,3);

CREATE TABLE IF NOT EXISTS information_schema_fuzzy.constants2 (
  id SERIAL       PRIMARY KEY,
  constant_schema VARCHAR(64) NULL,
  domain_name     VARCHAR(64) NOT NULL,
  constant_name   VARCHAR(64) NOT NULL,
  value           VARCHAR(64) NOT NULL,
  fuzzy_type      VARCHAR(64) NOT NULL
);

-- toString() function
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_tostring(elem anyelement) RETURNS varchar AS $$
    DECLARE
        final varchar := '{';
        nulo varchar := 'Null';
        size int;
    BEGIN
        IF elem.type THEN
            size := array_length(elem.odd,1);
            FOR j in 1..size LOOP
                IF (j = 1) THEN
                    final := final || elem.odd[j];
                    final := final || '/';
                    final := final || elem.value[j];
                ELSE
                    final := final || ', ';
                    final := final || elem.odd[j];
                    final := final || '/';
                    final := final || elem.value[j];
                END IF;
            END LOOP;
        ELSE
            FOR j IN 1..4 LOOP
                IF (j = 1) THEN
                    IF elem.value[j] is Null THEN
                        final := final || nulo;
                    ELSE 
                        final := final || elem.value[j];
                    END IF;
                ELSE
                    final := final || ', ';
                    IF elem.value[j] is Null THEN
                        final := final || nulo;
                    ELSE 
                        final := final || elem.value[j];
                    END IF;
                END IF;
            END LOOP;
        END IF;

        final = final || '}';
        return final;
    END;
$$ LANGUAGE plpgsql;