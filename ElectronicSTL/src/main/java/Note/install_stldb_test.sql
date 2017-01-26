-- MySQL dump 10.13  Distrib 5.7.17, for Linux (x86_64)
--
-- Host: localhost    Database: stldb_test
-- ------------------------------------------------------
-- Server version	5.7.17-0ubuntu0.16.10.1

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
-- Table structure for table `stl_customers`
--

DROP TABLE IF EXISTS `stl_customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_customers` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_customers`
--

LOCK TABLES `stl_customers` WRITE;
/*!40000 ALTER TABLE `stl_customers` DISABLE KEYS */;
/*!40000 ALTER TABLE `stl_customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_defect_type`
--

DROP TABLE IF EXISTS `stl_defect_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_defect_type` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_defect_type`
--

LOCK TABLES `stl_defect_type` WRITE;
/*!40000 ALTER TABLE `stl_defect_type` DISABLE KEYS */;
INSERT INTO `stl_defect_type` VALUES (1,'Discrepancy'),(2,'Interruption'),(3,'Snag');
/*!40000 ALTER TABLE `stl_defect_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_defects`
--

DROP TABLE IF EXISTS `stl_defects`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_defects` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `simulator_id` int(11) DEFAULT NULL,
  `page_id` int(11) DEFAULT NULL,
  `defect_type_id` int(11) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `corrective_action` varchar(100) NOT NULL DEFAULT ' ',
  `datetime_start` datetime DEFAULT NULL,
  `datetime_end` datetime DEFAULT NULL,
  `ata_syscode` int(11) NOT NULL DEFAULT '0',
  `ata_subcode` int(11) NOT NULL DEFAULT '0',
  `ata_description` varchar(45) NOT NULL DEFAULT ' ',
  `solved` tinyint(4) DEFAULT '0',
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_defects`
--

LOCK TABLES `stl_defects` WRITE;
/*!40000 ALTER TABLE `stl_defects` DISABLE KEYS */;
/*!40000 ALTER TABLE `stl_defects` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_device_performance`
--

DROP TABLE IF EXISTS `stl_device_performance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_device_performance` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_device_performance`
--

LOCK TABLES `stl_device_performance` WRITE;
/*!40000 ALTER TABLE `stl_device_performance` DISABLE KEYS */;
INSERT INTO `stl_device_performance` VALUES (1,'Unsatisfactory'),(2,'Poor'),(3,'Acceptable'),(4,'Good'),(5,'Excellent');
/*!40000 ALTER TABLE `stl_device_performance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_levels`
--

DROP TABLE IF EXISTS `stl_levels`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_levels` (
  `id` tinyint(4) NOT NULL,
  `name` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_levels`
--

LOCK TABLES `stl_levels` WRITE;
/*!40000 ALTER TABLE `stl_levels` DISABLE KEYS */;
INSERT INTO `stl_levels` VALUES (1,'User'),(2,'Device User'),(3,'Technical'),(4,'Administrator'),(5,'Super Administrator');
/*!40000 ALTER TABLE `stl_levels` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_maintenance_type`
--

DROP TABLE IF EXISTS `stl_maintenance_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_maintenance_type` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_maintenance_type`
--

LOCK TABLES `stl_maintenance_type` WRITE;
/*!40000 ALTER TABLE `stl_maintenance_type` DISABLE KEYS */;
INSERT INTO `stl_maintenance_type` VALUES (1,'Preventive Maintenance'),(2,'Scheduled Maintenance'),(3,'Unscheduled Maintenance');
/*!40000 ALTER TABLE `stl_maintenance_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_pages`
--

DROP TABLE IF EXISTS `stl_pages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_pages` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `simulator_id` int(11) DEFAULT NULL,
  `date_page` datetime DEFAULT NULL,
  `sched_datetime_start` varchar(30) DEFAULT NULL,
  `sched_datetime_end` varchar(30) DEFAULT NULL,
  `sched_datetime_total` varchar(30) DEFAULT NULL,
  `actual_datetime_start` varchar(30) DEFAULT NULL,
  `actual_datetime_end` varchar(30) DEFAULT NULL,
  `actual_datetime_total` varchar(30) DEFAULT NULL,
  `ttl_start` varchar(30) DEFAULT NULL,
  `ttl_end` varchar(30) DEFAULT NULL,
  `ttl_total` varchar(30) DEFAULT NULL,
  `customer_id` int(11) DEFAULT NULL,
  `training_type_id` int(11) DEFAULT NULL,
  `session_content` varchar(100) DEFAULT NULL,
  `maintenance_type_id` int(11) DEFAULT NULL,
  `device_users` varchar(150) DEFAULT NULL,
  `students` varchar(150) DEFAULT NULL,
  `observers` varchar(150) DEFAULT NULL,
  `maintenance_called` tinyint(4) DEFAULT NULL,
  `training_completed` tinyint(4) DEFAULT NULL,
  `interruptions` int(11) DEFAULT NULL,
  `lost_training_time` int(11) DEFAULT NULL,
  `device_performance_id` int(11) DEFAULT NULL,
  `remarks` text,
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_pages`
--

LOCK TABLES `stl_pages` WRITE;
/*!40000 ALTER TABLE `stl_pages` DISABLE KEYS */;
/*!40000 ALTER TABLE `stl_pages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_periodical_test`
--

DROP TABLE IF EXISTS `stl_periodical_test`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_periodical_test` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_periodical_test`
--

LOCK TABLES `stl_periodical_test` WRITE;
/*!40000 ALTER TABLE `stl_periodical_test` DISABLE KEYS */;
INSERT INTO `stl_periodical_test` VALUES (1,'QTG'),(2,'Flyout'),(3,'NAV DATA Update'),(4,'IOS DATA Update'),(5,'Video Calibration'),(6,'Transport Delay');
/*!40000 ALTER TABLE `stl_periodical_test` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_periodical_test_values`
--

DROP TABLE IF EXISTS `stl_periodical_test_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_periodical_test_values` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `page_id` int(11) DEFAULT NULL,
  `periodical_test_id` int(11) DEFAULT NULL,
  `number` varchar(15) DEFAULT NULL,
  `year` varchar(15) DEFAULT NULL,
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_periodical_test_values`
--

LOCK TABLES `stl_periodical_test_values` WRITE;
/*!40000 ALTER TABLE `stl_periodical_test_values` DISABLE KEYS */;
/*!40000 ALTER TABLE `stl_periodical_test_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_simulator_status`
--

DROP TABLE IF EXISTS `stl_simulator_status`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_simulator_status` (
  `id` tinyint(4) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_simulator_status`
--

LOCK TABLES `stl_simulator_status` WRITE;
/*!40000 ALTER TABLE `stl_simulator_status` DISABLE KEYS */;
INSERT INTO `stl_simulator_status` VALUES (1,'STD Down'),(2,'Yellow'),(3,'Available');
/*!40000 ALTER TABLE `stl_simulator_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_simulator_status_values`
--

DROP TABLE IF EXISTS `stl_simulator_status_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_simulator_status_values` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `simulator_id` int(11) DEFAULT NULL,
  `simulator_status_id` int(11) DEFAULT NULL,
  `datetime_start` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_simulator_status_values`
--

LOCK TABLES `stl_simulator_status_values` WRITE;
/*!40000 ALTER TABLE `stl_simulator_status_values` DISABLE KEYS */;
/*!40000 ALTER TABLE `stl_simulator_status_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_simulators`
--

DROP TABLE IF EXISTS `stl_simulators`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_simulators` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) DEFAULT NULL,
  `ttl` tinyint(1) DEFAULT '0',
  `actual_ttl` decimal(10,1) DEFAULT '0.0',
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_simulators`
--

LOCK TABLES `stl_simulators` WRITE;
/*!40000 ALTER TABLE `stl_simulators` DISABLE KEYS */;
/*!40000 ALTER TABLE `stl_simulators` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_training_type`
--

DROP TABLE IF EXISTS `stl_training_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_training_type` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_training_type`
--

LOCK TABLES `stl_training_type` WRITE;
/*!40000 ALTER TABLE `stl_training_type` DISABLE KEYS */;
INSERT INTO `stl_training_type` VALUES (1,'Training'),(2,'Check'),(3,'Engineering'),(4,'Maintenance'),(5,'Regualar Authority'),(6,'Other (ex. Demo)'),(7,'Compliance Monitoring'),(8,'Safety Monitoring'),(9,'Periodical Test');
/*!40000 ALTER TABLE `stl_training_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stl_users`
--

DROP TABLE IF EXISTS `stl_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stl_users` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(40) NOT NULL,
  `lastname` varchar(40) NOT NULL,
  `phone` varchar(40) NOT NULL,
  `email` varchar(40) NOT NULL,
  `password` varchar(40) NOT NULL,
  `level_id` tinyint(4) NOT NULL DEFAULT '1',
  `photo` blob,
  UNIQUE KEY `id` (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stl_users`
--

LOCK TABLES `stl_users` WRITE;
/*!40000 ALTER TABLE `stl_users` DISABLE KEYS */;
INSERT INTO `stl_users` VALUES (1,'Luca','Mezzolla','+393312593858','luca.mezzolla@urbe.aero','5fcfd41e547a12215b173ff47fdd3739',5,NULL);
/*!40000 ALTER TABLE `stl_users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-01-21 20:38:57
