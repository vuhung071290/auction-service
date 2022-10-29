--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account` (
  `domain_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `display_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `is_active` tinyint DEFAULT '0',
  `ip_login` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `last_login` bigint DEFAULT '0',
  `updated_date` bigint DEFAULT '0',
  PRIMARY KEY (`domain_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account`
--

LOCK TABLES `account` WRITE;
/*!40000 ALTER TABLE `account` DISABLE KEYS */;
INSERT INTO `account` VALUES ('admin','Super Admin','admin@gmail.com',1,'0:0:0:0:0:0:0:1',1632838405386,1590045958355);
/*!40000 ALTER TABLE `account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_activity`
--

DROP TABLE IF EXISTS `account_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_activity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `domain_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `timestamp` bigint NOT NULL,
  `action_code` int NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `Index_domain_name` (`domain_name`)
) ENGINE=InnoDB AUTO_INCREMENT=664 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_activity`
--

LOCK TABLES `account_activity` WRITE;
/*!40000 ALTER TABLE `account_activity` DISABLE KEYS */;
/*!40000 ALTER TABLE `account_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `account_permission`
--

DROP TABLE IF EXISTS `account_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `account_permission` (
  `domain_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `feature_action_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `feature_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`domain_name`,`feature_action_id`),
  KEY `FK_account_permission_feature_idx` (`feature_id`),
  KEY `FK_account_permission_feature_action_idx` (`feature_action_id`),
  CONSTRAINT `FK_account_permission_account` FOREIGN KEY (`domain_name`) REFERENCES `account` (`domain_name`),
  CONSTRAINT `FK_account_permission_feature` FOREIGN KEY (`feature_id`) REFERENCES `feature` (`feature_id`),
  CONSTRAINT `FK_account_permission_feature_action` FOREIGN KEY (`feature_action_id`) REFERENCES `feature_action` (`feature_action_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `account_permission`
--

LOCK TABLES `account_permission` WRITE;
/*!40000 ALTER TABLE `account_permission` DISABLE KEYS */;
INSERT INTO `account_permission` VALUES ('admin','ACCOUNT_ACTIVITY_VIEW','ACCOUNT_ACTIVITY'),('admin','ACCOUNT_MNG_ADD','ACCOUNT_MNG'),('admin','ACCOUNT_MNG_DELETE','ACCOUNT_MNG'),('admin','ACCOUNT_MNG_EDIT','ACCOUNT_MNG'),('admin','ACCOUNT_MNG_VIEW','ACCOUNT_MNG'),('admin','PERMISSION_MNG_ADD','PERMISSION_MNG'),('admin','PERMISSION_MNG_DELETE','PERMISSION_MNG'),('admin','PERMISSION_MNG_EDIT','PERMISSION_MNG'),('admin','PERMISSION_MNG_GRANT','PERMISSION_MNG'),('admin','PERMISSION_MNG_VIEW','PERMISSION_MNG');
/*!40000 ALTER TABLE `account_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feature`
--

DROP TABLE IF EXISTS `feature`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feature` (
  `feature_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `feature_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_date` bigint DEFAULT '0',
  PRIMARY KEY (`feature_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feature`
--

LOCK TABLES `feature` WRITE;
/*!40000 ALTER TABLE `feature` DISABLE KEYS */;
INSERT INTO `feature` VALUES ('ACCOUNT_ACTIVITY','Account Activity','View account activity',1616638925033),('ACCOUNT_MNG','Account Management','View and manage account',1616638925033),('PERMISSION_MNG','Permission Management','View and manage permission',1616638925033);
/*!40000 ALTER TABLE `feature` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feature_action`
--

DROP TABLE IF EXISTS `feature_action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `feature_action` (
  `feature_action_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `feature_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `feature_action_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `created_date` bigint DEFAULT '0',
  PRIMARY KEY (`feature_action_id`),
  KEY `Index_feature_id` (`feature_id`),
  CONSTRAINT `FK_feature_action_feature` FOREIGN KEY (`feature_id`) REFERENCES `feature` (`feature_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feature_action`
--

LOCK TABLES `feature_action` WRITE;
/*!40000 ALTER TABLE `feature_action` DISABLE KEYS */;
INSERT INTO `feature_action` VALUES ('ACCOUNT_ACTIVITY_VIEW','ACCOUNT_ACTIVITY','View','View account activity',4),('ACCOUNT_MNG_ADD','ACCOUNT_MNG','Add','Add account',0),('ACCOUNT_MNG_DELETE','ACCOUNT_MNG','Delete','Delete permanently account',3),('ACCOUNT_MNG_EDIT','ACCOUNT_MNG','Edit','Edit account info',2),('ACCOUNT_MNG_VIEW','ACCOUNT_MNG','View','View account list and account info',1),('PERMISSION_MNG_ADD','PERMISSION_MNG','Add','Add permission',5),('PERMISSION_MNG_DELETE','PERMISSION_MNG','Delete','Delete permission',8),('PERMISSION_MNG_EDIT','PERMISSION_MNG','Edit','Edit permission info',7),('PERMISSION_MNG_GRANT','PERMISSION_MNG','Grant','Grant permission to user',9),('PERMISSION_MNG_VIEW','PERMISSION_MNG','View','View permission',6);
/*!40000 ALTER TABLE `feature_action` ENABLE KEYS */;
UNLOCK TABLES;
