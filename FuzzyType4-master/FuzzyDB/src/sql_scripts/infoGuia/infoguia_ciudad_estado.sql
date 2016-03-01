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
-- Table structure for table `ciudad_estado`
--

DROP TABLE IF EXISTS `ciudad_estado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ciudad_estado` (
  `codigociudad` smallint(5) unsigned NOT NULL,
  `capital` char(1) DEFAULT 'N',
  `codigoestado` tinyint(3) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ciudad_estado`
--

LOCK TABLES `ciudad_estado` WRITE;
/*!40000 ALTER TABLE `ciudad_estado` DISABLE KEYS */;
INSERT INTO `ciudad_estado` VALUES (41,'S',1),(44,'S',17),(48,'S',23),(51,'S',5),(53,'S',12),(55,'S',8),(56,'N',8),(57,'N',7),(58,'N',3),(59,'N',8),(60,'N',14),(62,'S',16),(67,'S',14),(70,'S',3),(71,'N',8),(74,'S',6),(75,'N',11),(77,'S',19),(78,'N',14),(80,'N',20),(81,'S',15),(82,'N',5),(83,'N',20),(85,'S',10),(90,'N',5),(91,'S',13),(98,'N',10),(100,'N',14),(101,'N',14),(103,'N',14),(106,'S',18),(107,'S',11),(111,'N',9),(114,'N',8),(121,'S',22),(138,'N',8),(152,'N',23),(153,'N',12),(174,'N',12),(204,'N',12),(230,'N',3),(234,'N',13),(240,'N',17),(242,'N',3),(262,'S',21),(273,'N',23),(275,'N',3),(286,'N',14),(323,'N',14),(332,'S',9),(387,'N',8),(398,'S',20),(403,'N',12),(425,'N',1),(443,'N',5);
/*!40000 ALTER TABLE `ciudad_estado` ENABLE KEYS */;
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
