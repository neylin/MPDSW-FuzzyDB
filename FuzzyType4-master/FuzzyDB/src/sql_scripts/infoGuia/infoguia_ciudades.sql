CREATE DATABASE  IF NOT EXISTS `infoguia` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `infoguia`;
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
-- Table structure for table `ciudades`
--

DROP TABLE IF EXISTS `ciudades`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ciudades` (
  `codigociudad` smallint(5) unsigned NOT NULL,
  `codigotlf` varchar(4) DEFAULT NULL,
  `nombreciudad` varchar(30) DEFAULT NULL,
  PRIMARY KEY (`codigociudad`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ciudades`
--

LOCK TABLES `ciudades` WRITE;
/*!40000 ALTER TABLE `ciudades` DISABLE KEYS */;
INSERT INTO `ciudades` VALUES (41,'0212','Caracas'),(44,'0255','Acarigua'),(48,'0261','Maracaibo'),(51,'0243','Maracay'),(53,'0251','Barquisimeto'),(55,'0241','Valencia'),(56,'0242','Puerto Cabello'),(57,'0286','Puerto Ordaz'),(58,'0281','Puerto la Cruz'),(59,'0245','Guacara'),(60,'0212','Guatire'),(62,'0295','Porlamar'),(67,'0212','Los Teques'),(70,'0281','Barcelona'),(71,'0249','Montalban'),(74,'0273','Barinas'),(75,'0235','Valle de la Pascua'),(77,'0276','San Cristobal'),(78,'0212','Guarenas'),(80,'0271','Motatan'),(81,'0291','Maturin'),(82,'0244','Turmero'),(83,'0272','Trujillo'),(85,'0268','Coro'),(90,'0244','Cagua'),(91,'0274','Merida'),(98,'0269','Punto Fijo'),(100,'0212','Fila de Mariches'),(101,'0212','Turumo'),(103,'0234','Higuerote'),(106,'0293','Cumana'),(107,'0246','San Juan de los Morros'),(111,'0258','Tinaquillo'),(114,'0241','Naguanagua'),(121,'0254','San Felipe'),(138,'0249','Bejuma'),(152,'0264','Cabimas'),(153,'0251','Cabudare'),(174,'0252','Carora'),(204,'0253','Duaca'),(230,'0283','El Tigre'),(234,'0275','El Vigia'),(240,'0257','Guanare'),(242,'0281','Guanta'),(262,'0212','La Guaira'),(273,'0263','Las Piedras'),(275,'0281','Lecherias'),(286,'0234','Mamporal'),(323,'0234','Rio Chico'),(332,'0258','San Carlos'),(387,'0241','Tocuyito'),(398,'0271','Valera'),(403,'0251','Yaritagua'),(425,'0212','El Junquito'),(443,'0243','Santa Rita');
/*!40000 ALTER TABLE `ciudades` ENABLE KEYS */;
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
