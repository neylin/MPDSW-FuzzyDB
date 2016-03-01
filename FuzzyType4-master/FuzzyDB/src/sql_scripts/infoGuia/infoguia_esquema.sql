SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `infoguia` ;
CREATE SCHEMA IF NOT EXISTS `infoguia` ;
USE `infoguia` ;

-- -----------------------------------------------------
-- Table `infoguia`.`secciones`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `infoguia`.`secciones` ;

CREATE  TABLE IF NOT EXISTS `infoguia`.`secciones` (
  `codigoseccion` TINYINT UNSIGNED NOT NULL DEFAULT '0' ,
  `nombreseccion` VARCHAR(80) NULL DEFAULT NULL ,
  PRIMARY KEY (`codigoseccion`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `infoguia`.`categorias`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `infoguia`.`categorias` ;

CREATE  TABLE IF NOT EXISTS `infoguia`.`categorias` (
  `codigoseccion` TINYINT UNSIGNED NOT NULL DEFAULT '0' ,
  `codigocategoria` SMALLINT UNSIGNED NOT NULL DEFAULT '0' ,
  `nombrecategoria` VARCHAR(90) NULL DEFAULT NULL ,
  `totalempresas` SMALLINT UNSIGNED NOT NULL DEFAULT '0' ,
  `secciones_codigoseccion` TINYINT UNSIGNED NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`secciones_codigoseccion`, `codigocategoria`) ,
  INDEX `fk_categorias_secciones1` (`secciones_codigoseccion` ASC) ,
  CONSTRAINT `fk_categorias_secciones1`
    FOREIGN KEY (`secciones_codigoseccion` )
    REFERENCES `infoguia`.`secciones` (`codigoseccion` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `infoguia`.`empresas`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `infoguia`.`empresas` ;

CREATE  TABLE IF NOT EXISTS `infoguia`.`empresas` (
  `codigoempresa` INT UNSIGNED NOT NULL DEFAULT '0' ,
  `rif` VARCHAR(12) NULL DEFAULT NULL ,
  `web` VARCHAR(80) NULL DEFAULT NULL ,
  `nombreempresa` VARCHAR(80) NULL DEFAULT NULL ,
  `descripcionempresa` TEXT NULL DEFAULT NULL ,
  `infoadicional` TEXT NULL DEFAULT NULL ,
  PRIMARY KEY (`codigoempresa`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `infoguia`.`ciudades`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `infoguia`.`ciudades` ;

CREATE  TABLE IF NOT EXISTS `infoguia`.`ciudades` (
  `codigociudad` SMALLINT UNSIGNED NOT NULL DEFAULT '0' ,
  `codigotlf` VARCHAR(4) NULL DEFAULT NULL ,
  `nombreciudad` VARCHAR(30) NULL DEFAULT NULL ,
  PRIMARY KEY (`codigociudad`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `infoguia`.`estados`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `infoguia`.`estados` ;

CREATE  TABLE IF NOT EXISTS `infoguia`.`estados` (
  `codigoestado` TINYINT UNSIGNED NOT NULL DEFAULT '0' ,
  `nombreestado` VARCHAR(25) NULL DEFAULT NULL ,
  PRIMARY KEY (`codigoestado`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `infoguia`.`ciudad_estado`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `infoguia`.`ciudad_estado` ;

CREATE  TABLE IF NOT EXISTS `infoguia`.`ciudad_estado` (
  `codigociudad` SMALLINT UNSIGNED NOT NULL DEFAULT '0' ,
  `capital` CHAR NOT NULL DEFAULT 'P' ,
  `codigoestado` TINYINT UNSIGNED NOT NULL DEFAULT '0' ,
  `estados_codigoestado` TINYINT UNSIGNED NOT NULL ,
  `ciudades_codigociudad` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`ciudades_codigociudad`, `estados_codigoestado`) ,
  INDEX `fk_ciudad_estado_ciudades1` (`ciudades_codigociudad` ASC) ,
  CONSTRAINT `fk_ciudad_estado_estados1`
    FOREIGN KEY (`estados_codigoestado` )
    REFERENCES `infoguia`.`estados` (`codigoestado` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ciudad_estado_ciudades1`
    FOREIGN KEY (`ciudades_codigociudad` )
    REFERENCES `infoguia`.`ciudades` (`codigociudad` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `infoguia`.`dataempresas`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `infoguia`.`dataempresas` ;

CREATE  TABLE IF NOT EXISTS `infoguia`.`dataempresas` (
  `codigoempresa` INT UNSIGNED NOT NULL DEFAULT '0' ,
  `codigosucursal` INT UNSIGNED NOT NULL DEFAULT '0' ,
  `codigoestado` TINYINT UNSIGNED NOT NULL DEFAULT '0' ,
  `codigociudad` SMALLINT UNSIGNED NOT NULL DEFAULT '0' ,
  `codigourbanizacion` SMALLINT UNSIGNED NOT NULL DEFAULT '0' ,
  `direccion` VARCHAR(140) NULL DEFAULT NULL ,
  `urbanizacion` VARCHAR(60) NULL DEFAULT NULL ,
  `telefono` VARCHAR(50) NULL DEFAULT NULL ,
  `fax` VARCHAR(25) NULL DEFAULT NULL ,
  `email` VARCHAR(80) NULL DEFAULT NULL ,
  `empresas_codigoempresa` INT UNSIGNED NOT NULL ,
  `estados_codigoestado` TINYINT UNSIGNED NOT NULL ,
  PRIMARY KEY (`empresas_codigoempresa`, `estados_codigoestado`) ,
  INDEX `fk_dataempresas_estados1` (`estados_codigoestado` ASC) ,
  CONSTRAINT `fk_dataempresas_empresas`
    FOREIGN KEY (`empresas_codigoempresa` )
    REFERENCES `infoguia`.`empresas` (`codigoempresa` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_dataempresas_estados1`
    FOREIGN KEY (`estados_codigoestado` )
    REFERENCES `infoguia`.`estados` (`codigoestado` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `infoguia`.`paginacioncategorias`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `infoguia`.`paginacioncategorias` ;

CREATE  TABLE IF NOT EXISTS `infoguia`.`paginacioncategorias` (
  `codigoempresa` INT UNSIGNED NOT NULL DEFAULT '0' ,
  `codigocategoria` INT UNSIGNED NOT NULL DEFAULT '0' ,
  `codigoseccion` TINYINT UNSIGNED NOT NULL DEFAULT '0' ,
  `codigoestado` TINYINT UNSIGNED NOT NULL DEFAULT '0' ,
  `codigociudad` SMALLINT UNSIGNED NOT NULL DEFAULT '0' ,
  `tipoaviso` VARCHAR(45) NOT NULL DEFAULT 'R' ,
  `nombreaviso` VARCHAR(12) NULL DEFAULT NULL ,
  `eslogan` VARCHAR(50) NULL DEFAULT NULL ,
  `numeropagina` INT UNSIGNED NOT NULL DEFAULT '0' ,
  `ordenpagina` TINYINT UNSIGNED NOT NULL DEFAULT '0' ,
  `ordenciudad` TINYINT UNSIGNED NOT NULL DEFAULT '0' ,
  `ordenestado` TINYINT UNSIGNED NOT NULL DEFAULT '0' ,
  `empresas_codigoempresa` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`empresas_codigoempresa`) ,
  CONSTRAINT `fk_paginacioncategorias_empresas1`
    FOREIGN KEY (`empresas_codigoempresa` )
    REFERENCES `infoguia`.`empresas` (`codigoempresa` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `infoguia`.`urbanizaciones`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `infoguia`.`urbanizaciones` ;

CREATE  TABLE IF NOT EXISTS `infoguia`.`urbanizaciones` (
  `codigourb` SMALLINT UNSIGNED NOT NULL DEFAULT '0' ,
  `codigociudad` SMALLINT UNSIGNED NOT NULL DEFAULT '0' ,
  `nombreurb` VARCHAR(40) NULL DEFAULT NULL ,
  `ciudades_codigociudad` SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`ciudades_codigociudad`, `codigourb`) ,
  INDEX `fk_urbanizaciones_ciudades1` (`ciudades_codigociudad` ASC) ,
  CONSTRAINT `fk_urbanizaciones_ciudades1`
    FOREIGN KEY (`ciudades_codigociudad` )
    REFERENCES `infoguia`.`ciudades` (`codigociudad` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
