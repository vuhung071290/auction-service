--
-- Table structure for table `dgtc_genid_config`
--

DROP TABLE IF EXISTS `dgtc_genid_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dgtc_genid_config` (
  `id` int(11) NOT NULL,
  `buffer_size` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dgtc_genid_config`
--

LOCK TABLES `dgtc_genid_config` WRITE;
/*!40000 ALTER TABLE `dgtc_genid_config` DISABLE KEYS */;
INSERT INTO `dgtc_genid_config` VALUES (0,11);
/*!40000 ALTER TABLE `dgtc_genid_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dgtc_genid_data`
--

DROP TABLE IF EXISTS `dgtc_genid_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dgtc_genid_data` (
  `id` int(11) NOT NULL,
  `last_date` date NOT NULL,
  `count` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dgtc_genid_data`
--

LOCK TABLES `dgtc_genid_data` WRITE;
/*!40000 ALTER TABLE `dgtc_genid_data` DISABLE KEYS */;
INSERT INTO `dgtc_genid_data` VALUES (0,'2021-10-23',11);
/*!40000 ALTER TABLE `dgtc_genid_data` ENABLE KEYS */;
UNLOCK TABLES;
