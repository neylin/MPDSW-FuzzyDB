CREATE DATABASE  IF NOT EXISTS infoguia /*!40100 DEFAULT CHARACTER SET latin1 */;
USE infoguia;
-- MySQL dump 10.13  Distrib 5.5.16, for Win32 (x86)
--
-- Host: localhost    Database: infoguia
-- ------------------------------------------------------
-- Server version	5.5.17-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table urbanizaciones
--

DROP TABLE IF EXISTS urbanizaciones;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE urbanizaciones (
  codigourb smallint(5) unsigned NOT NULL,
  codigociudad smallint(5) unsigned NOT NULL,
  nombreurb varchar(40) DEFAULT NULL,
  PRIMARY KEY (codigourb)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table urbanizaciones
--

LOCK TABLES urbanizaciones WRITE;
/*!40000 ALTER TABLE urbanizaciones DISABLE KEYS */;
INSERT INTO urbanizaciones VALUES (1,41,'El Llanito'),(4,41,'Los Chaguaramos'),(5,41,'Petare'),(7,41,'Palo Verde'),(8,41,'La Urbina'),(10,41,'El Marqués'),(11,41,'Campo Rico'),(12,41,'Horizonte'),(13,41,'Boleíta Norte'),(14,41,'Boleíta Sur'),(16,41,'Montecristo'),(17,41,'Los Chorros'),(18,41,'Los Dos Caminos'),(19,41,'Sebucán'),(20,41,'Santa Eduvigis'),(21,41,'Los Palos Grandes'),(22,41,'Buena Vista'),(24,41,'Los Cortijos de Lourdes'),(27,41,'Macaracuay'),(28,41,'Caurimare'),(29,41,'El Cafetal'),(34,41,'Los Ruices'),(36,41,'Campo Claro'),(37,41,'La Carlota'),(38,41,'Santa Cecilia'),(41,41,'Chuao'),(44,41,'San Román'),(45,41,'Santa Rosa de Lima'),(46,41,'Santa Marta'),(47,41,'Santa Sofía'),(50,41,'La Floresta'),(52,41,'Bello Campo'),(54,41,'El Rosal'),(56,41,'Chacaíto'),(57,41,'Las Mercedes'),(58,41,'Valle Arriba'),(59,41,'Los Naranjos-Cafetal'),(64,41,'La Castellana'),(69,41,'Campo Alegre'),(70,41,'Country Club'),(71,41,'El Bosque'),(72,41,'Las Delicias-Urb.'),(73,41,'Sabana Grande'),(74,41,'Bello Monte'),(75,41,'San Antonio'),(79,41,'Los Cedros'),(80,41,'La Campiña'),(81,41,'Chapellín'),(82,41,'La Florida'),(83,41,'Los Caobos'),(84,41,'Plaza Venezuela'),(86,41,'La Colina'),(87,41,'Las Palmas'),(90,41,'Pedro Camejo'),(91,41,'Sarría'),(92,41,'Guaicaipuro'),(93,41,'Santa Rosa'),(94,41,'San Bernardino'),(95,41,'Cotiza'),(96,41,'El Retiro'),(97,41,'San José'),(98,41,'Altagracia'),(99,41,'La Candelaria'),(100,41,'Quebrada Honda'),(101,41,'La Pastora'),(103,41,'Manicomio'),(104,41,'Ruperto Lugo'),(105,41,'El Silencio'),(106,41,'Santa Teresa'),(107,41,'Santa Rosalía'),(108,41,'Quinta Crespo'),(109,41,'Monte Piedad'),(111,41,'Los Flores de Catia'),(112,41,'Propatria'),(113,41,'La Silsa'),(114,41,'Casalta'),(115,41,'Los Magallanes'),(116,41,'San Juan'),(117,41,'San Martín'),(119,41,'Puente Hierro'),(120,41,'San Agustín del Norte'),(121,41,'San Agustín del Sur'),(122,41,'Parque Central'),(123,41,'Montalbán'),(124,41,'La Vega'),(125,41,'El Paraíso'),(127,41,'Artigas'),(129,41,'Vista Alegre'),(130,41,'Bella Vista'),(132,41,'La Yaguara'),(134,41,'El Algodonal'),(135,41,'Carapa'),(136,41,'Carapita'),(137,41,'Antímano'),(138,41,'Caricuao'),(139,41,'Las Mayas'),(140,41,'Coche'),(141,41,'El Valle'),(142,41,'El Cementerio'),(143,41,'Los Rosales'),(144,41,'Valle Abajo'),(145,41,'Las Acacias'),(146,41,'Santa Mónica'),(148,41,'Cumbres de Curumo'),(149,41,'Santa Fe'),(150,41,'Los Campitos'),(151,41,'Prados del Este'),(152,41,'Alto Prado'),(153,41,'La Ciudadela'),(154,41,'Terrazas Club Hípico'),(156,41,'Santa Inés'),(158,41,'Los Samanes'),(161,41,'Sorocaima'),(162,41,'La Trinidad'),(164,41,'Baruta'),(165,41,'El Placer'),(166,41,'El Peñón'),(168,41,'Charallavito'),(169,41,'Los Naranjos-L.Mdes.'),(170,41,'La Boyera'),(171,41,'Las Marías'),(172,41,'El Hatillo'),(173,41,'Alto Hatillo'),(174,41,'San Luis'),(175,41,'Santa Paula'),(176,41,'Cerro Verde'),(177,41,'Los Pomelos'),(179,41,'La Quebradita'),(180,41,'23 de Enero'),(181,41,'Prado de María'),(183,41,'Los Castaños'),(184,41,'Gran Colombia'),(185,41,'El Conde'),(188,55,'Campo Alegre'),(190,55,'Ciudad Jardín Mañongo'),(191,55,'Camoruco'),(192,55,'El Bosque'),(193,55,'El Morro'),(195,55,'El Recreo'),(196,55,'El Viñedo (Barrio)'),(197,55,'Guataparo Country Club'),(199,55,'Kerdel'),(202,55,'La Arboleda'),(203,55,'La California'),(204,55,'La Ceiba'),(208,55,'La Trigaleña'),(209,55,'La Viña'),(210,55,'Las Acacias'),(211,55,'Las Quintas de Naguanagua'),(212,55,'Lomas del Este'),(213,55,'Los Naranjos'),(214,55,'Los Sauces'),(215,55,'Majay'),(217,55,'Miranda'),(218,55,'Parque El Trigal'),(219,55,'Santa Cecilia'),(220,55,'El Trigal Norte'),(221,55,'El Trigal Sur'),(222,55,'Terrazas de los Nísperos'),(223,55,'Prebo I'),(224,55,'Prebo II'),(225,55,'Prebo III'),(226,55,'Industrial Carabobo'),(227,55,'Industrial La Quizanda'),(228,55,'Ind. Municipal Norte'),(233,55,'La Isabelica'),(235,55,'Parque Valencia'),(236,55,'Parq. Ind. Castillito'),(237,55,'Castillito'),(240,55,'Guayabal'),(241,55,'La Florida'),(243,55,'Mañongo'),(246,55,'Santa Ana'),(251,55,'Antonio José de Sucre'),(253,55,'Bello Monte I'),(257,55,'Coromoto'),(258,55,'Don Bosco'),(263,55,'El Prado'),(264,55,'El Triunfo'),(266,55,'Eutimio Rivas'),(268,55,'La Blanquera'),(270,55,'La Castrera'),(272,55,'La Guacamaya'),(273,55,'La Milagrosa'),(279,55,'Libertad'),(281,55,'Los Taladros'),(282,55,'Monumental'),(287,55,'Ruíz Pineda '),(288,55,'San Agustín'),(289,55,'San Blas 1 y 2'),(291,55,'Santa Rosa'),(293,41,'Catedral'),(295,41,'Chacao'),(296,41,'Altamira'),(300,41,'Los Palos Grandes'),(303,41,'Maripérez'),(305,41,'Catia'),(307,41,'Ruiz Pineda'),(308,41,'Pérez Bonalde'),(309,51,'Santa Rosa'),(310,51,'Santa Ana'),(311,51,'El Bosque'),(312,51,'Las Acacias'),(313,51,'La Floresta'),(314,51,'La Barraca (Urb.)'),(316,51,'Zona Ind. San Vicente I'),(317,51,'Zona Ind. San Vicente II'),(319,51,'La Morita I'),(320,51,'El Piñonal'),(321,51,'Calicanto'),(323,91,'El Llanito'),(324,72,'Curazao'),(325,72,'Pueblo Nuevo'),(326,77,'Obrero'),(327,77,'Ermita'),(330,77,'Juan Maldonado'),(331,77,'Guayana'),(335,91,'Los Sauzales'),(337,72,'Lagunita'),(338,72,'Miranda'),(339,72,'La Popa'),(340,77,'La Concordia'),(344,77,'Centro'),(347,91,'La Magdalena'),(350,51,'San Isidro'),(351,51,'Coromoto'),(352,51,'El Milagro'),(354,51,'El Toro'),(355,51,'José Félix Rivas'),(356,51,'Los Caobos'),(358,51,'El Carmen'),(359,51,'La Democracia I'),(362,51,'Base Aragua'),(363,51,'La Cooperativa'),(364,51,'Los Olivos'),(365,51,'Andrés Bello'),(366,51,'La Arboleda'),(367,51,'La Soledad'),(368,51,'Bella Vista'),(369,51,'Independencia'),(370,51,'Libertador'),(371,51,'Santa Eduvigis'),(373,51,'Sucre'),(375,51,'San Jacinto'),(376,51,'Las Delicias'),(377,51,'Los Naranjos'),(379,51,'San Francisco'),(383,51,'San Rafael'),(385,51,'Bermúdez'),(386,51,'La Morita II'),(393,51,'Maracaya'),(394,51,'Samán de Guere'),(395,51,'23 de Enero'),(396,51,'San Miguel'),(399,51,'Alayón'),(400,51,'Campo Alegre'),(401,51,'Libertad'),(402,51,'Centro'),(404,51,'San Ignacio'),(405,51,'San Vicente'),(407,51,'Brisas del Lago'),(408,51,'La Romana'),(409,51,'El Limón'),(410,51,'La Candelaria'),(411,51,'Caña de Azúcar'),(412,51,'El Progreso'),(413,51,'Mata Seca'),(415,51,'El Piñal'),(417,51,'Valle Verde'),(418,55,'El Socorro'),(423,55,'Los Colorados'),(424,55,'Valle de Camoruco'),(425,55,'Michelena'),(426,55,'Ritec'),(427,55,'San Rafael'),(428,55,'Santa Eduvigis'),(429,55,'Central Tacarigua'),(430,55,'19 de Abril'),(431,55,'Unión'),(434,55,'Cabriales'),(436,55,'El Palotal'),(439,55,'Fundación Mendoza'),(442,55,'Santa Teresa'),(449,55,'Campo Solo'),(451,55,'Los Arales'),(452,55,'La Candelaria'),(454,77,'Pueblo Nuevo'),(456,91,'Alto Chama'),(460,77,'Pirineos'),(461,91,'Santa María'),(462,91,'San Juan Bautista'),(464,48,'Santa Lucía'),(465,48,'Valle Frío'),(467,48,'Altamira'),(470,48,'El Poniente'),(474,48,'La Chinita'),(475,48,'La Pomona'),(478,48,'San Rafael'),(485,48,'Vista al Lago'),(487,48,'Brisas del Sur'),(496,48,'La Misión'),(498,48,'Los Andes'),(499,48,'Los Estanques'),(500,48,'Los Pinos'),(502,48,'San Pedro'),(505,48,'Bella Vista'),(508,48,'Don Bosco'),(509,48,'Creole'),(511,48,'Las Mercedes'),(513,48,'Tierra Negra'),(523,48,'El Pedregal'),(524,48,'Francisco de Miranda'),(525,48,'José Antonio Páez'),(532,48,'San Miguel'),(534,48,'Cuatricentenaria'),(535,48,'Cumbres de Maracaibo'),(536,48,'El Prado'),(537,48,'La Floresta'),(540,48,'Las Lomas'),(541,48,'Raúl Leoni'),(550,48,'San Francisco'),(553,48,'San Felipe'),(558,48,'La Manzana de Oro'),(559,48,'Nueva Vía'),(560,48,'Santa María'),(561,48,'Sucre'),(562,48,'Buena Vista'),(563,48,'Las Tarabas'),(565,48,'La Trinidad'),(566,48,'Juana de Ávila'),(568,48,'Canaima'),(569,48,'Corazón de Jesús'),(571,48,'El Naranjal'),(573,48,'La California'),(581,48,'San Agustín'),(582,48,'San Jacinto'),(584,48,'Villa Delicias'),(585,48,'Los Olivos'),(586,48,'Rafael Urdaneta'),(587,48,'Ciudadela Faría'),(588,48,'Panamericano'),(589,48,'La Victoria'),(596,48,'Guaicaipuro'),(611,48,'Indio Mara'),(617,48,'5 de Julio'),(624,48,'El Trébol'),(625,48,'El Varillal'),(626,48,'Altos de la Vanega'),(628,48,'Urdaneta'),(631,48,'Cañada Honda'),(632,48,'El Amparo'),(634,48,'Gallo Verde'),(635,48,'Jorge Hernández'),(636,48,'La Pastora'),(637,48,'La Paz'),(638,48,'Los Claveles'),(641,48,'San José'),(645,48,'El Manzanillo'),(647,48,'Sierra Maestra'),(649,48,'Perijá'),(655,48,'La Limpia'),(658,48,'24 de Julio'),(659,48,'Carabobo'),(669,48,'Los Robles'),(670,48,'San Javier'),(671,48,'El Pilar'),(674,48,'La Estrella'),(679,48,'El Doral'),(680,48,'El Portal'),(682,48,'Irama'),(684,48,'Monte Bello'),(686,48,'18 de Octubre'),(687,48,'Altos de Jalisco'),(688,48,'Zapara'),(690,48,'Monte Claro'),(691,58,'Av. Municipal'),(692,58,'Mariño'),(695,48,'El Valle'),(703,48,'Puntica de Piedra'),(704,48,'Santa Rosa '),(706,48,'Bello Monte'),(717,48,'Las Delicias'),(718,48,'Veritas'),(719,48,'La Lago'),(721,48,'Sabaneta'),(722,48,'El Milagro'),(723,48,'Paraíso'),(1000,41,'Brisas de Propatria'),(1001,41,'Boquerón'),(1005,62,'Mundo Nuevo'),(1006,296,'Playa El Ángel'),(1007,296,'Campiare'),(1008,296,'Jóvito Villalba'),(1009,296,'Paraíso'),(1011,296,'Polanco'),(1014,62,'El Poblado'),(1015,62,'Guatamare'),(1016,62,'Palguamire'),(1018,62,'Genoves'),(1019,62,'Sabanamar'),(1020,62,'Costa Azul'),(1022,62,'San Fernando'),(1024,62,'El Cuarto'),(1025,62,'El Piache'),(1027,62,'Conejero'),(1029,62,'Pueblo Nuevo'),(1030,62,'Ciudad Cartón'),(1031,62,'Los Cocos'),(1032,62,'Los Cocos Norte'),(1034,62,'Bella Vista'),(1035,62,'Llano Adentro'),(1036,62,'Táchira'),(1037,62,'El Morro'),(1040,58,'Güaragüao'),(1041,58,'Los Yaques'),(1044,70,'Lecherías'),(1053,58,'Los Boqueticos'),(1054,58,'Oropeza Castillo'),(1055,58,'Bella Vista'),(1056,58,'Campo Alegre'),(1057,58,'Caribe'),(1058,58,'Chuparín'),(1059,58,'El Frio'),(1060,58,'El Pensil'),(1061,58,'La Caraqueña'),(1062,58,'La Tinia'),(1063,58,'Monte Cristo'),(1064,58,'Pueblo Nuevo'),(1065,58,'Tierra Adentro'),(1066,58,'Chuparín Arriba'),(1067,58,'Chuparín Central'),(1069,70,'Fernández Padilla'),(1070,70,'Río Viejo'),(1073,70,'Boyacá III'),(1074,70,'Boyacá IV'),(1075,70,'Boyacá V'),(1076,70,'Colinas del Neverí'),(1079,70,'Fundación Mendoza'),(1080,70,'Sect.Ind.Las Garzas'),(1081,70,'Razzetti (Razetti)'),(1082,58,'El Magüey'),(1083,58,'Isla Borracha'),(1084,70,'Las Garzas-Urb.'),(1086,70,'Brisas del Mar (Barrio)'),(1087,58,'Pozuelos'),(1088,58,'Pozuelito'),(1089,70,'Brisas del Mar (Urb.)'),(1090,70,'Buenos Aires'),(1091,70,'Colinas del Río'),(1092,70,'Corea'),(1093,70,'El Ingenio'),(1094,70,'La Montañita'),(1097,70,'Los Rosales'),(1098,70,'Nueva Barcelona'),(1100,70,'Paseo Cumanagoto'),(1102,70,'San José Obrero'),(1103,70,'Boyacá II'),(1105,70,'Sucre'),(1106,70,'El Espejo'),(1107,70,'Guamachito'),(1108,70,'Portugal'),(1109,70,'El Samán'),(1111,70,'Vista Alta'),(1113,70,'Camino Nuevo'),(1114,70,'Campo Alegre'),(1115,70,'La Matanza'),(1118,70,'Simón Bolívar'),(1119,70,'23 de Enero'),(1120,70,'Colombia'),(1122,70,'La Aduana'),(1123,70,'Los Montones'),(1124,51,'11 de Abril'),(1126,51,'Los Olivos Nuevos'),(1127,51,'Los Olivos Viejos'),(1128,51,'Corozal'),(1131,51,'Parque Aragua'),(1133,51,'Las Mayas'),(1136,51,'Camburito'),(1138,51,'La Pedrera'),(1139,51,'Toronjal'),(1142,51,'Ojo de Agua'),(1144,51,'Cadillal'),(1148,51,'1ro. de Mayo'),(1151,51,'El Hipódromo'),(1152,51,'Zona Ind. La Hamaca'),(1155,51,'La Esperanza'),(1156,51,'San Agustín'),(1158,51,'Zona Ind.San Miguel'),(1159,51,'Aquiles Nazoa'),(1160,51,'El Trébol'),(1161,51,'Fundación Mendoza'),(1162,51,'José G. Hernández'),(1163,51,'La Barraca (Barrio)'),(1165,51,'Los Samanes'),(1166,51,'Lourdes'),(1167,51,'Mario Briceño Iragorry'),(1168,51,'Piñonal Sur'),(1169,51,'San Carlos'),(1170,51,'San Pedro Alejandro'),(1173,51,'12 de Febrero'),(1174,51,'Belén'),(1176,51,'Girardot'),(1177,51,'Piñonal Norte'),(1178,55,'Colón'),(1180,55,'Brisas del Terminal'),(1181,55,'La Granja'),(1183,55,'Parque Cabriales'),(1184,55,'Parque Naguanagua'),(1186,55,'El Samán'),(1187,55,'Palma Real'),(1188,55,'Piedras Pintadas'),(1189,55,'Agua Blanca'),(1192,55,'Los Mangos'),(1194,55,'Prebo'),(1195,55,'San José de Tarbes'),(1196,55,'Sabana Larga'),(1197,55,'El Trigalito'),(1198,55,'Las Chimeneas'),(1199,55,'Las Clavelinas'),(1200,55,'El Trigal Centro'),(1201,55,'1ro. de Mayo'),(1202,55,'Atlas'),(1206,55,'Los Nísperos'),(1208,55,'Brisas del Este'),(1209,55,'Ezequiel Zamora'),(1211,55,'Libertador'),(1212,55,'Los Samanes Norte'),(1213,55,'Los Viveros'),(1223,55,'Negro Primero'),(1232,55,'Ambrosio Plaza'),(1234,55,'Bocaina I'),(1238,55,'Federación'),(1239,55,'Industrial La Guacamaya '),(1242,55,'13 de Septiembre'),(1246,55,'El Romancero'),(1248,55,'La Planta'),(1250,55,'Los Tamarindos'),(1253,55,'Santa Inés'),(1255,55,'Flor Amarillo'),(1256,55,'Industrial Araguaney'),(1261,55,'Villa Real'),(1262,55,'Bello Monte II'),(1263,55,'Bocaina II'),(1264,70,'Boyacá'),(1265,55,'El Viñedo (Urb.)'),(1266,41,'Alta Vista-Catia'),(1267,41,'Andrés Eloy Blanco'),(1270,41,'El Amparo'),(1272,41,'El Cuartel'),(1273,41,'Gramovén'),(1274,41,'Isaías M.Angarita'),(1276,41,'La Cortada'),(1282,41,'Nueva Caracas'),(1286,41,'Tamanaco'),(1287,41,'Vista al Mar'),(1288,41,'Agua Salud'),(1289,41,'Caño Amarillo'),(1291,41,'El Calvario'),(1292,41,'El Caribe'),(1293,41,'El Guarataro'),(1298,41,'Gato Negro'),(1300,41,'La Cañada'),(1301,41,'Los Frailes'),(1302,41,'Los Mecedores'),(1303,53,'Los Crepúsculos'),(1306,41,'Sierra Maestra'),(1307,41,'Sabana del Blanco'),(1308,53,'El Sisal'),(1310,53,'Zona Industrial I'),(1311,41,'Simón Rodríguez'),(1313,41,'Los Manolos'),(1315,41,'Pedregal'),(1317,41,'Santa María'),(1318,41,'Terrazas del Ávila'),(1319,41,'Miranda'),(1321,41,'El Carmen-Antímano'),(1325,41,'Germán Rodríguez'),(1330,41,'Santa Ana'),(1336,41,'La Hoyada'),(1337,41,'La Paz'),(1338,41,'La Alameda'),(1339,41,'La Veguita'),(1340,41,'Las Fuentes'),(1344,41,'La Montaña-Cota 905'),(1345,41,'Las Flores P. Hierro'),(1346,41,'Los Molinos'),(1354,41,'El Peaje'),(1355,41,'Fuerte Tiuna'),(1357,41,'La Bandera'),(1358,41,'La Ceiba'),(1363,41,'San Miguel-Cementerio'),(1373,53,'Simón Rodríguez'),(1374,41,'La California Norte'),(1375,41,'La California Sur'),(1379,41,'19 de Abril-Petare'),(1382,41,'El Centro'),(1386,41,'Las Vegas'),(1387,41,'Maca'),(1391,41,'Unión'),(1393,41,'Alta Vista-E.Llanito'),(1394,41,'Bolívar'),(1402,41,'San Isidro'),(1403,41,'Brisas de Turumo'),(1406,53,'Brisas del Obelisco'),(1408,53,'Santa Eduvigis'),(1409,41,'El Rosario'),(1413,41,'La Quebrada'),(1414,41,'Mamera'),(1422,41,'Los Mangos'),(1423,41,'Cochecito'),(1431,41,'Longaray'),(1432,41,'Los Jardines del V.'),(1433,41,'Parque Humboldt'),(1434,41,'Santa Gertrudis'),(1435,41,'El Güire'),(1436,41,'El Mirador'),(1437,41,'Guaycay'),(1438,41,'La Bonita'),(1439,41,'La Tahona'),(1440,41,'Las Mesetas'),(1443,41,'Los Riscos'),(1444,41,'Santa Cruz-Club Hípico'),(1446,41,'Vizcaya'),(1447,53,'Las Trinitarias'),(1456,41,'Kennedy'),(1461,41,'La Montaña-Caricuao'),(1462,41,'Las Adjuntas'),(1464,41,'Los Pinos'),(1465,41,'Los Telares'),(1466,41,'Macarao'),(1469,41,'UD-1'),(1476,41,'La Candela'),(1477,41,'UD-2'),(1478,41,'UD-3'),(1479,41,'UD-4'),(1480,41,'UD-5'),(1481,41,'UD-6'),(1482,41,'Barrilito'),(1485,41,'Manzanares'),(1486,41,'Minas de Baruta'),(1487,41,'Piedra Azul'),(1489,41,'La Esmeralda'),(1490,41,'Monterrey'),(1491,41,'Cantarrana'),(1492,41,'Los Geranios'),(1494,41,'La Lagunita'),(1501,53,'El Carmen'),(1502,53,'Patarata'),(1503,53,'Santa Inés'),(1504,53,'Caja de Agua'),(1505,53,'El Rosal'),(1506,53,'Jacinto Lara'),(1509,53,'Los Libertadores'),(1510,53,'Nueva Segovia'),(1511,53,'23 de Enero'),(1512,53,'Fundalara'),(1513,53,'Colinas Santa Rosa'),(1514,53,'Santa Elena'),(1515,53,'San José'),(1516,53,'Gil Fortoul'),(1517,53,'Sucre'),(1518,53,'Terepaima'),(1519,91,'San Isidro'),(1520,53,'La Concordia'),(1521,53,'Bararida'),(1522,91,'Santa Ana'),(1523,91,'Simón Bolívar'),(1525,91,'Pueblo Nuevo'),(1527,91,'Santo Domingo'),(1528,91,'Magdalena'),(1529,91,'Don Pancho'),(1531,91,'El Encanto'),(1532,91,'Buena Vista'),(1535,91,'Mucujun'),(1539,91,'Los Curos'),(1542,91,'La Pedregosa'),(1543,91,'Albarregas'),(1544,91,'Los Pinos'),(1546,91,'La Trinidad'),(1547,91,'Santa Bárbara'),(1548,91,'La Hacienda'),(1550,91,'La Mata'),(1554,91,'La Mara'),(1557,91,'La Sabana'),(1558,91,'El Central'),(1559,91,'Las Tapias'),(1560,91,'San Antonio'),(1561,91,'Santa Juana'),(1562,91,'San Cristobal'),(1563,91,'Las Dalias'),(1569,48,'Los Mangos'),(1572,48,'Canta Claro'),(1574,48,'La Picola'),(1576,48,'Mara Norte'),(1578,48,'Tarabas'),(1579,48,'Viento Norte'),(1580,48,'Altos de Milagro Norte'),(1584,48,'San Roque'),(1585,48,'12 de Octubre'),(1586,48,'Amparo'),(1589,48,'La Macondona'),(1590,48,'Los Postes Negros'),(1591,48,'Valle Alto'),(1592,48,'Valle Claro'),(1593,48,'1º de Mayo'),(1594,48,'Sabana Grande'),(1595,48,'Santa Bárbara '),(1600,48,'La Virginia'),(1601,48,'Andrés Eloy Blanco'),(1605,48,'Ixora Rojas'),(1607,48,'Libertad'),(1610,48,'Unión'),(1615,48,'Campo Claro'),(1616,48,'Cerro Pelado'),(1619,48,'La Conquista'),(1620,48,'La Ranchería'),(1625,48,'Bolívar'),(1627,48,'Zona Industrial I'),(1629,48,'El Gaitero'),(1632,48,'El Cardonal Sur'),(1634,48,'Lago Azul'),(1638,48,'San Benito'),(1639,48,'San Sebastian'),(1643,48,'El Pinar'),(1645,48,'Los Haticos'),(1646,48,'Sur América'),(1649,48,'Zona Industrial II'),(1654,48,'El Silencio'),(1662,48,'El Bajo'),(1665,48,'El Perú'),(1667,48,'La Coromoto'),(1669,48,'San Ramón'),(1671,91,'La Campiña'),(1672,91,'Santa Anita'),(1673,91,'La Milagrosa'),(1677,67,'Corralito'),(1678,41,'Carmelitas'),(1679,425,'El Junko'),(1680,425,'Luis Hurtado'),(1681,41,'Sartenejas'),(1684,41,'Colinas de Bello Monte'),(1685,41,'Lebrúm'),(1686,41,'Hoyo de la Puerta'),(1687,62,'4 de Mayo'),(1689,62,'La Arboleda'),(1690,62,'Santiago Mariño'),(1691,62,'Macho Muerto'),(1692,296,'San Lorenzo'),(1693,62,'San Rafael'),(1694,252,'Laguna Honda'),(1695,417,'Conuco Viejo'),(1696,442,'Playa El Agua'),(1697,442,'Playa Parguito'),(1699,442,'Manzanillo'),(1700,442,'El Cardón'),(1703,62,'Boulevard Guevara'),(1704,62,'Boulevard Gómez'),(1705,62,'Terranova'),(1707,296,'La Caranta'),(1708,255,'San Sebastian'),(1709,255,'El Copey'),(1711,255,'Atamo Norte'),(1712,255,'Atamo Sur'),(1716,442,'El Salado'),(1717,442,'Paraguachí'),(1722,252,'Altagracia'),(1723,252,'Los Millanes'),(1724,62,'El Silguero'),(1725,62,'Los Mártires'),(1726,62,'La Isleta'),(1729,235,'El Datil'),(1730,235,'Los Bagres'),(1735,316,'El Guamache'),(1739,428,'Boca de Rio'),(1745,428,'Punta Arenas'),(1751,428,'San Francisco'),(1754,442,'Puerto Fermin (El Tirano)'),(1755,441,'Boquerón'),(1757,426,'San Pedro'),(1758,62,'Achípano'),(1759,62,'Cruz Grande'),(1760,255,'Palo Sano'),(1761,235,'Playa El Yaque'),(1762,62,'Centro'),(1763,252,'Centro'),(1764,255,'Cocheima'),(1765,441,'Carapacho'),(1766,62,'Valle Abajo'),(1767,255,'Santa Isabel'),(1768,255,'El Tamarindo'),(1769,62,'Guaraguao'),(1771,252,'La Galera'),(1772,252,'Nuevo Juan Griego'),(1773,252,'El Fortin'),(1774,252,'Guaimara'),(1775,296,'La Ceiba'),(1777,255,'La Otra Banda'),(1778,252,'La Vencidad'),(1780,441,'La Guardia'),(1781,41,'El Recreo'),(1782,41,'Capitolio'),(1783,41,'Parque Carabobo'),(1784,41,'Bellas Artes'),(1785,41,'Colinas de Santa Mónica'),(1786,62,'Dumar'),(1789,41,'Galipán'),(1790,41,'Boleíta '),(1792,41,'Oripoto'),(1793,41,'Los Guayabitos'),(1794,41,'Los Próceres'),(1795,417,'Valle Hermoso Villa'),(1796,41,'Alta Florida'),(1797,41,'Lomas del Ávila'),(1798,41,'Lomas de la Trinidad'),(1799,316,'El Águila'),(1800,41,'La Guairita'),(1801,41,'La Rinconada'),(1802,41,'Zona Industrial la Naya'),(1803,41,'Carlos Delgado Chalbaud'),(1804,55,'Alegría'),(1806,55,'Carmen Norte'),(1807,55,'Carmen Sur'),(1808,55,'Cañaveral'),(1809,55,'Centro'),(1811,55,'Colinas de Guataparo'),(1815,55,'El Parral'),(1818,55,'Florida Norte'),(1820,55,'La Castellana'),(1821,55,'La Esmeralda'),(1826,55,'Los Caobos'),(1827,55,'Los Jardínes'),(1834,55,'Paraparal'),(1835,55,'Urdaneta'),(1836,55,'Valle de Aguirre'),(1837,55,'Victoria'),(1839,55,'Yagua'),(1840,55,'Yuma I'),(1842,113,'Los Cerritos'),(1843,55,'Carabobo'),(1844,296,'Peñas Blancas'),(1845,55,'Guaparo'),(1846,55,'El Morro II'),(1847,55,'El Trigal'),(1848,41,'Lomas de Las Mercedes'),(1849,55,'San José'),(1850,387,'Campo de Carabobo'),(1851,138,'Chirgua'),(1852,59,'Ciudad Alianza'),(1855,138,'Agua Clara'),(1873,138,'El León'),(1877,138,'El Rincón'),(1904,138,'Miraflores'),(1910,138,'San Antonio'),(1917,138,'Santa María'),(1933,59,'Araguita'),(1934,59,'Barrio Obrero'),(1938,59,'Coromoto'),(1939,59,'El Placer'),(1940,59,'El Samán'),(1942,59,'La Emboscada'),(1943,59,'La Floresta'),(1944,59,'La Florida'),(1949,59,'La Tigrera'),(1954,59,'Los Naranjos'),(1958,59,'Naranjillo'),(1959,59,'Negro Primero'),(1962,59,'San Agustín'),(1964,59,'Santa Eduviges'),(1966,59,'Turumo'),(1968,114,'Tarapio'),(1969,41,'Colinas del Tamanaco'),(1975,246,'Buena Vista'),(1981,246,'El Ávila'),(2045,113,'Las Agüitas'),(2047,113,'Las Garcitas'),(2049,113,'Libertador'),(2051,113,'Los Guayos'),(2093,76,'Colinas de Mara I'),(2099,76,'El Trapiche'),(2103,76,'Santa Ana'),(2106,119,'Casco Central'),(2107,119,'El Banco'),(2125,387,'Bella Vista'),(2133,387,'El Molino'),(2137,387,'El Vigia'),(2141,115,'Agua Blanca'),(2142,115,'Bolívar'),(2145,115,'El Carmen'),(2156,115,'Libertador'),(2180,71,'Paso Real'),(2188,114,'Bárbula'),(2189,114,'Bella Vista'),(2199,114,'Ciudad Jardín Mañongo'),(2204,114,'Coromoto'),(2216,114,'Guayabal'),(2218,114,'La Begoña'),(2220,114,'La Campiña'),(2223,114,'La Florida'),(2231,114,'Las Quintas'),(2236,114,'Los Próceres'),(2237,114,'Malagón'),(2241,114,'Nueva Esparta'),(2243,114,'Palma Real'),(2250,114,'Santa Ana'),(2251,114,'Simón Bolívar'),(2252,114,'Tarapio'),(2254,114,'Unión'),(2257,56,'5 de Julio'),(2258,56,'Ajuro'),(2259,56,'Andrés Eloy Blanco'),(2262,56,'Carlos Felipe'),(2263,56,'Cartón'),(2265,56,'Cumboto'),(2267,56,'El Faro'),(2269,56,'El Peaje'),(2270,56,'El Polvorin'),(2272,56,'Ezequiel Zamora'),(2273,56,'Industrial'),(2274,56,'La Belisa'),(2278,56,'La Pedrera'),(2281,56,'La Sorpresa'),(2283,56,'Las Catorce'),(2288,56,'Nuevas Brisas'),(2291,56,'Playa Blanca'),(2294,56,'Rancho Grande'),(2295,56,'San Esteban'),(2296,56,'Santa Cruz'),(2305,387,'La Guásima'),(2316,62,'Playa Marina Concorde'),(2317,55,'San Diego'),(2318,152,'Ambrosio'),(2319,152,'Miraflores'),(2320,152,'La Misión'),(2321,152,'La Vereda'),(2322,152,'Delicias Nuevas'),(2323,55,'Monteserino'),(2324,152,'Las Cabillas'),(2325,55,'El Morro I'),(2326,113,'Centro'),(2327,441,'Los Fermines'),(2328,441,'Las Vegas'),(2329,59,'Zona Industrial Pruinca'),(2330,41,'Nuevo Prado'),(2331,41,'Mirávila'),(2332,55,'Catedral'),(2333,55,'Tocuyito'),(2334,115,'Pueblo Nuevo'),(2336,132,'El Progreso'),(2343,152,'12 de Octubre'),(2351,152,'Bella Vista'),(2352,152,'Bello Monte'),(2353,152,'Buena Vista'),(2355,152,'Campo Alegre'),(2359,152,'Casco Central'),(2360,152,'Centro'),(2371,152,'El Milagro'),(2373,152,'El Rosario'),(2374,152,'El Solito'),(2377,152,'Francisco de Miranda'),(2382,152,'Independencia'),(2386,152,'José Felix Rivas'),(2392,152,'La Rosa Vieja'),(2400,152,'Los Hornitos'),(2402,152,'Los Laureles (Urb.)'),(2409,152,'Nueva Cabimas (Urb.)'),(2412,152,'Punta Gorda'),(2416,152,'San José I'),(2419,152,'Santa Clara'),(2426,152,'Tierra Negra'),(2431,156,'Nueva Bolivia'),(2436,178,'Latina'),(2443,99,'Andrés Bello'),(2445,99,'Barrio Nuevo'),(2451,99,'El Porvenir'),(2455,99,'Las Morochas'),(2456,99,'Libertad'),(2458,99,'Los Samanes'),(2464,387,'Las Palmas'),(2465,51,'Choroní'),(2473,48,'Santa Fe'),(2474,55,'Eligio Macias Mújica'),(2475,316,'La Blanquilla'),(2476,48,'Belloso'),(2477,48,'El Saladillo'),(2478,48,'Santa Rosa II'),(2479,106,'Mochima'),(2480,48,'El Tránsito'),(2481,48,'Brisas del Norte'),(2486,48,'Casco Central'),(2488,48,'Cecilio Acosta'),(2489,48,'Chiquinquirá'),(2493,48,'Dr. Portillo'),(2497,56,'Malabares'),(2505,99,'Pinto Salinas'),(2510,48,'Idelfonso Vásquez'),(2512,114,'Los Guayabitos'),(2517,48,'Santa Rita'),(2521,48,'La Punta'),(2522,48,'La Rotaria'),(2529,48,'Los Samanes'),(2532,48,'Manzanillo'),(2537,56,'Tejerías'),(2549,48,'María Concepción Palacios'),(2553,99,'1º de Mayo'),(2560,99,'San José'),(2564,99,'Unión'),(2566,258,'Campo Elías'),(2586,270,'Campo Florida Grande'),(2596,282,'Centro'),(2597,282,'Santa Teresa'),(2599,63,'Independencia'),(2608,313,'El Tablazo'),(2612,56,'Los Muelles'),(2613,152,'5 Bocas'),(2614,363,'Centro'),(2615,41,'Colinas de los Caobos'),(2616,55,'La Yaguara'),(2617,48,'Las Corubas'),(2619,90,'Centro'),(2620,48,'Zona Industrial Norte'),(2624,90,'Barrancón'),(2625,90,'Los Meregotos'),(2629,90,'Corinsa'),(2631,90,'Prados de la Encrucijada'),(2637,90,'La Ciudadela'),(2639,90,'La Trinidad'),(2651,90,'Las Vegas'),(2658,54,'Bolívar Norte'),(2659,54,'Bolívar Sur'),(2660,54,'Las Mercedes'),(2661,54,'La Mora'),(2663,54,'El Avión'),(2664,54,'Nueva Victoria'),(2673,54,'La Otra Banda'),(2674,54,'Casco Histórico'),(2675,54,'Morichal'),(2677,104,'Tiarita'),(2685,104,'Agua Amarilla'),(2691,387,'Centro'),(2692,55,'Plaza de Toros'),(2693,258,'Centro'),(2695,55,'Andrés Eloy Blanco'),(2696,54,'Centro'),(2697,105,'Magdaleno'),(2699,51,'Lago II'),(2701,115,'17 de Diciembre'),(2702,159,'La Bomba'),(2713,97,'San José'),(2714,97,'Zona Colonial'),(2716,214,'Centro'),(2737,387,'Barrerita'),(2742,51,'El Castaño'),(2743,51,'El Piñonal Sur'),(2748,51,'Los Chaguaramos'),(2751,51,'Mata Redonda'),(2754,51,'Río Blanco I'),(2756,61,'Carabaño Norte'),(2757,105,'Orticeño'),(2758,61,'Fundavilla'),(2759,291,'El Playón'),(2765,105,'La Atascosa'),(2788,364,'La Arboleda'),(2792,364,'Los Manguitos'),(2801,82,'La Encrucijada'),(2804,82,'La Julia'),(2811,82,'San Joaquin'),(2820,82,'Campo Alegre'),(2824,82,'El Tierral'),(2825,82,'Fundación Mendoza'),(2856,61,'Apolo'),(2858,61,'Camejo'),(2868,252,'Tacarigua'),(2870,316,'Las Guevaras'),(2875,59,'Yagua'),(2876,41,'El Dorado'),(2877,54,'Industrial Soco'),(2891,104,'Jabillar'),(2897,105,'Centro'),(2904,443,'Centro'),(2926,51,'Bolívar'),(2927,255,'Guacuco'),(2928,152,'19 de Abril'),(2945,56,'Zona Colonial'),(2946,442,'La Fuente'),(2948,445,'Altagracia'),(2949,445,'Tacarigua'),(2950,445,'Valle de Pedro González'),(2951,445,'Guayacán'),(2952,445,'El Cercado'),(2953,445,'El Maco'),(2954,255,'Playa Guacuco'),(2955,255,'Las Huertas'),(2956,255,'Salamanca'),(2959,296,'Lagunamar'),(2960,99,'Casco Central'),(2961,296,'Maneiro'),(2962,296,'Los Robles'),(2963,41,'Colinas de la California'),(2964,296,'Jorge Coll'),(2965,446,'La Cruz del Pastel'),(2966,446,'San Antonio'),(2967,446,'Valle Verde'),(2968,446,'Centro'),(2969,99,'Tasajeras'),(2972,56,'Paseo El Malecón'),(2973,59,'Brisas del Lago'),(2974,51,'San José'),(2975,48,'San Martín'),(2976,55,'Ind. Municipal Sur'),(2977,48,'Bellas Artes'),(2978,48,'La Ciega'),(2979,104,'Zona Industrial Guayas'),(2981,51,'La Providencia'),(2982,51,'Base Sucre'),(2983,51,'19 de Abril'),(3004,376,'Cruz Alta'),(3062,153,'Centro'),(3063,153,'Chucho Briceño'),(3065,153,'El Recreo'),(3069,153,'La Mata'),(3070,153,'La Mora'),(3084,53,'5 de Julio'),(3087,53,'Bella Vista'),(3088,53,'Bolívar'),(3090,53,'Cerritos Blancos'),(3092,53,'Club Hípico Trinitarias'),(3094,53,'Colina del Turbio'),(3095,53,'Colinas Jebe II'),(3096,53,'Concepción'),(3107,53,'Del Este'),(3110,53,'El Parral'),(3112,53,'El Pinal'),(3118,53,'Zona Industrial III'),(3129,53,'La Pastora'),(3147,53,'Manaure'),(3149,53,'Monte Real'),(3152,53,'Negro Primero'),(3155,53,'Propatria'),(3156,53,'Pueblo Nuevo'),(3159,53,'Río Lama'),(3163,53,'San Antonio'),(3165,53,'San Francisco'),(3166,53,'San Jacinto'),(3170,53,'Santa Isabel'),(3173,53,'Tierra Negra'),(3180,53,'Zona Industrial II'),(3181,53,'Campo Verde'),(3199,125,'Alta Vista'),(3204,125,'Centro'),(3230,70,'5 de Julio'),(3235,53,'El Parque'),(3236,58,'Cerro Sur'),(3237,105,'Los Robles'),(3238,153,'Las Tunas'),(3239,53,'Centro'),(3240,53,'La Ensenada'),(3241,53,'La Floresta'),(3247,275,'El Morro');
/*!40000 ALTER TABLE urbanizaciones ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-07-02 10:51:28
