--
-- Table structure for table `bank_info`
--

DROP TABLE IF EXISTS `bank_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_info` (
                             `account_number` varchar(100) NOT NULL,
                             `name_account` varchar(100) NOT NULL,
                             `branch` varchar(100) NOT NULL,
                             `is_active` tinyint DEFAULT '0',
                             `updated_date` bigint DEFAULT '0',
                             `created_date` bigint DEFAULT '0',
                             PRIMARY KEY (`account_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_info`
--

LOCK TABLES `bank_info` WRITE;
/*!40000 ALTER TABLE `bank_info` DISABLE KEYS */;
INSERT INTO `bank_info` VALUES ('12345711456514','Đạt','Vietcombank',0,1640508417799,1640508417799),('123457456514','Đạt','Vietcombank',0,1640508413594,1640508413594),('12345745654','Đạt','Vietcombank',0,1640507403851,1640507403851),('345711456514','AAA','Vietcombank',0,1640797070518,1640508459156);
/*!40000 ALTER TABLE `bank_info` ENABLE KEYS */;
UNLOCK TABLES;
