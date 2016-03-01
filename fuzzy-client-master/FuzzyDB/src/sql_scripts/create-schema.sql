CREATE SCHEMA IF NOT EXISTS information_schema_fuzzy; -- CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS information_schema_fuzzy.domains (
  domain_id SERIAL  PRIMARY KEY,
  table_schema      VARCHAR(64) NOT NULL,
  domain_name       VARCHAR(64) NOT NULL,
  domain_type       INTEGER NOT NULL,
  type3_domain_id   INTEGER,
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

CREATE TABLE IF NOT EXISTS information_schema_fuzzy.columns5 (
  table_schema  VARCHAR(64) NOT NULL,
  table_name    VARCHAR(64) NOT NULL,
  column_name   VARCHAR(64) NOT NULL,
  domain_id     INTEGER NOT NULL,

  PRIMARY KEY (table_schema, table_name, column_name),
  FOREIGN KEY (domain_id) REFERENCES information_schema_fuzzy.domains (domain_id)
    ON UPDATE CASCADE ON DELETE CASCADE
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

-- ################# TIPO 5 #################

CREATE OR REPLACE FUNCTION information_schema_fuzzy.array_unique (ANYARRAY) RETURNS ANYARRAY
LANGUAGE SQL
AS $body$
  SELECT ARRAY(
    SELECT DISTINCT $1[s.i]
    FROM generate_series(array_lower($1,1), array_upper($1,1)) AS s(i)
  );
$body$;

CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy5_tostring(elem anyelement)
RETURNS varchar AS $$
DECLARE
final varchar := '{';
size int;
BEGIN
IF elem is Null THEN
    final := 'NULL';
ELSE
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
    final = final || '}';
END IF;
return final;
END;
$$ LANGUAGE plpgsql;

-- GENERAL FUNCTION
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy5_f(elem anyelement, tag TEXT, dom TEXT) RETURNS REAL AS $$
DECLARE
de REAL;
nu REAL;
po REAL;
res REAL;
siz INTEGER;
e1 TEXT;
i INTEGER;
id1 INTEGER;
id2 INTEGER;
BEGIN
siz := array_length(elem.odd,1);

de := 0;
nu := 0;


--t3

id1 = (SELECT label_id FROM information_schema_fuzzy.domains, information_schema_fuzzy.labels WHERE
    information_schema_fuzzy.domains.domain_name = dom AND
    information_schema_fuzzy.labels.label_name = tag AND
    information_schema_fuzzy.labels.domain_id = information_schema_fuzzy.domains.type3_domain_id
    );

FOR i IN 1..siz LOOP
    e1 := elem.value[i];
    
    
    id2 = (SELECT label_id FROM information_schema_fuzzy.domains, information_schema_fuzzy.labels WHERE
        information_schema_fuzzy.domains.domain_name = dom AND
        information_schema_fuzzy.labels.label_name = e1 AND
        information_schema_fuzzy.labels.domain_id = information_schema_fuzzy.domains.type3_domain_id
        );
        
    po := (SELECT value FROM information_schema_fuzzy.similarities WHERE
                    label1_id = id1 AND label2_id = id2 LIMIT 1);
    
    --RAISE NOTICE 'row = % % %', id1, id2, po;
    nu := nu + elem.odd[i]*COALESCE(po,0.0);
    de := de + elem.odd[i];
END LOOP;


res := nu/de;

return res;

END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy5_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
DECLARE

BEGIN
return elem1.value = elem2.value and elem1.odd = elem2.odd;

END;
$$ LANGUAGE plpgsql;

-- ################# TIPO 2 #################

-- LO QUE COPIAMOS DE LO QUE SIRVE
-- Stored functions for comparing fuzzy type 2 values.
CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_lower(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
DECLARE
comp1 float := 0;
comp2 float := 0;
val float := 0;
size1 int := array_length(elem1.value,1);
size2 int := array_length(elem2.value,1);
BEGIN
IF elem1.type and elem2.type THEN
FOR j IN 0..size1 LOOP
  FOR i IN 0..size2 LOOP

    IF elem2.value[i] > elem1.value[j] THEN
        val := 0;
        IF elem2.odd[i] > elem1.odd[j] THEN
      val := elem1.odd[j];
        ELSE 
      val := elem2.odd[i];
        END IF;
    if val > comp1 THEN
                comp1 := val;
                END IF;
                EXIT WHEN comp1 = 1;
                END IF;
    END LOOP;
    EXIT WHEN comp1 = 1;
    END LOOP;

  size1 = size2;
  size2 = array_length(elem1.value,1);
FOR j IN 0..size1 LOOP
  FOR i IN 0..size2 LOOP

    IF elem1.value[i] > elem2.value[j] THEN
        val := 0;
        IF elem1.odd[i] > elem2.odd[j] THEN
      val := elem2.odd[j];
        ELSE 
      val := elem1.odd[i];
        END IF;
    if val > comp2 THEN
                comp2 := val;
                END IF;
                EXIT WHEN comp2 = 1;
                END IF;
    END LOOP;
    EXIT WHEN comp2 = 1;
    END LOOP;
    

  

  return comp1 > comp2;
ELSE IF (elem1.type = False) and (elem2.type = False) THEN


  IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
    IF (elem1.value[2] is not Null) THEN
      IF elem1.value[2] > elem2.value[3] THEN
        return False;
      END IF;
      return False;     
    END IF;
  END IF;

  IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
    IF elem1.value[3] < elem2.value[2] THEN
      return True;    
    END IF;
    IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
      return False;
    END IF;
    IF elem1.value[2] > elem2.value[3]  THEN
      return False;
    END IF;
    return False;
  END IF; 

  IF (elem1.value[3] is Null) THEN
    IF (elem2.value[3] is null) THEN
      return False;   
    END IF;
    IF elem1.value[2] > elem2.value[3] THEN
      return False;
    ELSE
      return False;
    END IF; 
  END IF;


END IF;
END IF;
IF (elem1.type = False) and (elem2.type = True) THEN
  IF elem1.value[2] is Null THEN
    return True;
  END IF;
  FOR i IN 1..size2 LOOP
    IF elem2.odd[i] = 1 THEN
      IF elem1.value[2] is not Null THEN
        IF elem1.value[2] <= elem2.value[i] THEN
          return True;
        END IF;
      END IF;
    END IF;
  END LOOP;
  return False;
END IF;

IF (elem1.type = True) and (elem2.type = False) THEN
  IF elem2.value[3] is Null THEN
    return True;
  END IF;
  FOR i IN 1..size1 LOOP
    IF elem1.odd[i] = 1 THEN
      IF elem2.value[2] is not Null THEN
        IF elem1.value[i] <= elem2.value[3] THEN
          return True;
        END IF;
      END IF;
    END IF;
  END LOOP;
  return False;
END IF;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_lower_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
DECLARE
comp1 float := 0;
comp2 float := 0;
val float := 0;
size1 int := array_length(elem1.value,1);
size2 int := array_length(elem2.value,1);
BEGIN
IF elem1.type and elem2.type THEN
FOR j IN 0..size1 LOOP
  FOR i IN 0..size2 LOOP

    IF elem2.value[i] > elem1.value[j] THEN
        val := 0;
        IF elem2.odd[i] > elem1.odd[j] THEN
      val := elem1.odd[j];
        ELSE 
      val := elem2.odd[i];
        END IF;
    if val > comp1 THEN
                comp1 := val;
                END IF;
                EXIT WHEN comp1 = 1;
                END IF;
    END LOOP;
    EXIT WHEN comp1 = 1;
    END LOOP;

  size1 = size2;
  size2 = array_length(elem1.value,1);
FOR j IN 0..size1 LOOP
  FOR i IN 0..size2 LOOP

    IF elem1.value[i] > elem2.value[j] THEN
        val := 0;
        IF elem1.odd[i] > elem2.odd[j] THEN
      val := elem2.odd[j];
        ELSE 
      val := elem1.odd[i];
        END IF;
    if val > comp2 THEN
                comp2 := val;
                END IF;
                EXIT WHEN comp2 = 1;
                END IF;
    END LOOP;
    EXIT WHEN comp2 = 1;
    END LOOP;
    

  

  return comp1 >= comp2;
ELSE IF (elem1.type = False) and (elem2.type = False) THEN

  IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
    IF (elem1.value[2] is not Null) THEN
      IF elem1.value[2] > elem2.value[3] THEN
        return False;
      END IF;
      return True;      
    END IF;
  END IF;

  IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
    IF elem1.value[3] < elem2.value[2] THEN
      return True;    
    END IF;
    IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
      return True;
    END IF;
    IF elem1.value[2] > elem2.value[3]  THEN
      return False;
    END IF;
    return True;
  END IF; 

  IF (elem1.value[3] is Null) THEN
    IF (elem2.value[3] is null) THEN
      return True;    
    END IF;
    IF elem1.value[2] > elem2.value[3] THEN
      return False;
    ELSE
      return True;
    END IF; 
  END IF;





END IF;
END IF;
IF (elem1.type = False) and (elem2.type = True) THEN
  IF elem1.value[2] is Null THEN
    return True;
  END IF;
  FOR i IN 1..size2 LOOP
    IF elem2.odd[i] = 1 THEN
      IF elem1.value[2] is not Null THEN
        IF elem1.value[2] <= elem2.value[i] THEN
          return True;
        END IF;
      END IF;
    END IF;
  END LOOP;
  return False;
END IF;

IF (elem1.type = True) and (elem2.type = False) THEN
  IF elem2.value[3] is Null THEN
    return True;
  END IF;
  FOR i IN 1..size1 LOOP
    IF elem1.odd[i] = 1 THEN
      IF elem2.value[2] is not Null THEN
        IF elem1.value[i] <= elem2.value[3] THEN
          return True;
        END IF;
      END IF;
    END IF;
  END LOOP;
  return False;
END IF;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
DECLARE
comp1 float := 0;
comp2 float := 0;
val float := 0;
size1 int := array_length(elem1.value,1);
size2 int := array_length(elem2.value,1);
bool1 boolean;
bool2 boolean;
BEGIN
IF elem1.type and elem2.type THEN
FOR j IN 0..size1 LOOP
  FOR i IN 0..size2 LOOP

    IF elem2.value[i] > elem1.value[j] THEN
        val := 0;
        IF elem2.odd[i] > elem1.odd[j] THEN
      val := elem1.odd[j];
        ELSE 
      val := elem2.odd[i];
        END IF;
    if val > comp1 THEN
                comp1 := val;
                END IF;
                EXIT WHEN comp1 = 1;
                END IF;
    END LOOP;
    EXIT WHEN comp1 = 1;
    END LOOP;

  size1 = size2;
  size2 = array_length(elem1.value,1);
FOR j IN 0..size1 LOOP
  FOR i IN 0..size2 LOOP

    IF elem1.value[i] > elem2.value[j] THEN
        val := 0;
        IF elem1.odd[i] > elem2.odd[j] THEN
      val := elem2.odd[j];
        ELSE 
      val := elem1.odd[i];
        END IF;
    if val > comp2 THEN
                comp2 := val;
                END IF;
                EXIT WHEN comp2 = 1;
                END IF;
    END LOOP;
    EXIT WHEN comp2 = 1;
    END LOOP;
    

  

  return comp2 = comp1;
ELSE IF (elem1.type = False) and (elem2.type = False) THEN
  IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
    IF (elem1.value[2] is not Null) THEN
      IF elem1.value[2] > elem2.value[3] THEN
        return False;
      END IF;
      return True;      
    END IF;
  END IF;

  IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
    IF elem1.value[3] < elem2.value[2] THEN
      return False;   
    END IF;
    IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
      return True;
    END IF;
    IF elem1.value[2] > elem2.value[3]  THEN
      return False;
    END IF;
    return True;
  END IF; 

  IF (elem1.value[3] is Null) THEN
    IF (elem2.value[3] is null) THEN
      return True;    
    END IF;
    IF elem1.value[2] > elem2.value[3] THEN
      return False;
    ELSE
      return True;
    END IF; 
  END IF;


  

  END IF;
END IF;
  
IF (elem1.type = False) and (elem2.type = True) THEN
  bool1 := False;
  bool2 := False;
  IF elem1.value[2] is Null THEN
    bool1 := True;
  END IF;
  IF elem1.value[3] is Null THEN
    bool2 := True;
  END IF;
  FOR i IN 1..size2 LOOP
    IF elem2.odd[i] = 1 THEN
      IF elem1.value[2] is not Null THEN
        IF elem1.value[2] <= elem2.value[i] THEN
          bool1 := True;
        END IF;
      END IF;
      IF elem1.value[3] is not Null THEN
        IF elem1.value[3] >= elem2.value[i] THEN
          bool2 := True;
        END IF;
      END IF;
    END IF;
    IF bool1 and bool2 THEN
      return True;
    END IF;
  END LOOP;
  return False;
END IF;

IF (elem1.type = True) and (elem2.type = False) THEN
  bool1 := False;
  bool2 := False;
  IF elem2.value[2] is Null THEN
    bool1 := True;
  END IF;
  IF elem2.value[3] is Null THEN
    bool2 := True;
  END IF;
  FOR i IN 1..size1 LOOP
    IF elem1.odd[i] = 1 THEN
      IF elem2.value[2] is not Null THEN
        IF elem2.value[2] <= elem1.value[i] THEN
          bool1 := True;
        END IF;
      END IF;
      IF elem2.value[3] is not Null THEN
        IF elem2.value[3] >= elem1.value[i] THEN
          bool2 := True;
        END IF;
      END IF;
    END IF;
    IF bool1 and bool2 THEN
      return True;
    END IF;
  END LOOP;
  return False;
END IF;

END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_greater(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
DECLARE
comp1 float := 0;
comp2 float := 0;
val float := 0;
size1 int := array_length(elem1.value,1);
size2 int := array_length(elem2.value,1);
BEGIN
IF elem1.type and elem2.type THEN
FOR j IN 0..size1 LOOP
FOR i IN 0..size2 LOOP

IF elem2.value[i] > elem1.value[j] THEN
val := 0;
IF elem2.odd[i] > elem1.odd[j] THEN
val := elem1.odd[j];
ELSE
val := elem2.odd[i];
END IF;
if val > comp1 THEN
                comp1 := val;
                END IF;
                EXIT WHEN comp1 = 1;
                END IF;
END LOOP;
EXIT WHEN comp1 = 1;
END LOOP;

size1 = size2;
size2 = array_length(elem1.value,1);
FOR j IN 0..size1 LOOP
FOR i IN 0..size2 LOOP

IF elem1.value[i] > elem2.value[j] THEN
val := 0;
IF elem1.odd[i] > elem2.odd[j] THEN
val := elem2.odd[j];
ELSE
val := elem1.odd[i];
END IF;
if val > comp2 THEN
                comp2 := val;
                END IF;
                EXIT WHEN comp2 = 1;
                END IF;
END LOOP;
EXIT WHEN comp2 = 1;
END LOOP;




return comp2 > comp1;
ELSE IF (elem1.type = False) and (elem2.type = False) THEN

  IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
    IF (elem1.value[2] is not Null) THEN
      IF elem1.value[2] > elem2.value[3] THEN
        return True;
      END IF;
      return False;     
    END IF;
  END IF;

  IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
    IF elem1.value[3] < elem2.value[2] THEN
      return False;   
    END IF;
    IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
      return False;
    END IF;
    IF elem1.value[2] > elem2.value[3]  THEN
      return True;
    END IF;
    return False;
  END IF; 

  IF (elem1.value[3] is Null) THEN
    IF (elem2.value[3] is null) THEN
      return False;   
    END IF;
    IF elem1.value[2] > elem2.value[3] THEN
      return True;
    ELSE
      return False;
    END IF; 
  END IF;








END IF;
END IF;
IF (elem1.type = False) and (elem2.type = True) THEN
  IF elem1.value[3] is Null THEN
    return True;
  END IF;
  FOR i IN 1..size2 LOOP
    IF elem2.odd[i] = 1 THEN
      IF elem1.value[3] is not Null THEN
        IF elem1.value[3] >= elem2.value[i] THEN
          return True;
        END IF;
      END IF;
    END IF;
  END LOOP;
  return False;
END IF;

IF (elem1.type = True) and (elem2.type = False) THEN
  IF elem2.value[2] is Null THEN
    return True;
  END IF;
  FOR i IN 1..size1 LOOP
    IF elem1.odd[i] = 1 THEN
      IF elem2.value[2] is not Null THEN
        IF elem2.value[2] <= elem1.value[i] THEN
          return True;
        END IF;
      END IF;
    END IF;
  END LOOP;
  return False;
END IF;

END;
$$ LANGUAGE plpgsql;




CREATE OR REPLACE FUNCTION information_schema_fuzzy.fuzzy2_greater_eq(elem1 anyelement, elem2 anyelement) RETURNS boolean AS $$
DECLARE
comp1 float := 0;
comp2 float := 0;
val float := 0;
size1 int := array_length(elem1.value,1);
size2 int := array_length(elem2.value,1);
BEGIN
IF elem1.type and elem2.type THEN
FOR j IN 0..size1 LOOP
  FOR i IN 0..size2 LOOP

    IF elem2.value[i] > elem1.value[j] THEN
        val := 0;
        IF elem2.odd[i] > elem1.odd[j] THEN
      val := elem1.odd[j];
        ELSE 
      val := elem2.odd[i];
        END IF;
    if val > comp1 THEN
                comp1 := val;
                END IF;
                EXIT WHEN comp1 = 1;
                END IF;
    END LOOP;
    EXIT WHEN comp1 = 1;
    END LOOP;

  size1 = size2;
  size2 = array_length(elem1.value,1);
FOR j IN 0..size1 LOOP
  FOR i IN 0..size2 LOOP

    IF elem1.value[i] > elem2.value[j] THEN
        val := 0;
        IF elem1.odd[i] > elem2.odd[j] THEN
      val := elem2.odd[j];
        ELSE 
      val := elem1.odd[i];
        END IF;
    if val > comp2 THEN
                comp2 := val;
                END IF;
                EXIT WHEN comp2 = 1;
                END IF;
    END LOOP;
    EXIT WHEN comp2 = 1;
    END LOOP;
    

  

  return comp2 >= comp1;
ELSE IF (elem1.type = False) and (elem2.type = False) THEN

  IF (elem1.value[3] is not Null) and (elem2.value[2] is Null) THEN
    IF (elem1.value[2] is not Null) THEN
      IF elem1.value[2] > elem2.value[3] THEN
        return True;
      END IF;
      return True;      
    END IF;
  END IF;

  IF (elem1.value[3] is not Null) and (elem2.value[2] is not Null) THEN
    IF elem1.value[3] < elem2.value[2] THEN
      return False;   
    END IF;
    IF (elem1.value[2] is Null) or (elem2.value[3] is Null ) THEN
      return True;
    END IF;
    IF elem1.value[2] > elem2.value[3]  THEN
      return True;
    END IF;
    return True;
  END IF; 

  IF (elem1.value[3] is Null) THEN
    IF (elem2.value[3] is null) THEN
      return True;    
    END IF;
    IF elem1.value[2] > elem2.value[3] THEN
      return True;
    ELSE
      return True;
    END IF; 
  END IF;



END IF;
END IF;
IF (elem1.type = False) and (elem2.type = True) THEN
  IF elem1.value[3] is Null THEN
    return True;
  END IF;
  FOR i IN 1..size2 LOOP
    IF elem2.odd[i] = 1 THEN
      IF elem1.value[3] is not Null THEN
        IF elem1.value[3] >= elem2.value[i] THEN
          return True;
        END IF;
      END IF;
    END IF;
  END LOOP;
  return False;
END IF;

IF (elem1.type = True) and (elem2.type = False) THEN
  IF elem2.value[2] is Null THEN
    return True;
  END IF;
  FOR i IN 1..size1 LOOP
    IF elem1.odd[i] = 1 THEN
      IF elem2.value[2] is not Null THEN
        IF elem2.value[2] <= elem1.value[i] THEN
          return True;
        END IF;
      END IF;
    END IF;
  END LOOP;
  return False;
END IF;

END;
$$ LANGUAGE plpgsql;


-- AQUI TERMINA LO QUE COPIAMOS


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