CREATE SCHEMA test_repuestos;

CREATE TABLE IF NOT EXISTS test_repuestos.concesionarios (
  nombre VARCHAR(64) NOT NULL PRIMARY KEY,
  origen INTEGER NOT NULL REFERENCES information_schema_fuzzy.labels (label_id) ON UPDATE CASCADE ON DELETE CASCADE,
  dueno VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS test_repuestos.repuestos (
  nombre VARCHAR(64) NOT NULL,
  direccion VARCHAR(64) NOT NULL,
  telefono VARCHAR(64) NOT NULL PRIMARY KEY,
  ciudad INTEGER NOT NULL REFERENCES information_schema_fuzzy.labels (label_id) ON UPDATE CASCADE ON DELETE CASCADE,
  concesionario VARCHAR(64) NOT NULL REFERENCES test_repuestos.concesionarios (nombre),
  UNIQUE (nombre, telefono)
);
