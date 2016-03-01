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
-- Table structure for table `secciones`
--

DROP TABLE IF EXISTS `secciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `secciones` (
  `codigoseccion` tinyint(4) NOT NULL,
  `nombreseccion` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`codigoseccion`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='	';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `secciones`
--

LOCK TABLES `secciones` WRITE;
/*!40000 ALTER TABLE `secciones` DISABLE KEYS */;
INSERT INTO `secciones` VALUES (1,'Adornos, Arreglos y Regalos'),(2,'Agricultura, Cria, Animales y Plantas'),(3,'Alimentos ... Equipos y Accesorios'),(4,'Alimentos ... Productos'),(5,'Publicidad, Material Pop, Artes Graficas'),(6,'Articulos ... Varios'),(7,'Automoviles, Vehiculos, Camiones, Motos'),(8,'Aviones, Barcos y Trenes'),(9,'Bancos, Seguros, Finanzas, Creditos'),(10,'Bebidas y Tabaco'),(11,'Belleza, Cuidado E Higiene'),(12,'Computacion, Informatica y Tecnologia'),(13,'Construccion, Decoracion ... Productos'),(14,'Construccion, Decoracion ... Servicios'),(15,'Consultores, Asesores ... Varios'),(16,'Deportes, Recreacion, Hobbies y Juegos'),(17,'Educacion, Aprendizaje y Adiestramiento'),(18,'Electricidad, Electronica, Iluminacion'),(19,'Empaques, Envases, Recipientes'),(20,'Eventos, Arte y Espectaculos'),(21,'Ferreterias, Pinturas y Sanitarios'),(22,'Festejos y Recepciones'),(23,'Fumigacion, Incineracion y Purificacion'),(25,'Limpieza, Pulitura y Mantenimiento'),(26,'Maderas y Vidrios'),(27,'Maquinas, Maquinarias y Equipos'),(28,'Materiales, Productos ... Varios'),(29,'Metalmecanica, Metalurgia y Plasticos'),(30,'Muebles, Mobiliario y Cuadros'),(31,'Oficina, Escolar, Papeleria, Libros'),(32,'Organismos, Instituciones, Gobierno'),(33,'Pisos, Techos, Paredes, Puertas, Ventanas'),(34,'Quimica, Energia, Petroleo y Minas'),(35,'Refrigeracion y Calefaccion'),(36,'Restaurantes y Centros Nocturnos'),(37,'Ropa, Calzado, Accesorios y Joyas'),(38,'Salud, Medicina, Ciencia y Bienestar'),(39,'Seguridad - Proteccion, Seguridad - Control'),(40,'Servicios y Comercios ... Varios'),(41,'Sonido, Video y Fotografia'),(42,'Telecomunicaciones, Telefonia, Internet'),(43,'Textiles, Lenceria, Mercerias, Tapicerias'),(44,'Transportadores, Elevadores y Gruas'),(45,'Turismo, Negocio, Correo y Transporte'),(46,'Vajillas y Utensilios'),(47,'Medios de Comunicacion Audiovisuales'),(48,'Medios de Comunicacion Digitales'),(49,'Medios de Comunicacion Impresos');
/*!40000 ALTER TABLE `secciones` ENABLE KEYS */;
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
