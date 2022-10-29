DROP TABLE IF EXISTS `property`;
CREATE TABLE `property` (
    `property_id` bigint DEFAULT '0',
    `name` varchar(100) NOT NULL,
    `description` text,
    `images` text,
    `registration_forms` text,
    `auction_minutes` text,
    `contracts` text,
    `created_user` varchar(100) NOT NULL,
    `extra_info` text,
    `updated_date` bigint DEFAULT '0',
    `created_date` bigint DEFAULT '0',
    PRIMARY KEY (`property_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
alter table `property` ADD INDEX idx_name(`name`);
alter table `property` ADD INDEX idx_created_date(`created_date`);