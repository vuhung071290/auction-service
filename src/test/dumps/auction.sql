DROP TABLE IF EXISTS `auction_register`;
DROP TABLE IF EXISTS `auction`;
CREATE TABLE `auction` (
    `auction_id` bigint DEFAULT '0',
    `name` varchar(100) NOT NULL,
    `description` text,
    `auction_method` int NOT NULL,
    `register_fee` float DEFAULT '0',
    `deposit_fee` float DEFAULT '0',
    `start_price` float DEFAULT '0',
    `step_price` float DEFAULT '0',
    `status` int NOT NULL,
    `user_id_won` bigint DEFAULT '0',
    `start_register_date` bigint DEFAULT '0',
    `end_register_date` bigint DEFAULT '0',
    `start_auction_date` bigint DEFAULT '0',
    `end_auction_date` bigint DEFAULT '0',
    `property_id` bigint DEFAULT '0',
    `created_user` varchar(100) NOT NULL,
    `extra_info` text,
    `updated_date` bigint DEFAULT '0',
    `created_date` bigint DEFAULT '0',
    PRIMARY KEY (`auction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
alter table `auction` ADD INDEX idx_property_id(`property_id`);
alter table `auction` ADD INDEX idx_name(`name`);
alter table `auction` ADD INDEX idx_status(`status`);
alter table `auction` ADD INDEX idx_created_date(`created_date`);
alter table `auction` ADD INDEX idx_start_register_date(`start_register_date`);
alter table `auction` ADD INDEX idx_start_auction_date(`start_auction_date`);

CREATE TABLE `auction_register`(
   `user_id` bigint DEFAULT '0',
   `auction_id` bigint DEFAULT '0',
   `status` int NOT NULL,
   `updated_date` bigint DEFAULT '0',
   `created_date` bigint DEFAULT '0',
   PRIMARY KEY (`user_id`,`auction_id`),
   KEY `FK_auction_register_userId_id` (`user_id`),
   CONSTRAINT `FK_auction_register_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
   KEY `FK_auction_register_auction_id` (`auction_id`),
   CONSTRAINT `FK_auction_register_auction_id` FOREIGN KEY (`auction_id`) REFERENCES `auction` (`auction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `auction_historical`(
   `auction_historical_id` bigint DEFAULT '0',
   `user_id` bigint DEFAULT '0',
   `auction_id` bigint DEFAULT '0',
   `bid_price` float DEFAULT '0',5
   `created_date` bigint DEFAULT '0',
   PRIMARY KEY (`auction_historical_id`),
   KEY `FK_auction_historical_user_id` (`user_id`),
   CONSTRAINT `FK_auction_historical_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
   KEY `FK_auction_historical_auction_id` (`auction_id`),
   CONSTRAINT `FK_auction_historical_auction_id` FOREIGN KEY (`auction_id`) REFERENCES `auction` (`auction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
alter table `auction_historical` ADD INDEX idx_bid_price(`bid_price`);
alter table `auction_historical` ADD INDEX idx_created_date(`created_date`);