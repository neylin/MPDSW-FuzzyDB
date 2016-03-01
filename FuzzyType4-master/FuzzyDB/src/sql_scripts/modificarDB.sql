-- Eliminando Caracas y Charallave (no estan en ciudades)

DELETE FROM `information_schema_fuzzy`.`labels` WHERE `label_id`='5';
DELETE FROM `information_schema_fuzzy`.`labels` WHERE `label_id`='1';

-- insertando manualmente Acarigua y barinas 

INSERT INTO `information_schema_fuzzy`.`labels` (`label_id`, `domain_id`, `label_name`) VALUES ('1', '1', 'Acarigua');
INSERT INTO `information_schema_fuzzy`.`labels` (`label_id`, `domain_id`, `label_name`) VALUES ('5', '1', 'Barinas');

-- Insertando el resto de las tuplas en labels
INSERT INTO `information_schema_fuzzy`.`labels` (`label_id`, `domain_id`, `label_name`) VALUES ('10', '1', 'Bejuma'),('11', '1', 'Cabimas'),('12', '1', 'Cabudare'),('13', '1', 'Cagua'),('14', '1', 'Caracas'),('15', '1', 'Carora'),('16', '1', 'Coro'),('17', '1', 'Cumana'),('18', '1', 'Duaca'),('19', '1', 'El Junquito'),('20', '1', 'El Tigre'),('21', '1', 'El Vigia'),('22', '1', 'Fila de Mariches'),('23', '1', 'Guacara'),('24', '1', 'Guanare'),('25', '1', 'Guanta'),('26', '1', 'Guatire'),('27', '1', 'Higuerote'),('28', '1', 'Las Piedras'),('29', '1', 'Lecherias'),('30', '1', 'Mamporal'),('31', '1', 'Maracaibo'),('32', '1', 'Maturin'),('33', '1', 'Merida'),('34', '1', 'Montalban'),('35', '1', 'Motatan'),('36', '1', 'Naguanagua'),('37', '1', 'Porlamar'),('38', '1', 'Puerto Cabello'),('39', '1', 'Puerto la Cruz'),('40', '1', 'Puerto Ordaz'),('41', '1', 'Punto Fijo'),('42', '1', 'Rio Chico'),('43', '1', 'San Carlos'),('44', '1', 'San Cristobal'),('45', '1', 'San Felipe'),('46', '1', 'San Juan de los Morros'),('47', '1', 'Santa Rita'),('48', '1', 'Tinaquillo'),('49', '1', 'Tocuyito'),('50', '1', 'Trujillo'),('51', '1', 'Turmero'),('52', '1', 'Turumo'),('53', '1', 'Valera'),('54', '1', 'Valle de la Pascua'),('55', '1', 'Yaritagua');


-- Modificando tipo de la columna infoguia.ciudades.nombreciudad de VARCHAR(30) a INT. 
ALTER TABLE `infoguia`.`ciudades` CHANGE COLUMN `nombreciudad` `nombreciudad` INT NULL DEFAULT NULL;

-- Llenando la columna infoguia.ciudades.nombreciudad con los id's correspondientes a information_schema_fuzzy.labels.label_id
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='1' WHERE `codigociudad`='41';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='2' WHERE `codigociudad`='44';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='3' WHERE `codigociudad`='48';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='4' WHERE `codigociudad`='51';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='5' WHERE `codigociudad`='53';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='6' WHERE `codigociudad`='55';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='7' WHERE `codigociudad`='56';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='8' WHERE `codigociudad`='57';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='9' WHERE `codigociudad`='58';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='10' WHERE `codigociudad`='59';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='11' WHERE `codigociudad`='60';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='12' WHERE `codigociudad`='62';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='13' WHERE `codigociudad`='67';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='14' WHERE `codigociudad`='70';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='15' WHERE `codigociudad`='71';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='16' WHERE `codigociudad`='74';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='17' WHERE `codigociudad`='75';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='18' WHERE `codigociudad`='77';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='19' WHERE `codigociudad`='78';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='20' WHERE `codigociudad`='80';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='21' WHERE `codigociudad`='81';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='22' WHERE `codigociudad`='82';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='23' WHERE `codigociudad`='83';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='24' WHERE `codigociudad`='85';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='25' WHERE `codigociudad`='90';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='26' WHERE `codigociudad`='91';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='27' WHERE `codigociudad`='98';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='28' WHERE `codigociudad`='100';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='29' WHERE `codigociudad`='101';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='30' WHERE `codigociudad`='103';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='31' WHERE `codigociudad`='106';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='32' WHERE `codigociudad`='107';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='33' WHERE `codigociudad`='111';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='34' WHERE `codigociudad`='114';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='35' WHERE `codigociudad`='121';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='36' WHERE `codigociudad`='138';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='37' WHERE `codigociudad`='152';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='38' WHERE `codigociudad`='153';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='39' WHERE `codigociudad`='174';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='40' WHERE `codigociudad`='204';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='41' WHERE `codigociudad`='230';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='42' WHERE `codigociudad`='234';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='43' WHERE `codigociudad`='240';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='44' WHERE `codigociudad`='242';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='45' WHERE `codigociudad`='262';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='46' WHERE `codigociudad`='273';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='47' WHERE `codigociudad`='275';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='48' WHERE `codigociudad`='286';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='49' WHERE `codigociudad`='323';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='50' WHERE `codigociudad`='332';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='51' WHERE `codigociudad`='387';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='52' WHERE `codigociudad`='398';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='53' WHERE `codigociudad`='403';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='54' WHERE `codigociudad`='425';
UPDATE `infoguia`.`ciudades` SET `nombreciudad`='55' WHERE `codigociudad`='443';

-- Cambiado el nombre del dominio ciudad a ciudades

UPDATE `information_schema_fuzzy`.`domains` SET `domain_name`='ciudades' WHERE `domain_id`='1';

-- Agregada la columna nombreciudad a information_schema_fuzzy.columns
INSERT INTO `information_schema_fuzzy`.`columns` (`table_schema`, `table_name`, `column_name`, `domain_id`) VALUES ('infoguia', 'ciudades', 'nombreciudad', '1'); 










