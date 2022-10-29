--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
    `user_id` bigint NOT NULL DEFAULT '0',
    `is_active` tinyint DEFAULT '0',
    `display_name` varchar(250) COLLATE utf8mb4_unicode_ci DEFAULT '',
    `identity_card_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '',
    `address` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT '',
    `hash_password` text COLLATE utf8mb4_unicode_ci NOT NULL,
    `avatar_url` text COLLATE utf8mb4_unicode_ci,
    `ip_login` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `phone_number` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `sms_token` text COLLATE utf8mb4_unicode_ci,
    `firebase_token` text COLLATE utf8mb4_unicode_ci,
    `role` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT 'user',
    `bank_account` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '',
    `bank_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '',
    `last_login` bigint DEFAULT '0',
    `updated_date` bigint DEFAULT '0',
    `created_date` bigint DEFAULT '0',
    PRIMARY KEY (`user_id`),
    KEY `idx_user_phone_number` (`phone_number`),
    KEY `idx_user_display_name` (`display_name`),
    KEY `idx_user_created_date` (`created_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (211114000000188,1,'kaka thor zz','321321321','kaka địa chỉ v','$2a$10$.uax6j0vHU9R3Cu1ARwlmOjrWyfSSPDbrBdsGKw7bM8z/iPTj4nnS','http://116.118.49.152:8080/api/storage/view-image/2021/11/30/00/211129000000034.jpg','27.74.248.88','+84944149979','','','user','123456780','ngân hàng zz h',1640071037300,1640079428739,1636904306991),(211117000000001,1,'Tô','2','Nguyễn Thị Thập','$2a$10$SlQ2nEOE9TR/XBybMKALTOu0ux934FJ3npSKXZD70/lV2cea9DNIa','http://116.118.49.152:8080/api/storage/view-image/2021/11/30/02/211129000000035.png','0:0:0:0:0:0:0:1','+84829345889','','fVZwYtpYR1SINoYyZa4IXW:APA91bEiP43JrJD6X5M3HJb26z-vKIu4D0lKK5Rh_08SnHGCkeh1kdfvEkA-UUgTxTU_YeQzRoMWOZzXNshGtt0CgcJ7o1C6pCiLitBR80mxz6lzidJb_vdIp1txMVpmzyCjGlLhH8qt','user','abc23','To Quang Dat',1640548823533,1640548823544,1637201027539),(211127000000303,1,'Hùng Lê','321405555','P16, Q8, TPHCM','$2a$10$qDqbEuBMuSSIF.v5Dh0RUeVPyEuf9QknZzqW2vM553843Fb.jlBtq','http://116.118.49.152:8080/api/storage/view-image/2021/12/11/00/211210000000034.png','42.116.182.136','+84396873333','','','user','','',1639838534968,1639838534970,1638076434108),(211203000000007,1,'Tam Huynh','321334675','Tan Thuy, Ba tri, Ben Tre','$2a$10$AmYMwPSKS8lwZtnrK8M08urutK7YcJZZV3MqCraUdIMItqHUJJBRG',NULL,'42.119.155.207','+84772183680','','','user','','',1640148066478,1640148066479,1638525846605),(211203000000147,0,'hh kaka 09','123456789','dong xoai','$2a$10$pS0lbzNoroF3TyDkNq7dHufkHGr9gHVs02K877r23/VtHzfUyp3x6',NULL,'115.78.2.199','+84765335528','135582','','user','','',NULL,1638611042870,1638611042573);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_registration`
--

DROP TABLE IF EXISTS `user_registration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_registration` (
     `user_id` bigint NOT NULL DEFAULT '0',
     `resend_opt_date` bigint DEFAULT '0',
     `resend_opt_times` tinyint DEFAULT '0',
     `max_resend_opt_times` tinyint DEFAULT '3',
     `active_fail_times` tinyint DEFAULT '0',
     `active_fail_date` bigint DEFAULT '0',
     `max_active_fail_times` tinyint DEFAULT '3',
     `login_fail_times` tinyint DEFAULT '0',
     `login_fail_date` bigint DEFAULT '0',
     `max_login_fail_times` tinyint DEFAULT '10',
     `send_password_times` tinyint DEFAULT '0',
     `send_password_date` bigint DEFAULT '0',
     `max_send_password_times` tinyint DEFAULT '10',
     PRIMARY KEY (`user_id`),
     KEY `FK_user_registration_user_id` (`user_id`),
     CONSTRAINT `FK_user_registration_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_registration`
--

LOCK TABLES `user_registration` WRITE;
/*!40000 ALTER TABLE `user_registration` DISABLE KEYS */;
INSERT INTO `user_registration` VALUES (211114000000188,1636904307887,0,3,1,1636904320988,3,0,0,10,3,1636986483491,3),(211117000000001,1637201028918,0,3,0,0,3,0,0,10,2,1637990044146,3),(211127000000303,1638076436237,0,3,0,0,3,0,0,10,0,0,3),(211203000000007,1638525847407,0,3,0,0,3,0,0,10,0,0,3),(211203000000147,1638612405963,3,3,1,1638612728921,3,0,0,10,0,0,3);
/*!40000 ALTER TABLE `user_registration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification` (
    `notification_id` bigint NOT NULL DEFAULT '0',
    `user_id` bigint DEFAULT '0',
    `title` text COLLATE utf8mb4_unicode_ci NOT NULL,
    `message` text COLLATE utf8mb4_unicode_ci NOT NULL,
    `type` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '',
    `is_read` tinyint DEFAULT '0',
    `limit_date` bigint DEFAULT '0',
    `created_date` bigint DEFAULT '0',
    PRIMARY KEY (`notification_id`),
    KEY `FK_notification_user_id` (`user_id`),
    KEY `idx_notification_user_id` (`user_id`),
    KEY `idx_notification_created_date` (`created_date`),
    KEY `idx_notification_limit_date` (`limit_date`),
    CONSTRAINT `FK_notification_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (211203000000034,211117000000001,'Thông báo','Bạn cần thanh toán lệ phí là 100000.0 vào STK: 12345678910, ngân hàng: A Chau, với tin nhắn \'PDG: 211120000000193\' để được tham gia đấu giá tài sản: Xe máy','auction_register_notification',1,1641133365566,1638541365566),(211203000000035,211117000000001,'Thông báo','Bạn cần thanh toán lệ phí là 100000.0 vào STK: 12345678910, ngân hàng: A Chau, với tin nhắn \'PDG: 211120000000191\' để được tham gia đấu giá tài sản: Nhà','auction_register_notification',0,1641133372283,1638541372283),(211203000000036,211117000000001,'Thông báo','Bạn cần thanh toán lệ phí là 100000.0 vào STK: 12345678910, ngân hàng: A Chau, với tin nhắn \'PDG: 211120000000192\' để được tham gia đấu giá tài sản: Tivi Sam Sung','auction_register_notification',0,1641133377054,1638541377054),(211207000000001,211114000000188,'Thông báo','Bạn cần thanh toán lệ phí là 100000.0 vào STK: 12345678910, ngân hàng: A Chau, với tin nhắn \'PDG: 211120000000190\' để được tham gia đấu giá tài sản: Xe hơi','auction_register_notification',1,1641450042086,1638858042086),(211221000000007,211114000000188,'Thông báo','Bạn cần thanh toán lệ phí là 100000.0 vào STK: 12345678910, ngân hàng: A Chau, với tin nhắn \'PDG: 211220000000128\' để được tham gia đấu giá tài sản: Xe hơi','auction_register_notification',0,1642667466785,1640075466785),(211221000000010,211203000000007,'Thông báo','Bạn cần thanh toán lệ phí là 100000.0 vào STK: 12345678910, ngân hàng: A Chau, với tin nhắn \'PDG: 211220000000128\' để được tham gia đấu giá tài sản: Xe hơi','auction_register_notification',1,1642730458452,1640138458452),(211221000000085,211203000000007,'Thông báo','Bạn cần thanh toán lệ phí là 100000.0 vào STK: 12345678910, ngân hàng: A Chau, với tin nhắn \'PDG: 211221000000084\' để được tham gia đấu giá tài sản: Nhà 1','auction_register_notification',0,1642740354613,1640148354613);
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auction_register_notification`
--

DROP TABLE IF EXISTS `auction_register_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auction_register_notification` (
     `notification_id` bigint NOT NULL DEFAULT '0',
     `auction_id` bigint DEFAULT '0',
     PRIMARY KEY (`notification_id`),
     KEY `FK_auction_notification_notification_id` (`notification_id`),
     KEY `FK_auction_register_notification_auction_id` (`auction_id`),
     CONSTRAINT `FK_auction_register_notification_auction_id` FOREIGN KEY (`auction_id`) REFERENCES `auction` (`auction_id`),
     CONSTRAINT `FK_auction_register_notification_notification_id` FOREIGN KEY (`notification_id`) REFERENCES `notification` (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auction_register_notification`
--

LOCK TABLES `auction_register_notification` WRITE;
/*!40000 ALTER TABLE `auction_register_notification` DISABLE KEYS */;
INSERT INTO `auction_register_notification` VALUES (211207000000001,211120000000190),(211203000000035,211120000000191),(211203000000036,211120000000192),(211203000000034,211120000000193),(211221000000007,211220000000128),(211221000000010,211220000000128),(211221000000085,211221000000084);
/*!40000 ALTER TABLE `auction_register_notification` ENABLE KEYS */;
UNLOCK TABLES;